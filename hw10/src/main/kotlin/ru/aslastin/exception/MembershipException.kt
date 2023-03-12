package ru.aslastin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class MembershipNotFoundException(val membershipId: Long) : IllegalAccessException() {
    override val message: String
        get() = "membershipId=$membershipId wasn't found"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class MembershipExpiredException(val membershipId: Long, val endTime: Long, val time: Long) :
    IllegalAccessException() {
    override val message: String
        get() = "membershipId=$membershipId expired in $endTime, but was attempt to enter in $time"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class MembershipBadTimeIntervalException(val startTime: Long, val endTime: Long) :
    IllegalAccessException() {
    override val message: String
        get() = "bad time interval [startTime=$startTime, endTime=$endTime]"
}
