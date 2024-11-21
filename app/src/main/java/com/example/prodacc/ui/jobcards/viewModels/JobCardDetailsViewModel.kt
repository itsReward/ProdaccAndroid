package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prodacc.data.remote.dao.JobCard
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import com.prodacc.data.repositories.TimeSheetRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class JobCardDetailsViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val timeSheetRepository: TimeSheetRepository = TimeSheetRepository(),
    private val job: UUID
):ViewModel() {
    private lateinit var _jobCard: MutableState<JobCard>

    init {
        viewModelScope.launch {
            _jobCard = mutableStateOf(jobCardRepository.getJobCard(job)!!)
        }
    }

    val jobCard = _jobCard.value
    val jobCardStatusList = jobCardStatusRepository.generateJobCardStatus(jobCard.id)
    val timesheets = timeSheetRepository.getTimeSheets()

    val teamExpanded = mutableStateOf(false)

    fun onTeamExpandedToggle(){
        teamExpanded.value = !teamExpanded.value
    }

    fun fetchJobCard(jobCardId: UUID) {

    }


    fun updateDateAndTimeIn(newDateTime: LocalDateTime) {
        _jobCard.value = jobCard.copy(dateAndTimeIn = newDateTime)
    }

    fun updateEstimatedTimeOfCompletion(newDateTime: LocalDateTime) {
        _jobCard.value = jobCard.copy(estimatedTimeOfCompletion = newDateTime)
    }

    fun updateJobCardDeadline(newDateTime: LocalDateTime) {
        _jobCard.value = jobCard.copy(jobCardDeadline = newDateTime)
    }

    fun updateDateAndTimeClosed(newDateTime: LocalDateTime) {
        _jobCard.value = jobCard.copy(dateAndTimeClosed = newDateTime)
    }

    fun updateDateAndTimeFrozen(newDateTime: LocalDateTime) {
        _jobCard.value = jobCard.copy(dateAndTimeFrozen = newDateTime)
    }

    fun updateSupervisor(id : UUID){

    }

    fun updateServiceAdvisor(id : UUID){

    }
}