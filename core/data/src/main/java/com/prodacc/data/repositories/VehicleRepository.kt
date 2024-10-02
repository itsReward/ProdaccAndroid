package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.Vehicle
import java.util.UUID

class VehicleRepository {
    val mercedesModels = listOf(
        "A-Class",
        "B-Class",
        "C-Class",
        "E-Class",
        "S-Class",
        "CLA",
        "CLS",
        "GLA",
        "GLB",
        "GLC",
        "GLE",
        "GLS",
        "G-Class",
        "SL",
        "SLC",
        "AMG GT",
        "EQC",
        "EQS",
        "EQB",
        "EQE"
    )
    val regNumbers = listOf(
        "ABA 1234",
        "ACD 5678",
        "AAF 2345",
        "ABG 6789",
        "AGK 3456",
        "AAD 7890",
        "ACA 4567",
        "AEB 8901",
        "AFG 5678",
        "ABF 9012",
        "AGA 6789",
        "ACB 0123",
        "AAD 7890",
        "AFA 3456",
        "AGC 4567",
        "AEB 5678",
        "AAD 6789",
        "ACF 7890",
        "AFG 8901",
        "ABB 9012"
    )
    val firstNames = listOf(
        "Tendai",
        "Nyasha",
        "Tatenda",
        "Chipo",
        "Ropafadzo",
        "Simba",
        "Kudakwashe",
        "Vimbai",
        "Rutendo",
        "Tafadzwa",
        "Farai",
        "Chenai",
        "Anesu",
        "Kudzai",
        "Shingirai",
        "Munyaradzi",
        "Makanaka",
        "Tanyaradzwa",
        "Tariro",
        "Runyararo"
    )
    val lastNames = listOf(
        "Moyo",
        "Ndlovu",
        "Mugabe",
        "Chinotimba",
        "Mushonga",
        "Chiwenga",
        "Mandaza",
        "Gumbo",
        "Sibanda",
        "Mpofu",
        "Matsika",
        "Chigumba",
        "Dube",
        "Mugari",
        "Ngwenya",
        "Mugari",
        "Muringani",
        "Masunda",
        "Chikafu",
        "Sithole"
    )

    fun getVehicleById(id: UUID): Vehicle {
        return Vehicle(
            id = UUID.randomUUID(),
            model = mercedesModels.random(),
            regNumber = regNumbers.random(),
            make = "Mercedes Benz",
            color = listOf("Red", "Blue", "Green", "Black", "White").random(),
            chassisNumber = UUID.randomUUID().toString(),
            clientId = UUID.randomUUID(),
            clientName = firstNames.random(),
            clientSurname = lastNames.random()
        )
    }

    fun getVehicles(size: Int = 20): List<Vehicle> {
        return List(size) { index ->
            Vehicle(
                id = UUID.randomUUID(),
                model = mercedesModels.random(),
                regNumber = regNumbers.random(),
                make = "Mercedes Benz",
                color = listOf("Red", "Blue", "Green", "Black", "White").random(),
                chassisNumber = UUID.randomUUID().toString().dropLast(8),
                clientId = UUID.randomUUID(),
                clientName = firstNames.random(),
                clientSurname = lastNames.random()
            )
        }
    }
}