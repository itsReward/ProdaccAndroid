package com.example.auth.domain

class UserManager {

    var isLoggedIn = false
    var isAdmin = false

    fun login(username: String, password: String) {
        // Implement login logic
        isLoggedIn = true
        isAdmin = true //checkIfAdmin(username)
    }

    private fun checkIfAdmin(username: String): Boolean {
        // Implement admin check logic
        return true //username == "admin"
    }

    fun logout() {
        isLoggedIn = false
        isAdmin = false
    }

}