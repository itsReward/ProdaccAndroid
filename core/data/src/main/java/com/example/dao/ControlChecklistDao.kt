package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.entities.ControlChecklist
import java.util.UUID

@Dao
interface ControlChecklistDao {
    //insert control checklist
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(controlChecklistDao: ControlChecklist)

    //update control checklist
    @Update
    suspend fun update(controlChecklistDao: ControlChecklist)

    //delete control checklist
    @Query("DELETE FROM control_checklists WHERE id = :id")
    suspend fun delete(id: UUID)

    //select all control checklists
    @Query("SELECT * FROM control_checklists")
    suspend fun selectAll(): List<ControlChecklist>

    //select control checklist by job card id
    @Query("SELECT * FROM control_checklists WHERE jobCardId = :jobCardId")
    suspend fun selectByJobCardId(jobCardId: Long): List<ControlChecklist>
}