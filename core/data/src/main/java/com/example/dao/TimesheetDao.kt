package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.entities.Timesheet
import java.util.UUID

@Dao
interface TimesheetDao {
    //select all timesheets for an employee
    @Query("SELECT * FROM timesheets WHERE technician = :employeeId")
    suspend fun getTimesheetsForEmployee(employeeId: UUID): List<Timesheet>

    //select all timesheets for a job card
    @Query("SELECT * FROM timesheets WHERE jobCardId = :jobCardId")
    suspend fun getTimesheetsForJobCard(jobCardId: UUID): List<Timesheet>

    //insert timesheet
    @Insert
    suspend fun insertTimesheet(timesheet: Timesheet)

    //update timesheet
    @Update
    suspend fun updateTimesheet(timesheet: Timesheet)

    //delete timesheet
    @Query("DELETE FROM timesheets WHERE id = :id")
    suspend fun deleteTimesheet(id: UUID)


}