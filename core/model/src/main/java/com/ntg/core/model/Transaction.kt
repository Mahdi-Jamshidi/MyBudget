package com.ntg.core.model

data class Transaction(
    val id: Int,
    val sId: String? = null,
    val accountId: Int,
    val sourceId: Int,
    val categoryId: Int? = null,
    val name: String? = null,
    val amount: Long,
    val type: String? = null,
    val date: Long,
    val note: String? = null
)
