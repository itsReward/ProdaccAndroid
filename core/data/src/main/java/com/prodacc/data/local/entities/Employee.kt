package com.prodacc.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "employees")
data class Employee (
    @PrimaryKey(autoGenerate = true) val id: UUID,

    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val address: String,
    val rating: Int,
    val role: String,
    val department: String,
    val version: Int = 0


)