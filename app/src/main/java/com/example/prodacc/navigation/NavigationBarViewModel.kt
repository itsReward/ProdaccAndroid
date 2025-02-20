package com.example.prodacc.navigation

import androidx.lifecycle.ViewModel
import com.prodacc.data.SignedInUserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationBarViewModel @Inject constructor(
    signedInUserManager: SignedInUserManager
) : ViewModel() {
    val currentUser = signedInUserManager.user
    val currentRole = signedInUserManager.role
    val currentEmployee = signedInUserManager.employee

    fun getNavigationItems(): List<String> = when (currentRole.value) {
        is SignedInUserManager.Role.Admin -> {
            listOf("Job Cards", "Vehicles", "Clients", "Employees")
        }
        is SignedInUserManager.Role.Technician -> {
            listOf("Job Cards", "Profile")
        }
        is SignedInUserManager.Role.Supervisor -> {
            listOf("Job Cards", "Vehicles", "Technicians", "Profile")
        }
        else -> {
            listOf("Job Cards", "Vehicles", "Clients", "Profile")
        }
    }

    fun getSelectedItemIndex(currentRoute: String?): Int = when (currentRoute) {
        Route.JobCards.path -> 0
        Route.Vehicles.path -> 1
        Route.Clients.path -> 2
        Route.Employees.path -> {
            if (currentRole.value is SignedInUserManager.Role.Supervisor) {
                2
            } else {
                3
            }
        }
        else -> 4 // Default to Job Cards
    }
}