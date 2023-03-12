package ru.aslastin.dto

import javax.validation.constraints.Min

data class CreateCompanyDto(
    val companyId: Long,
    @field:Min(0) val pricePerStock: Long
)

data class AddStocksDto(
    @field:Min(1) val countStocksToAdd: Int
)

data class CompanyInfoDto(
    val companyId: Long,
    val pricePerStock: Long,
    val countStocks: Int
)

data class BuyStocksDto(
    val companyId: Long,
    val userBuyerId: Long,
    @field:Min(1) val countStocksToBuy: Int,
)
