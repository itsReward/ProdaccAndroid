package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.JobCardReport
import java.util.UUID

class JobCardReportRepository {
    private val jobCardReports: List<JobCardReport> = generateJobCardReports(20)


    fun getJobCardReports(): List<JobCardReport> {
        return jobCardReports
    }

    fun getJobCardReport(id: UUID): JobCardReport? {
        return jobCardReports.find { it.reportId == id }
    }

    private fun generateJobCardReports(size: Int): List<JobCardReport> {
        return List(size) {
            JobCardReport(
                reportId = UUID.randomUUID(),
                jobCardId = UUID.randomUUID(),
                employeeId = UUID.randomUUID(),
                reportType = "Type ${it + 1}",
                jobReport = "Report content ${it + 1}"
            )
        }
    }
}