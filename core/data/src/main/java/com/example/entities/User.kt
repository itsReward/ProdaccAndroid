package com.example.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import java.util.UUID

@Fts4
@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val username: String,
    val password: String,
    val email: String,
    val role: String,
    val employeeId: UUID?,
    val version: Int = 0
)