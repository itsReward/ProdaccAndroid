package com.example.prodacc.ui.jobcards.viewModels

import com.prodacc.data.remote.dao.JobCard
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<JobCardEvent>()
    val events = _events.asSharedFlow()

    private val _crudEvents = MutableSharedFlow<JobCardCRUDEvent>()
    val crudEvents = _crudEvents.asSharedFlow()

    suspend fun emit(event: JobCardEvent) {
        _events.emit(event)
    }

    suspend fun emit(event: JobCardCRUDEvent) {
        _crudEvents.emit(event)
    }

    sealed class JobCardEvent {
        data object StatusChanged : JobCardEvent()
        data class Error(val message: String) : JobCardEvent()
    }

    sealed class JobCardCRUDEvent {
        data class JobCardCreated(val jobCard : JobCard) : JobCardCRUDEvent()
        data object JobCardDeleted : JobCardCRUDEvent()

    }
}