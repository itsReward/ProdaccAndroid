package com.prodacc.data.remote

import com.prodacc.data.remote.dao.Token

object TokenManager {
    private var token: Token? = null

    fun saveToken(newToken: Token?) {
        token = newToken
    }

    fun getToken(): Token? = token
}