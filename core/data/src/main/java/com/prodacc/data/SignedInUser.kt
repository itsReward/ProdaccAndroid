package com.prodacc.data

import com.prodacc.data.remote.dao.User
import com.prodacc.data.repositories.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object SignedInUser{

    var user: User? = null

    suspend fun initialize(signedIn: String): UserSignInResult {
        return when (val signedInUser = UserRepository().getUserByUsername(signedIn)){
            is UserRepository.LoadingResult.Error -> {
                UserSignInResult.Error(signedInUser.message)
            }
            is UserRepository.LoadingResult.NetworkError -> {
                UserSignInResult.Error("Network Error")
            }
            is UserRepository.LoadingResult.Success -> {
                UserSignInResult.Error("Returned List, Can never happen anyways")
            }
            is UserRepository.LoadingResult.UserEntity -> {
                user = signedInUser.user
                UserSignInResult.Success
            }
        }

    }

    sealed class UserSignInResult{
        data class Error(val message: String): UserSignInResult()
        data object Success : UserSignInResult()
    }


}