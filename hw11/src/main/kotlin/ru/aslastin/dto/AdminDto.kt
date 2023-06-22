package ru.aslastin.dto

import javax.validation.constraints.Min

data class ChangePricePerStockDto(
    @field:Min(0) val newPricePerStock: Long
)
