package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Vehicle (
    @SerializedName("id")
    val id : UUID,

    @SerializedName("model")
    val model : String,

    @SerializedName("regNumber")
    val regNumber : String,

    @SerializedName("make")
    val make : String,

    @SerializedName("color")
    val color : String,

    @SerializedName("chassisNumber")
    val chassisNumber: String,

    @SerializedName("clientId")
    val clientId: UUID,

    @SerializedName("clientName")
    val clientName : String,

    @SerializedName("clientSurname")
    val clientSurname : String

)

data class NewVehicle (

    @SerializedName("model")
    val model : String,

    @SerializedName("regNumber")
    val regNumber : String,

    @SerializedName("make")
    val make : String,

    @SerializedName("color")
    val color : String,

    @SerializedName("chassisNumber")
    val chassisNumber: String,

    @SerializedName("clientId")
    val clientId: UUID,

    @SerializedName("clientName")
    val clientName : String,

    @SerializedName("clientSurname")
    val clientSurname : String

)
