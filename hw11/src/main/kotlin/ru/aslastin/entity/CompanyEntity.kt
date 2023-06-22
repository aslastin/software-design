package ru.aslastin.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "companies")
data class CompanyEntity(
    val pricePerStock: Long,
    val countStocks: Int,
    @Id val companyId: Long
)
