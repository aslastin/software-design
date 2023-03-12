package ru.aslastin.event

import javax.persistence.*

@Entity(name = "turnstile_enter_events")
@SequenceGenerator(
    allocationSize = 1,
    name = "visit_id_seq",
    sequenceName = "visit_id_seq"
)
data class TurnstileEnterEvent(
    val membershipId: Long,
    val time: Long,
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "visit_id_seq")
    val visitId: Long? = null
)
