package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.entities.ServiceChecklist
import java.util.UUID

@Dao
interface ServiceChecklistDao {
    //insert service checklist
    @Insert
    suspend fun insertServiceChecklist(serviceChecklist: ServiceChecklist)

    //update service checklist
    @Update
    suspend fun updateServiceChecklist(serviceChecklist: ServiceChecklist)

    //delete service checklist
    @Query("DELETE FROM service_checklists WHERE id = :id")
    suspend fun deleteServiceChecklist(id: UUID)

    //get service checklist by id
    @Query("SELECT * FROM service_checklists WHERE id = :id")
    suspend fun getServiceChecklistById(id: UUID): ServiceChecklist?

    //get all service checklists
    @Query("SELECT * FROM service_checklists")
    suspend fun getAllServiceChecklists(): List<ServiceChecklist>

    //get service checklist by job card id
    @Query("SELECT * FROM service_checklists WHERE jobCardId = :jobCardId")
    suspend fun getServiceChecklistByJobCardId(jobCardId: UUID): ServiceChecklist?

}