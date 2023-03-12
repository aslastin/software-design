package ru.aslastin.event

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "turnstile_exit_events")
data class TurnstileExitEvent(
    val visitId: Long,
    val membershipId: Long,
    val time: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)
