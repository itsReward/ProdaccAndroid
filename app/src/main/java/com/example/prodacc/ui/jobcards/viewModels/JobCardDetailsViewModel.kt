package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository

class JobCardDetailsViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val job: Any
):ViewModel() {
    private val _jobCard = mutableStateOf(jobCardRepository.generateJobCards(1).last())
    val jobCard = _jobCard.value
    val jobCardStatusList = jobCardStatusRepository.generateJobCardStatus(jobCard.id)

    val teamExpanded = mutableStateOf(false)

    fun onTeamExpandedToggle(){
        teamExpanded.value = !teamExpanded.value
    }

}