package ru.aslastin.dto

import org.springframework.http.ResponseEntity

// Queries && DTOs

data class DayToCountQuery(
    val membershipId: Long
)

data class DayToCountDto(
    val membershipId: Long,
    val dayToCount: Map<String, Int>
)

data class AvgFrequencyQuery(
    val membershipId: Long
)

data class AvgFrequencyDto(
    val membershipId: Long,
    val result: Double
)

data class AvgDurationQuery(
    val membershipId: Long
)

data class AvgDurationDto(
    val membershipId: Long,
    val result: Double
)
