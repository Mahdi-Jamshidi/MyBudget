package com.ntg.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.UserDataRepository
import com.ntg.core.data.repository.api.AuthRepository
import com.ntg.core.data.repository.transaction.TransactionsRepository
import com.ntg.core.model.Account
import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceType
import com.ntg.core.model.Transaction
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.network.model.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel
@Inject constructor(
    private val accountRepository: AccountRepository,
    private val sourceRepository: SourceExpenditureRepository,
    private val authRepository: AuthRepository,
    private val userDataRepository: UserDataRepository,
    private val bankCardRepository: BankCardRepository,
    private val transactionRepository: TransactionsRepository
) : ViewModel() {

    val homeUiState = MutableStateFlow(SetupUiState.Loading)

    fun accounts() = accountRepository.getAll()

    fun getAccount(id: Int) = accountRepository.getAccountByAccount(id)

    fun accountWithSources() = accountRepository.getAccountsWithSources()

    fun getSourcesById(id: Int) = sourceRepository.getSourceDetails(id)

    private val _serverAccounts = MutableStateFlow<Result<List<ServerAccount>?>>(Result.Loading(false))
    val serverAccounts: StateFlow<Result<List<ServerAccount>?>> = _serverAccounts

    fun upsertAccount(account: Account) {
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            if (account.dateCreated.orEmpty().isEmpty()){
                account.dateCreated = System.currentTimeMillis().toString()
            }
            accountRepository.update(account)
        }
    }

    fun updateAccount(account: Account){
        viewModelScope.launch {
            account.dateModified = System.currentTimeMillis().toString()
            account.dateCreated = System.currentTimeMillis().toString()
            accountRepository.update(account)
        }
    }

    fun insertNewAccount(
        account: Account
    ){
        viewModelScope.launch {
            accountRepository.insert(account)
        }
    }


    fun insertNewSource(
        source: SourceExpenditure
    ){
        viewModelScope.launch {
            sourceRepository.insert(source)
        }
    }

    fun insertNewBankCard(
        bankCard: SourceType.BankCard
    ){
        viewModelScope.launch {
            bankCardRepository.insert(bankCard)
        }
    }

    fun updateBankCard(bankCard: SourceType.BankCard) {
        viewModelScope.launch {
            bankCardRepository.update(bankCard)
        }
    }

    fun tempRemove(sourceId: Int) {
        viewModelScope.launch {
            sourceRepository.tempRemove(sourceId)
        }
    }

    fun initCardTransactions(
        initAmount: Long,
        sourceId: Int,
        accountId: Int
    ){
        viewModelScope.launch {
            transactionRepository.insertNewTransaction(
                Transaction(
                    0, amount = initAmount, accountId = accountId, sourceId = sourceId, date = System.currentTimeMillis()
                )
            )
        }
    }

    fun serverAccounts(){
        viewModelScope.launch {
            _serverAccounts.value = Result.Loading(true)
            authRepository.serverAccounts()
                .collect { result ->
                    _serverAccounts.value =
                        result
                }
        }
    }

    fun setDefaultAccount(){
        viewModelScope.launch {
            accountRepository.insert(
                Account(
                    id = 0,
                    sId = null,
                    name = "حساب شخصی",
                    dateCreated = System.currentTimeMillis().toString()
                )
            )
        }
    }

    fun logout(){
        viewModelScope.launch {
            userDataRepository.logout()
        }
    }

}

enum class SetupUiState {
    Loading,
    Error,
    Success
}