package ru.aslastin.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import ru.aslastin.event.CreateMembershipEvent
import ru.aslastin.event.ProlongMembershipEvent
import ru.aslastin.event.TurnstileEnterEvent
import ru.aslastin.event.TurnstileExitEvent

interface CreateMembershipRepository : CrudRepository<CreateMembershipEvent, Long>

interface ProlongMembershipRepository : CrudRepository<ProlongMembershipEvent, Long> {
    @Query("SELECT SUM(prolongTime) FROM prolong_membership_events WHERE membershipId = :membershipId")
    fun sumProlongTimeByMembershipId(membershipId: Long): Long
}

interface TurnstileEnterRepository : CrudRepository<TurnstileEnterEvent, Long> {
    fun findAllByMembershipIdAndTimeAfter(
        membershipId: Long,
        timeAfter: Long
    ): List<TurnstileEnterEvent>

    fun findAllByMembershipIdAndVisitIdAfter(
        membershipId: Long,
        visitIdAfter: Long
    ): List<TurnstileEnterEvent>
}

interface TurnstileExitRepository : CrudRepository<TurnstileExitEvent, Long> {
    fun findAllByMembershipIdAndVisitIdAfter(
        membershipId: Long,
        visitIdAfter: Long
    ): List<TurnstileExitEvent>
}
