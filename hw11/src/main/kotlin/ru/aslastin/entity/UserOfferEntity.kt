package ru.aslastin.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "user_offers")
data class UserOfferEntity(
    val userSellerId: Long,
    val companyId: Long,
    val pricePerStock: Long,
    val countStocks: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val offerId: Long? = null
)
