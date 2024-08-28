package com.prodacc.data.repositories;

import com.prodacc.data.remote.dao.JobCard;
import java.time.LocalDateTime

import java.util.List;
import java.util.UUID

class JobCardRepository {
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
        "ABF 1234",
        "ACG 5678",
        "BHT 2345",
        "CAZ 6789",
        "DMV 3456",
        "EMR 7890",
        "FHG 4567",
        "GJK 8901",
        "HLM 5678",
        "JNV 9012",
        "KPL 6789",
        "LMO 0123",
        "MTR 7890",
        "NZW 3456",
        "PKY 4567",
        "QRS 5678",
        "RTV 6789",
        "SWZ 7890",
        "TXA 8901",
        "UVL 9012"
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

    fun generateJobCards(size : Int = 15 ): kotlin.collections.List<JobCard> {
        return List(size){ index ->
            JobCard(
                id = UUID.randomUUID(),
                jobCardName = " ${firstNames.random()} ${lastNames.random()}'s ${mercedesModels.random()}",
                jobCardNumber = Math.random().toInt(),
                vehicleId = UUID.randomUUID(),
                vehicleName = mercedesModels.random(),
                clientId = UUID.randomUUID(),
                clientName = "${firstNames.random()} ${lastNames.random()}",
                serviceAdvisorId = UUID.randomUUID(),
                serviceAdvisorName = "${firstNames.random()} ${lastNames.random()}",
                supervisorId = UUID.randomUUID(),
                supervisorName = "${firstNames.random()} ${lastNames.random()}",
                dateAndTimeIn = LocalDateTime.now(),
                estimatedTimeOfCompletion = LocalDateTime.now().plusHours(1),
                dateAndTimeFrozen = null,
                dateAndTimeClosed = null,
                priority = false,
                jobCardDeadline = LocalDateTime.now().plusDays(1),
                timesheets = listOf(),
                stateChecklistId = UUID.randomUUID(),
                serviceChecklistId = UUID.randomUUID(),
                controlChecklistId = UUID.randomUUID()
            )
        }
    }
}
