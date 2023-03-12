package ru.aslastin.entity

import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "users")
data class UserEntity(
    val balance: Long,
    @Id val userId: Long
)
