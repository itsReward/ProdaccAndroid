package com.example.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "clients")
data class Client (
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val firstName: String,
    val lastName: String,
    val gender: String,
    val jobTitle: String?,
    val company: String?,
    val phoneNumber: String?,
    val address: String?,
    val version: Int = 0

)