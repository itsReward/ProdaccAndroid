package com.example.prodacc.ui.jobcards.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCardComment
import com.prodacc.data.repositories.CommentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

class CommentsViewModel(
    private val commentRepository: CommentRepository = CommentRepository(),
    private val jobId: String
) : ViewModel(), WebSocketInstance.WebSocketEventListener{
    private val _comments = MutableStateFlow<List<JobCardComment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _savingState = MutableStateFlow<SavingState>(SavingState.Idle)
    val savingState = _savingState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState = _updateState.asStateFlow()



    init {
        WebSocketInstance.addWebSocketListener(this)

        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            fetchComments()
        }
    }

    private suspend fun fetchComments() {
        try {
            val jobId = UUID.fromString(jobId)
            when (val comments = commentRepository.getCommentsByJobCardId(jobId)){
                is CommentRepository.LoadingResult.Error -> _loadingState.value = LoadingState.Error(comments.message?: "Unknown error")
                is CommentRepository.LoadingResult.ErrorSingleMessage -> _loadingState.value = LoadingState.Error(
                    comments.message
                )
                is CommentRepository.LoadingResult.NetworkError -> _loadingState.value = LoadingState.Error("Network Error")
                is CommentRepository.LoadingResult.SingleEntity -> {}
                is CommentRepository.LoadingResult.Success -> {
                    _comments.value = comments.comments
                    _loadingState.value = LoadingState.Idle
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun refreshComments(){
        _updateState.value = UpdateState.Updating
        viewModelScope.launch {
            fetchComments()
        }
    }

    private fun handleException(e : Exception){
        when(e){
            is IOException -> _loadingState.value = LoadingState.Error("Connection Error")
            else -> _loadingState.value =   LoadingState.Error(e.message?:"Unknown Error occurred")
        }
    }

    sealed class LoadingState{
        data object Idle : LoadingState()
        data object Loading: LoadingState()
        data class Error(val message : String): LoadingState()
    }

    sealed class SavingState{
        data object Idle : SavingState()
        data object Saving : SavingState()
        data class Error(val message : String): SavingState()
    }

    sealed class UpdateState{
        data object Idle : UpdateState()
        data object Updating : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when(update){
            is WebSocketUpdate.DeleteComment -> refreshComments()
            is WebSocketUpdate.NewComment -> refreshComments()
            is WebSocketUpdate.UpdateComment -> refreshComments()
            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        _updateState.value = UpdateState.Error(error.message?:"Unknown Error Refresh")
    }
}

class CommentsViewModelFactory(private val jobId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(jobId = jobId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}