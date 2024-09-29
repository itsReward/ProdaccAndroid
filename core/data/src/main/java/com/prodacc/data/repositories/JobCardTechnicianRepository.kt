package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.JobCardTechnician
import java.util.UUID

class JobCardTechnicianRepository {
    private val jobCardTechnicians: List<JobCardTechnician> = generateJobCardTechnicians(20)


    fun getJobCardTechnicians(): List<JobCardTechnician> {
        return jobCardTechnicians
    }

    fun getJobCardTechnician(jobCardId: UUID): JobCardTechnician? {
        return jobCardTechnicians.find { it.jobCardId == jobCardId }
    }

    private fun generateJobCardTechnicians(size: Int): List<JobCardTechnician> {
        return List(size) {
            JobCardTechnician(
                jobCardId = UUID.randomUUID(),
                technicianId = UUID.randomUUID()
            )
        }
    }
}