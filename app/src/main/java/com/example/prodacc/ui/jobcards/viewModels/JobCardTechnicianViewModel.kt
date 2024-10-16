package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import com.prodacc.data.repositories.JobCardTechnicianRepository
import java.util.UUID

class JobCardTechnicianViewModel(
    private val jobCardTechnicianRepository: JobCardTechnicianRepository = JobCardTechnicianRepository()
): ViewModel() {
    private val jobCardTechnicians = jobCardTechnicianRepository.getJobCardTechnicians(UUID.randomUUID())
}