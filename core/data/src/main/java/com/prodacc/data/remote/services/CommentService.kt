package com.prodacc.data.remote.services

import com.prodacc.data.remote.dao.JobCardComment
import com.prodacc.data.remote.dao.NewComment
import com.prodacc.data.remote.dao.UpdateComment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface CommentService {
    @GET("/comments/all")
    fun getComments(): Response<List<JobCardComment>>

    @GET("/comments/get/{id}")
    fun getCommentByCommentId(@Path("id") id: UUID): Response<JobCardComment>

    @GET("/comments/get/jobCard/{id}")
    fun getCommentByJobCardId(@Path("id") id: UUID): Response<List<JobCardComment>>

    @POST("/comments/new")
    fun newComment(@Body comment: NewComment): Response<List<JobCardComment>>

    @PUT("/comments/update/{id}")
    fun updateComment(@Path("id") id: UUID, @Body comment: UpdateComment): Response<JobCardComment>

    @DELETE("/comments/delete/{id}")
    fun deleteComment(@Path("id") id: UUID) : Response<Boolean>
}