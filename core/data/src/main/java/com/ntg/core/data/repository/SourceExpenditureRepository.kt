package com.ntg.core.data.repository

import com.ntg.core.model.SourceExpenditure
import com.ntg.core.model.SourceWithDetail
import kotlinx.coroutines.flow.Flow

interface SourceExpenditureRepository {

    suspend fun insert(sourceExpenditure: SourceExpenditure)

    suspend fun delete(sourceExpenditure: SourceExpenditure)

    suspend fun getAll(): Flow<List<SourceExpenditure>>

    suspend fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>>
}