package ru.aslastin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class CompanyAlreadyExistsException(val companyId: Long) : IllegalAccessException() {
    override val message: String
        get() = "company with id=$companyId already exists"
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class CompanyNotFoundException(val companyId: Long) : IllegalAccessException() {
    override val message: String
        get() = "company with id=$companyId not found"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class CompanyNotEnoughStocksException(
    val companyId: Long,
    val requestedStocksCount: Int,
    val actualStocksCount: Int
) : IllegalAccessException() {
    override val message: String
        get() = "companyId=$companyId requestedStocksCount=$requestedStocksCount actualStocksCount=$actualStocksCount"
}
