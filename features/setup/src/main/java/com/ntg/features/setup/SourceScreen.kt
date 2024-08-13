package com.ntg.features.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ntg.core.designsystem.components.AppBar
import com.ntg.core.designsystem.components.BankCard
import com.ntg.core.designsystem.components.BudgetTextField
import com.ntg.core.designsystem.components.ExposedDropdownMenuSample
import com.ntg.core.designsystem.components.TextDivider
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.mybudget.common.LoginEventListener
import com.ntg.core.mybudget.common.SharedViewModel
import com.ntg.mybudget.core.designsystem.R
import kotlin.math.exp

@Composable
fun SourceRoute(
    sharedViewModel: SharedViewModel,
    accountId: Int,
    setupViewModel: SetupViewModel = hiltViewModel()
) {
    sharedViewModel.setExpand.postValue(true)
    sharedViewModel.bottomNavTitle.postValue(stringResource(id = com.ntg.feature.setup.R.string.submit))
    var source by remember {
        mutableStateOf<SourceExpenditure?>(null)
    }

    SourceScreen {
        source = it
    }

    DisposableEffect(Unit) {
        val listener = object : LoginEventListener {
            override fun onLoginEvent() {
                if (source != null){
                    setupViewModel.insertNewSource(source!!)
                }
            }
        }
        sharedViewModel.loginEventListener = listener
        onDispose {
            sharedViewModel.loginEventListener = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SourceScreen(
    onSubmit: (source: SourceExpenditure) -> Unit
) {

    val cardNumber = remember {
        mutableStateOf("")
    }

    val name = remember {
        mutableStateOf("")
    }

    val expire = remember {
        mutableStateOf("")
    }

    val source by remember {
        mutableStateOf<SourceExpenditure?>(null)
    }

//    onSubmit.invoke(
//        SourceExpenditure(
//            accountId = 0,
//            name = name.value,
//        )
//    )

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(id = R.string.source_expenditure)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            ExposedDropdownMenuSample(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            )

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
                label = stringResource(id = R.string.card_number)
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
                label = stringResource(id = R.string.expire)
            )

            Spacer(modifier = Modifier.padding(24.dp))


        }

    }

}