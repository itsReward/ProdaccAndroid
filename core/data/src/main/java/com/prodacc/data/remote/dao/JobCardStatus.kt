package com.prodacc.data.remote.dao

import java.time.LocalDateTime
import java.util.UUID

data class JobCardStatus (
    val statusId : UUID,
    val jobId : UUID,
    val status : String,
    val createdAt : LocalDateTime
)