package ru.aslastin.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "user_stocks")
data class UserStocksEntity(
    val userId: Long,
    val companyId: Long,
    val countStocks: Int,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)
