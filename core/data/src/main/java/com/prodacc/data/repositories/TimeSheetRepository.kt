package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.Timesheet
import java.time.LocalDateTime
import java.util.UUID

class TimeSheetRepository {
    private val timesheets: List<Timesheet> = generateTimeSheets(6)


    fun getTimeSheets(): List<Timesheet> {
        return timesheets
    }

    fun getTimeSheet(id: UUID): Timesheet? {
        return timesheets.find { it.id == id }
    }

    private fun generateTimeSheets(size: Int): List<Timesheet> {
        return List(size) {
            Timesheet(
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