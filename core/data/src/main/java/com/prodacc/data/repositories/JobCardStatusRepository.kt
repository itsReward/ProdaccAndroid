package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.JobCardStatus
import java.time.LocalDateTime
import java.util.UUID

class JobCardStatusRepository {
    private val status =
        listOf("open", "diagnostics", "approval", "work in progress", "testing", "done", "frozen")

    fun generateJobCardStatus(id: UUID): List<JobCardStatus> {
        return List(6) { index ->
            JobCardStatus(
                statusId = UUID.randomUUID(),
                jobId = id,
                status = status[index],
                createdAt = LocalDateTime.now().plusHours(index.toLong())
            )
        }
    }
}