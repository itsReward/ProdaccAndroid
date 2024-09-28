package com.prodacc.data.remote.dao

import java.time.LocalDateTime
import java.util.UUID

data class StateChecklist(
    val id: UUID,
    val jobCardId: UUID,
    val jobCardName: String,
    val technicianId: UUID,
    val technicianName: String,
    val created: LocalDateTime,
    val checklist: Map<String, String>
)
