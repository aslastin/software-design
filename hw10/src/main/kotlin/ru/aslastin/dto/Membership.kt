package ru.aslastin.dto

import javax.validation.constraints.Min

// Commands && DTOs

data class CreateMembershipCommand(
    @field:Min(1)
    val startTime: Long,
    @field:Min(1)
    var endTime: Long
)

data class ProlongMembershipDto(
    @field:Min(1)
    val prolongTime: Long
)

data class ProlongMembershipCommand(
    val membershipId: Long,
    val prolongTime: Long
)

// Queries && DTOs

data class MembershipInfoQuery(
    val membershipId: Long
)

data class StartTimeQuery(
    val membershipId: Long
)

data class MembershipInfoDto(
    val membershipId: Long,
    val startTime: Long,
    val endTime: Long
)
