package com.ntg.features.setup

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BankCard
import com.ntg.core.designsystem.components.BudgetButton
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ButtonSize
import com.ntg.core.designsystem.components.ExposedDropdownMenuSample
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.designsystem.components.WheelList
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceTypes
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.core.mybudget.common.generateUniqueFiveDigitId
import com.ntg.mybudget.core.designsystem.R
import kotlinx.coroutines.launch

@Composable
fun SourceRoute(
    sharedViewModel: SharedViewModel,
    accountId: Int,
    sourceId: Int? = null,
    setupViewModel: SetupViewModel = hiltViewModel(),
    onShowSnackbar: suspend (Int, String?) -> Boolean,
    onBack:() -> Unit
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = com.ntg.feature.setup.R.string.submit))
    var source by remember {
        mutableStateOf<SourceExpenditure?>(null)
    }
    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }

    val editSource = setupViewModel.getSourcesById(sourceId ?: -1).collectAsStateWithLifecycle(
        initialValue = null
    ).value

    SourceScreen(editSource, onBack = onBack) { sourceValue, card ->
        source = sourceValue
        bankCard = card
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = bankCard) {
        sharedViewModel.loginEventListener = object : LoginEventListener {
            override fun onLoginEvent() {

                if (bankCard?.number.orEmpty().isEmpty()){
                    scope.launch {
                        onShowSnackbar(R.string.err_empty_card_number, null)
                    }
                    return
                }else if (bankCard?.number.orEmpty().length != 16){
                    scope.launch {
                        onShowSnackbar(R.string.err_length_number, null)
                    }
                    return
                }

                if (bankCard?.name.orEmpty().isEmpty()){
                    scope.launch {
                        onShowSnackbar(R.string.err_empty_name, null)
                    }
                    return
                }

                if (editSource != null){
                    bankCard?.sourceId = sourceId
                    setupViewModel.updateBankCard(bankCard!!)
                    onBack()
                } else if (source != null && bankCard != null){
                    source?.accountId = accountId
                    bankCard?.sourceId = source?.id
                    setupViewModel.insertNewSource(source!!)
                    setupViewModel.insertNewBankCard(bankCard!!)
                    onBack()
                }else{
                    scope.launch {
                        onShowSnackbar(com.ntg.feature.setup.R.string.err_in_submit, null)
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceScreen(
    editSource: SourceWithDetail?,
    onBack:()-> Unit,
    onSubmit: (source: SourceExpenditure, card: SourceType.BankCard?) -> Unit
) {


    var sourceType by remember {
        mutableStateOf("")
    }

    var bankCard by remember {
        mutableStateOf<SourceType.BankCard?>(null)
    }

    var editMode by remember {
        mutableStateOf(false)
    }

    onSubmit.invoke(
        SourceExpenditure(
            id = generateUniqueFiveDigitId(),
            accountId = 0,
            name = "تومن",
            symbol = "ت",
            isSelected = false,
            type = SourceTypes.BankCard.ordinal,
            dateCreated = System.currentTimeMillis().toString()
        ),
        bankCard
    )


    if (editSource != null){
        editMode = true
        when(editSource.sourceType){
            is SourceType.BankCard -> sourceType = stringResource(id = R.string.bank_card)
            is SourceType.Gold -> sourceType = stringResource(id = R.string.gold)
            null -> editMode = false
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBar(
                title = stringResource(id = R.string.source_expenditure),
                scrollBehavior = scrollBehavior,
                navigationOnClick = {
                    onBack.invoke()
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            if (!editMode){
                ExposedDropdownMenuSample(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp)
                ){
                    sourceType = it
                }
            }


            when(sourceType){

                stringResource(id = R.string.bank_card) -> {

                    BankCardView(
                        if (editMode) editSource?.sourceType as SourceType.BankCard else null
                    ){
                        bankCard = it
                    }
                }

                stringResource(id = R.string.foreign_currency) -> {

                }

                stringResource(id = R.string.gold) -> {

                }

            }


        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BankCardView(
    editBankCard: SourceType.BankCard? = null,
    bankCard: (SourceType.BankCard) -> Unit
){

    val concurrency = remember {
        mutableStateOf("تومن")
    }

    val name = remember {
        mutableStateOf("")
    }

    val expire = remember {
        mutableStateOf("")
    }

    var month by remember {
        mutableIntStateOf(0)
    }

    var year by remember {
        mutableIntStateOf(0)
    }

    val cardNumber = remember {
        mutableStateOf("")
    }

    bankCard(
        SourceType.BankCard(
            id = editBankCard?.id ?: generateUniqueFiveDigitId(),
            number = cardNumber.value,
            date = "$year/$month",
            name = name.value,
            updatedAt = System.currentTimeMillis().toString()
        )
    )

    LaunchedEffect(key1 = editBankCard) {
        if (editBankCard != null && cardNumber.value.isEmpty()){
            name.value = editBankCard.name
            cardNumber.value = editBankCard.number
            if (editBankCard.date.isNotEmpty()){
                year = editBankCard.date.split("/")[0].toInt()
                month = editBankCard.date.split("/")[1].toInt()
                expire.value = editBankCard.date
            }
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    if (editBankCard == null){
        BudgetTextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            text = concurrency,
            label = stringResource(id = R.string.concurrency),
            readOnly = true
        )
    }

    TextDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        title = stringResource(id = R.string.bank_card)
    )



    BankCard(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        cardNumber = cardNumber.value,
        name = name.value,
        amount = "0",
        expiringDate = expire.value
    )


    TextDivider(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        title = stringResource(id = R.string.card_info)
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = cardNumber,
        label = stringResource(id = R.string.card_number),
        length = 16,
        keyboardType = KeyboardType.NumberPassword
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = name,
        label = stringResource(id = R.string.first_last_name)
    )

    BudgetTextField(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        text = expire,
        label = stringResource(id = R.string.expire),
        readOnly = true,
        onClick = {
            showBottomSheet = true
        }
    )

    Spacer(modifier = Modifier.padding(24.dp))

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {

            Column {

                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = stringResource(id = R.string.select_expire_date), style = MaterialTheme.typography.titleSmall)

                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 32.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (1..12).toList(),
                        initialItem = month,
                        onItemSelected = { i, item ->
                            month = item
                            expire.value = "$year/$month"
                        }
                    )

                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = "/")

                    WheelList(
                        modifier = Modifier.weight(1f),
                        items = (1403..1408).toList(),
                        initialItem = year,
                        onItemSelected = { i, item ->
                            year = item
                            expire.value = "$year/$month"
                        }
                    )
                }

                BudgetButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    text = stringResource(id = com.ntg.feature.setup.R.string.submit),
                    size = ButtonSize.MD){
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }

            }

        }
    }
}

