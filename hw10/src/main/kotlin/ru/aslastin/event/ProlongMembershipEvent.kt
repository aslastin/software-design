package ru.aslastin.event

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity(name = "prolong_membership_events")
data class ProlongMembershipEvent(
    val membershipId: Long,
    val prolongTime: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)
