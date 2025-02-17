package com.prodacc.data.remote

import com.prodacc.data.remote.dao.Token
import java.lang.ref.WeakReference

object TokenManager {
    private var token: Token? = null

    fun saveToken(newToken: Token?) {
        token = newToken
        notifyTokenChange()
    }

    interface TokenListener{
        fun onTokenUpdate()
    }

    private var tokenChangeListeners = mutableListOf<WeakReference<TokenListener>>()

    fun addTokenChangeListeners(listener: TokenListener) {
        tokenChangeListeners.add(WeakReference(listener))
    }

    fun removeTokenChangeListener(listener: TokenListener){
        tokenChangeListeners.removeAll {it.get() == listener}
    }

    private fun notifyTokenChange(){
        tokenChangeListeners.removeAll{ it.get() == null }
        tokenChangeListeners.forEach {
            it.get()?.onTokenUpdate()
        }
    }

    fun getToken(): Token? = token
}