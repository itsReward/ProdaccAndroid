package com.example.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "vehicles")
data class Vehicle (
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val clientId: UUID,
    val model: String,
    val regNumber: String,
    val make: String,
    val color: String,
    val chassisNumber: String,
    val version: Int = 0
)