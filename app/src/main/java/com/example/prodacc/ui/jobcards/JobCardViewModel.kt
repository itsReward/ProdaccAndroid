package com.example.prodacc.ui.jobcards

import androidx.lifecycle.ViewModel
import com.prodacc.data.repositories.JobCardRepository

class JobCardViewModel(
    private val jobCardRepository: JobCardRepository = JobCardRepository()
): ViewModel(){
    val jobCards = jobCardRepository.generateJobCards(10)
    val pastJobCards = jobCardRepository.generateJobCards(5)

}