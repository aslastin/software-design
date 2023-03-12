package ru.aslastin.dto

// Commands && DTOs

data class EnterTurnstileCommand(
    val membershipId: Long,
    val time: Long
)

data class EnterTurnstileDto(
    val visitId: Long,
    val membershipId: Long,
    val time: Long
)

data class ExitTurnstileCommand(
    val visitId: Long,
    val membershipId: Long,
    val time: Long
)
