package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class ServerAccount(
    val id: String? = null,
    val name: String? = null,
    @SerializedName("owner_id")
    val ownerId:String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("deleted_at")
    val deletedAt: String? = null,
    val wallets: List<WalletRes>? = null,
)


data class WalletRes(
    val id: String? = null,
    @SerializedName("wallet_type_id")
    val walletType:Int? = null,
    @SerializedName("currency_id")
    val currencyId: Int,
    @SerializedName("account_id")
    val accountId: String? = null,
    val details: WalletDetailsRes,
    )

data class WalletDetailsRes(
    @SerializedName("bank_id")
    val bankId: Int? = null,
    @SerializedName("bank_name")
    val bankName: String?= null,
    @SerializedName("cart_number")
    val cardNumber: String? = null,
    @SerializedName("cart_owner_name")
    val cardOwner: String? = null,
    val cvv2: String? = null,

)