package com.prodacc.data.remote

import com.prodacc.data.remote.dao.Token

object TokenManager {
    private var token: Token = Token("empty token")

    fun saveToken(newToken: Token) {
        token = newToken
    }

    fun getToken(): Token = token
}