package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.StateChecklist
import java.time.LocalDateTime
import java.util.UUID

class StateChecklistRepository {
    private val stateChecklists: List<StateChecklist> by lazy {
        generateStateChecklists(20)
    }

    fun getStateChecklists(): List<StateChecklist> {
        return stateChecklists
    }

    fun getStateChecklist(id: UUID): StateChecklist? {
        return stateChecklists.find { it.id == id }
    }

    private fun generateStateChecklists(size: Int): List<StateChecklist> {
        return List(size) {
            StateChecklist(
                id = UUID.randomUUID(),
                jobCardId = UUID.randomUUID(),
                jobCardName = "State Job Card ${it + 1}",
                technicianId = UUID.randomUUID(),
                technicianName = "State Technician ${it + 1}",
                created = LocalDateTime.now(),
                checklist = mapOf("State Item ${it + 1}" to "State Status ${it + 1}")
            )
        }
    }
}