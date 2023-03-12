package ru.aslastin.event

import javax.persistence.*

@Entity(name = "create_membership_events")
@SequenceGenerator(
    allocationSize = 1,
    name = "membership_id_seq",
    sequenceName = "membership_id_seq"
)
data class CreateMembershipEvent(
    val startTime: Long,
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "membership_id_seq")
    val membershipId: Long? = null,
)
