package ru.aslastin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserAlreadyExistsException(val userId: Long) : IllegalAccessException() {
    override val message: String
        get() = "user with id=$userId already exists"
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFoundException(val userId: Long) : IllegalAccessException() {
    override val message: String
        get() = "user with id=$userId not found"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserNotEnoughBalanceException(
    val userId: Long,
    val expectedBalance: Long,
    val actualBalance: Long
) : IllegalAccessException() {
    override val message: String
        get() = "user=$userId expectedBalance=$expectedBalance actualBalance=$actualBalance"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UserNotEnoughStocksCountException(
    val userId: Long,
    val expectedStocksCount: Int,
    val actualStocksCount: Int
) : IllegalAccessException() {
    override val message: String
        get() = "userId=$userId expectedStocksCount=$expectedStocksCount actualStocksCount=$actualStocksCount"
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserOfferNotFoundException(val offerId: Long) : IllegalAccessException() {
    override val message: String
        get() = "offer with id=$offerId wasn't found"
}
