package com.prodacc.data.remote.dao

import java.util.UUID

data class JobCardReport(
    val reportId: UUID,
    val jobCardId: UUID,
    val employeeId: UUID,
    val reportType: String,
    val jobReport: String
)
