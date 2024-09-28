package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.ServiceChecklist
import java.time.LocalDateTime
import java.util.UUID

class ServiceChecklistRepository {
    private val serviceChecklists: List<ServiceChecklist> by lazy {
        generateServiceChecklists(20)
    }

    fun getServiceChecklists(): List<ServiceChecklist> {
        return serviceChecklists
    }

    fun getServiceChecklist(id: UUID): ServiceChecklist? {
        return serviceChecklists.find { it.id == id }
    }

    private fun generateServiceChecklists(size: Int): List<ServiceChecklist> {
        return List(size) {
            ServiceChecklist(
                id = UUID.randomUUID(),
                jobCardId = UUID.randomUUID(),
                jobCardName = "Service Job Card ${it + 1}",
                technicianId = UUID.randomUUID(),
                technicianName = "Service Technician ${it + 1}",
                created = LocalDateTime.now(),
                checklist = mapOf("Service Item ${it + 1}" to "Service Status ${it + 1}")
            )
        }
    }
}