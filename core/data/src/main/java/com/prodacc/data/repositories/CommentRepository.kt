package com.prodacc.data.repositories

import android.util.Log
import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.JobCardComment
import com.prodacc.data.remote.dao.NewComment
import com.prodacc.data.remote.dao.UpdateComment
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
)
{
    private val service get() = apiServiceContainer.commentService

    suspend fun getCommentById(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.getCommentByCommentId(id)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.SingleEntity(null, "Comment not found")
                } else {
                    LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getCommentsByJobCardId(id: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getCommentByJobCardId(id)
            Log.d("Get Comments Response", response.message())

            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getComments(): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getComments()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun createComment(newComment: NewComment): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.newComment(newComment)
            if (response.isSuccessful) {
                response.body()?.let { comments ->
                    if (comments.isNotEmpty()) {
                        LoadingResult.SingleEntity(comments.first(), null)
                    } else {
                        LoadingResult.ErrorSingleMessage("Comment creation returned no data")
                    }
                } ?: LoadingResult.ErrorSingleMessage("Comment creation failed")
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Comment creation error")
            }
        }
    }

    suspend fun updateComment(id: UUID, updatedComment: UpdateComment): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.updateComment(id, updatedComment)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.ErrorSingleMessage("Comment update failed")
                } else {
                    LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Comment update error")
            }
        }
    }

    suspend fun deleteComment(id: UUID): LoadingResult = withContext(dispatcher.io){
        try {
            val response = service.deleteComment(id)
            if (response.isSuccessful) {
                LoadingResult.Success(emptyList())
            } else {
                LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Comment deletion error")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val comments: List<JobCardComment>, val message: String? = null) :
            LoadingResult()

        data class Error(val message: String?) : LoadingResult()
        data class ErrorSingleMessage(val message: String) : LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val comment: JobCardComment?, val error: String?) : LoadingResult()
    }
}
