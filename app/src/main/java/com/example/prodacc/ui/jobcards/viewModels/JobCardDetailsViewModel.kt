package com.example.prodacc.ui.jobcards.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.prodacc.data.repositories.JobCardRepository
import com.prodacc.data.repositories.JobCardStatusRepository
import com.prodacc.data.repositories.TimeSheetRepository
import java.time.LocalDateTime

class JobCardDetailsViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository(),
    private val jobCardStatusRepository: JobCardStatusRepository = JobCardStatusRepository(),
    private val timeSheetRepository: TimeSheetRepository = TimeSheetRepository(),
    private val job: Any
):ViewModel() {
    private val _jobCard = mutableStateOf(jobCardRepository.generateJobCards(1).last())
    val jobCard = _jobCard.value
    val jobCardStatusList = jobCardStatusRepository.generateJobCardStatus(jobCard.id)
    val timesheets = timeSheetRepository.getTimeSheets()

    val teamExpanded = mutableStateOf(false)

    fun onTeamExpandedToggle(){
        teamExpanded.value = !teamExpanded.value
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
}