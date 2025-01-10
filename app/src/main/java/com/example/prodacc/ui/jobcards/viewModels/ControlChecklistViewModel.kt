package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.SignedInUser
import com.prodacc.data.remote.dao.ControlChecklist
import com.prodacc.data.remote.dao.User
import com.prodacc.data.repositories.ControlChecklistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class ControlChecklistViewModel(
    private val controlChecklistRepository: ControlChecklistRepository = ControlChecklistRepository(),
    private val signedInUser: User = SignedInUser.user!!,
    private val jobCardId: String
): ViewModel() {
    private val _controlChecklist = MutableStateFlow<ControlChecklist?>(null)
    val controlChecklist = _controlChecklist.asStateFlow()

    private val _loadingState = MutableStateFlow<ControlChecklistLoadingState>(ControlChecklistLoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchControlChecklist()
        }
    }


    private suspend fun fetchControlChecklist(){
        _loadingState.value = ControlChecklistLoadingState.Loading
        try {
            when (val response = controlChecklistRepository.getControlChecklist(UUID.fromString(jobCardId))){
                is ControlChecklistRepository.LoadingResult.Error -> _loadingState.value = ControlChecklistLoadingState.Error(response.message)
                is ControlChecklistRepository.LoadingResult.Loading -> _loadingState.value = ControlChecklistLoadingState.Loading
                is ControlChecklistRepository.LoadingResult.Success -> {
                    _controlChecklist.value = response.data
                    _loadingState.value = ControlChecklistLoadingState.Success
                }
            }

        } catch (e: Exception){
            when (e){
                is IOException -> _loadingState.value = ControlChecklistLoadingState.Error("Network Error")
                else -> _loadingState.value = ControlChecklistLoadingState.Error("Unknown Error")
            }
        }
    }




    open class ControlChecklistLoadingState{
        data object Idle : ControlChecklistLoadingState()
        data object Loading : ControlChecklistLoadingState()
        data object Success : ControlChecklistLoadingState()
        data class Error(val message: String) : ControlChecklistLoadingState()

    }

}


class ControlChecklistViewModelFactory(private val jobCardId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(ControlChecklistViewModel::class.java)) {
            return ControlChecklistViewModel(jobCardId = jobCardId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}