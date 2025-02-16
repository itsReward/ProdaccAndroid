package com.prodacc.data.repositories

import android.util.Log
import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardComment
import com.prodacc.data.remote.dao.NewComment
import com.prodacc.data.remote.dao.UpdateComment
import java.io.IOException
import java.util.UUID

class CommentRepository {
    private val service = ApiInstance.commentService

    suspend fun getCommentById(id: UUID): LoadingResult {
        return try {
            val response = service.getCommentByCommentId(id)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    return LoadingResult.SingleEntity(null, "Comment not found")
                } else {
                    return LoadingResult.SingleEntity(response.body(), null)
                }
            } else {
                return LoadingResult.Error(response.raw().message)
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getCommentsByJobCardId(id: UUID): LoadingResult {
        return try {
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

    suspend fun getComments(): LoadingResult {
        return try {
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

    suspend fun createComment(newComment: NewComment): LoadingResult {
        return try {
            val response = service.newComment(newComment)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.ErrorSingleMessage("Comment creation failed")
                } else {
                    LoadingResult.Success(response.body() ?: emptyList())
                }
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

    suspend fun updateComment(id: UUID, updatedComment: UpdateComment): LoadingResult {
        return try {
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

    suspend fun deleteComment(id: UUID): LoadingResult {
        return try {
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
