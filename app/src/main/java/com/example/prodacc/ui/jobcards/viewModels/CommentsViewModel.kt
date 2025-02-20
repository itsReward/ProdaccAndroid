package com.example.prodacc.ui.jobcards.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prodacc.data.SignedInUserManager
import com.prodacc.data.remote.WebSocketInstance
import com.prodacc.data.remote.WebSocketUpdate
import com.prodacc.data.remote.dao.JobCardComment
import com.prodacc.data.remote.dao.NewComment
import com.prodacc.data.repositories.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val webSocketInstance: WebSocketInstance,
    private val signedInUserManager: SignedInUserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel(), WebSocketInstance.WebSocketEventListener
{
    // Get jobCardId from SavedStateHandle
    private val jobId: String = checkNotNull(savedStateHandle["jobId"]) {
        "jobCardId parameter wasn't found. Please make sure it's passed in the navigation arguments."
    }

    private val _comments = MutableStateFlow<List<JobCardComment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState = _loadingState.asStateFlow()

    private val _savingState = MutableStateFlow<SavingState>(SavingState.Idle)
    val savingState = _savingState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState = _updateState.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment = _comment.asStateFlow()

    private val _jobCardName = MutableStateFlow("")
    val jobCardName = _comment.asStateFlow()


    init {
        webSocketInstance.addWebSocketListener(this)

        _loadingState.value = LoadingState.Loading
        viewModelScope.launch {
            fetchComments()
        }
    }

    fun updateComment(string: String) {
        _comment.value = string

    }

    fun createComment() {
        viewModelScope.launch {
            try {
                val jobId = UUID.fromString(jobId)
                val employeeId = signedInUserManager.employee.value!!.id
                when (val comment = commentRepository.createComment(
                    NewComment(
                        jobId,
                        employeeId,
                        _comment.value
                    )
                )) {
                    is CommentRepository.LoadingResult.Error -> _savingState.value =
                        SavingState.Error(comment.message ?: "unknown error")

                    is CommentRepository.LoadingResult.ErrorSingleMessage -> _savingState.value =
                        SavingState.Error(
                            comment.message
                        )

                    is CommentRepository.LoadingResult.NetworkError -> _savingState.value =
                        SavingState.Error("Network error")

                    is CommentRepository.LoadingResult.SingleEntity -> {
                        _comment.value = ""

                        Log.e("Comment creation successful", "Sending new comment websocket update")

                        refreshComments()
                        _savingState.value = SavingState.Idle
                    }

                    is CommentRepository.LoadingResult.Success -> {

                        Log.e("Comment creation successful", "Susesssssssssssssssssss")
                    }
                }
            } catch (e: Exception) {
                handleException(e)
            } finally {
                _comment.value = ""
                EventBus.emitCommentEvent(EventBus.CommentEvent.CommentCreated)
                webSocketInstance.sendWebSocketMessage(
                    "NEW_COMMENT",
                    jobId
                )
                refreshComments()
            }
        }

    }

    private suspend fun fetchComments() {
        try {
            val jobId = UUID.fromString(jobId)
            when (val comments = commentRepository.getCommentsByJobCardId(jobId)) {
                is CommentRepository.LoadingResult.Error -> _loadingState.value =
                    LoadingState.Error(comments.message ?: "Unknown error")

                is CommentRepository.LoadingResult.ErrorSingleMessage -> _loadingState.value =
                    LoadingState.Error(
                        comments.message
                    )

                is CommentRepository.LoadingResult.NetworkError -> _loadingState.value =
                    LoadingState.Error("Network Error")

                is CommentRepository.LoadingResult.SingleEntity -> {}
                is CommentRepository.LoadingResult.Success -> {
                    _comments.value = comments.comments
                    if (_comments.value.isNotEmpty()) {
                        _jobCardName.value = _comments.value.first().jobCardName
                    }
                    _loadingState.value = LoadingState.Idle
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun refreshComments() {
        _updateState.value = UpdateState.Updating
        viewModelScope.launch {
            _comments.value = emptyList()
            fetchComments()
        }
    }

    private fun handleException(e: Exception) {
        when (e) {
            is IOException -> _loadingState.value = LoadingState.Error("Connection Error")
            else -> _loadingState.value = LoadingState.Error(e.message ?: "Unknown Error occurred")
        }
    }

    sealed class LoadingState {
        data object Idle : LoadingState()
        data object Loading : LoadingState()
        data class Error(val message: String) : LoadingState()
    }

    sealed class SavingState {
        data object Idle : SavingState()
        data object Saving : SavingState()
        data class Error(val message: String) : SavingState()
    }

    sealed class UpdateState {
        data object Idle : UpdateState()
        data object Updating : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    override fun onWebSocketUpdate(update: WebSocketUpdate) {
        when (update) {
            is WebSocketUpdate.DeleteComment -> refreshComments()
            is WebSocketUpdate.NewComment -> refreshComments()
            is WebSocketUpdate.UpdateComment -> refreshComments()
            else -> {}
        }
    }

    override fun onWebSocketError(error: Throwable) {
        _updateState.value = UpdateState.Error(error.message ?: "Unknown Error Refresh")
    }

    fun deleteComment(uuid: UUID) {
        viewModelScope.launch {
            commentRepository.deleteComment(uuid)
            EventBus.emitCommentEvent(EventBus.CommentEvent.CommentDeleted)
            webSocketInstance.sendWebSocketMessage("DELETE_COMMENT", jobId)
            refreshComments()
        }
    }
}

