package ru.aslastin.converter

import ru.aslastin.dto.CompanyInfoDto
import ru.aslastin.entity.CompanyEntity

fun CompanyEntity.toCompanyInfoDto() = CompanyInfoDto(companyId, pricePerStock, countStocks)
