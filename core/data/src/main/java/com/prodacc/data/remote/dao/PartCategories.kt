package com.prodacc.data.remote.dao

import java.util.UUID

data class PartCategories(
    val id: UUID,
    val categoryName: String
)

data class NewPartCategories(
    val categoryName: String
)
