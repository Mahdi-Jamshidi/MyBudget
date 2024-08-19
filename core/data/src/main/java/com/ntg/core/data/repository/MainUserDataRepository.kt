package com.ntg.core.data.repository

import com.ntg.core.model.UserData
import com.ntg.mybudget.core.datastore.BudgetPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MainUserDataRepository @Inject constructor(
    private val budgetPreferencesDataSource: BudgetPreferencesDataSource,
) : UserDataRepository{

    override val userData: Flow<UserData> =
        budgetPreferencesDataSource.userData

    override suspend fun setUserLogged() {
        budgetPreferencesDataSource.setUserLogged()
    }
}