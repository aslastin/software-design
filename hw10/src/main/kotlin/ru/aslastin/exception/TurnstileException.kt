package ru.aslastin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class VisitIdNotFoundException(val visitId: Long) : IllegalAccessException() {
    override val message: String
        get() = "visitId=$visitId wasn't found"
}

@ResponseStatus(HttpStatus.NOT_FOUND)
class MembershipMismatchedException(val expectedVisitId: Long, val actualVisitId: Long) :
    IllegalAccessException() {
    override val message: String
        get() = "expectedVisitId=$expectedVisitId, actualVisitId=$actualVisitId"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AlreadyExitedException(val visitId: Long) : IllegalAccessException() {
    override val message: String
        get() = "visitId=$visitId has already exited"
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadVisitIntervalException(val enterTime: Long, val endTime: Long) : IllegalAccessException() {
    override val message: String
        get() = "bad time interval [startTime=$enterTime, endTime=$endTime]"
}
