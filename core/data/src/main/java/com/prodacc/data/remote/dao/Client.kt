package com.prodacc.data.remote.dao

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class Client(
    @SerializedName("id") val id: UUID,

    @SerializedName("clientName") val clientName: String,

    @SerializedName("clientSurname") val clientSurname: String,

    @SerializedName("gender") val gender: String,

    @SerializedName("jobTitle") val jobTitle: String,

    @SerializedName("company") val company: String,

    @SerializedName("phone") val phone: String,

    @SerializedName("email") val email: String,

    @SerializedName("address") val address: String,

    @SerializedName("vehicles") val vehicles: List<ClientVehicle>
)

data class ClientVehicle(
    @SerializedName("id") val id: UUID,
    @SerializedName("model") val model: String,
    @SerializedName("make") val make: String
)

data class NewClient(
    @SerializedName("clientName") val clientName: String,

    @SerializedName("clientSurname") val clientSurname: String,

    @SerializedName("gender") val gender: String,

    @SerializedName("jobTitle") val jobTitle: String,

    @SerializedName("company") val company: String,

    @SerializedName("phone") val phone: String,

    @SerializedName("email") val email: String,

    @SerializedName("address") val address: String,
)