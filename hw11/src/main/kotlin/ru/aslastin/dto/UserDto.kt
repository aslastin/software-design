package ru.aslastin.dto

import javax.validation.constraints.Min

data class CreateUserDto(
    val userId: Long
)

data class IncreaseUserBalanceDto(
    @field:Min(1) val increaseAmount: Long
)

data class UserBalanceDto(
    val userId: Long,
    val balance: Long
)

data class UserTotalBalanceDto(
    val userId: Long,
    val totalBalance: Long
)

data class PurchasedCompanyStocks(
    val companyId: Long,
    val pricePerStock: Long,
    val countStocks: Int,
)

data class UserStocksDto(
    val userId: Long,
    val purchasedStocks: List<PurchasedCompanyStocks>
)

data class CreateUserOfferDto(
    val userSellerId: Long,
    val companyId: Long,
    @field:Min(0) val pricePerStock: Long,
    @field:Min(1) val countStocks: Int,
)

data class UserOfferDto(
    val offerId: Long,
    val userSellerId: Long,
    val companyId: Long,
    val pricePerStock: Long,
    val countStocks: Int,
)

data class BuyUserOfferDto(
    val offerId: Long,
    val userBuyerId: Long
)
