package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName

data class UserLogInDetails(
    val username: String,
    val password: String
)

data class Token(
    @SerializedName("accessToken")
    val accessToken: String
)