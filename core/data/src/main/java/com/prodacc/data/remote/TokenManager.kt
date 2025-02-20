package com.prodacc.data.remote

import com.prodacc.data.remote.dao.Token
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor() {
    private var token: Token? = null
    private var tokenChangeListeners = mutableListOf<WeakReference<TokenListener>>()

    fun saveToken(newToken: Token?) {
        token = newToken
        notifyTokenChange()
    }

    interface TokenListener {
        fun onTokenUpdate()
    }

    fun addTokenChangeListeners(listener: TokenListener) {
        tokenChangeListeners.add(WeakReference(listener))
    }

    fun removeTokenChangeListener(listener: TokenListener) {
        tokenChangeListeners.removeAll { it.get() == listener }
    }

    private fun notifyTokenChange() {
        tokenChangeListeners.removeAll { it.get() == null }
        tokenChangeListeners.forEach {
            it.get()?.onTokenUpdate()
        }
    }

    fun getToken(): Token? = token
}