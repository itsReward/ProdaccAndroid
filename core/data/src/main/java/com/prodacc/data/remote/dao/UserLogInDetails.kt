package com.prodacc.data.remote.dao

data class UserLogInDetails(
    val username: String,
    val password: String
)

data class Token(
    val token: String
)