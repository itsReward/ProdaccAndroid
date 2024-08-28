package com.prodacc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.prodacc.data.local.entities.StateChecklist
import java.util.UUID

@Dao
interface StateChecklistDao {
    //select state checklist by id
    @Query("SELECT * FROM state_checklists WHERE id = :id")
    suspend fun getStateChecklistById(id: UUID): StateChecklist

    //select all state checklists
    @Query("SELECT * FROM state_checklists")
    suspend fun getAllStateChecklists(): List<StateChecklist>

    //select all state checklist with job card id
    @Query("SELECT * FROM state_checklists WHERE jobCardId = :jobCardId")
    suspend fun getAllStateChecklistsByJobCardId(jobCardId: UUID): List<StateChecklist>

    //insert state checklist
    @Insert
    suspend fun insertStateChecklist(stateChecklist: StateChecklist)

    //update state checklist
    @Update
    suspend fun updateStateChecklist(stateChecklist: StateChecklist)

    //delete state checklist
    @Query("DELETE FROM state_checklists WHERE id = :id")
    suspend fun deleteStateChecklist(id : UUID)

}