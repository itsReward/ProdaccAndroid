package com.example.navigation

object NavigationContract {
    object Products {
        const val ROUTE = "products"

        // If you need to pass arguments later
        fun createRoute(args: String? = null) = buildString {
            append(ROUTE)
            if (args != null) append("/$args")
        }
    }

    // Other route definitions...
}