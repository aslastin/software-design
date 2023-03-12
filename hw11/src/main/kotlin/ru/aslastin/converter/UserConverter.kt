package ru.aslastin.converter

import ru.aslastin.dto.UserBalanceDto
import ru.aslastin.dto.UserOfferDto
import ru.aslastin.entity.UserEntity
import ru.aslastin.entity.UserOfferEntity

fun UserEntity.toUserBalanceDto() = UserBalanceDto(userId, balance)

fun UserOfferEntity.toUserOfferDto() =
    UserOfferDto(offerId!!, userSellerId, companyId, pricePerStock, countStocks)
