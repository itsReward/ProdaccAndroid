package com.prodacc.data.remote.dao

import java.time.LocalDateTime
import java.util.UUID

data class TimeSheet(
    val id: UUID,
    val sheetTitle: String,
    val report: String,
    val clockInDateAndTime: LocalDateTime,
    val clockOutDateAndTime: LocalDateTime,
    val jobCardId: UUID,
    val jobCardName: String,
    val technicianId: UUID,
    val technicianName: String
)