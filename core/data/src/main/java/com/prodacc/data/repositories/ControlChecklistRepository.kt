package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.ControlChecklist
import java.time.LocalDateTime
import java.util.UUID

class ControlChecklistRepository {
    private val controlChecklists: List<ControlChecklist> by lazy {
        generateControlChecklists(20)
    }

    fun getControlChecklists(): List<ControlChecklist> {
        return controlChecklists
    }

    fun getControlChecklist(id: UUID): ControlChecklist? {
        return controlChecklists.find { it.id == id }
    }

    private fun generateControlChecklists(size: Int): List<ControlChecklist> {
        return List(size) {
            ControlChecklist(
                id = UUID.randomUUID(),
                jobCardId = UUID.randomUUID(),
                jobCardName = "Job Card ${it + 1}",
                technicianId = UUID.randomUUID(),
                technicianName = "Technician ${it + 1}",
                created = LocalDateTime.now(),
                checklist = mapOf("Item ${it + 1}" to "Status ${it + 1}")
            )
        }
    }
}