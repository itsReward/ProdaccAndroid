package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.TimeSheet
import java.time.LocalDateTime
import java.util.UUID

class TimeSheetRepository {
    private val timeSheets: List<TimeSheet> = generateTimeSheets(20)


    fun getTimeSheets(): List<TimeSheet> {
        return timeSheets
    }

    fun getTimeSheet(id: UUID): TimeSheet? {
        return timeSheets.find { it.id == id }
    }

    private fun generateTimeSheets(size: Int): List<TimeSheet> {
        return List(size) {
            TimeSheet(
                id = UUID.randomUUID(),
                sheetTitle = "Time Sheet ${it + 1}",
                report = "Report content ${it + 1}",
                clockInDateAndTime = LocalDateTime.now(),
                clockOutDateAndTime = LocalDateTime.now().plusHours(8),
                jobCardId = UUID.randomUUID(),
                jobCardName = "Job Card ${it + 1}",
                technicianId = UUID.randomUUID(),
                technicianName = "Technician ${it + 1}"
            )
        }
    }
}