package com.prodacc.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.prodacc.data.local.entities.JobCard
import java.util.UUID

@Dao
interface JobCardDao {
    //insert job card
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertJobCard(jobCard: JobCard): UUID

    //update job card
    @Update
    suspend fun updateJobCard(jobCard: JobCard)

    //delete job card
    @Delete
    suspend fun deleteJobCard(jobCard: JobCard)

    //get job card by id
    @Query("SELECT * FROM job_cards WHERE id = :id")
    suspend fun getJobCardById(id: UUID): JobCard?

    //get all job cards
    @Query("SELECT * FROM job_cards")
    suspend fun getAllJobCards(): List<JobCard>

    //get job cards by vehicle id
    @Query("SELECT * FROM job_cards WHERE vehicleId = :vehicleId")
    suspend fun getJobCardsByVehicleId(vehicleId: UUID): List<JobCard>

    //get job cards by client id
    @Query("SELECT * FROM job_cards WHERE clientId = :clientId")
    suspend fun getJobCardsByClientId(clientId: UUID): List<JobCard>

    //get job card by technician id
    @Query("SELECT * FROM job_cards WHERE technicians LIKE :technicianId")
    suspend fun getJobCardsByTechnicianId(technicianId: UUID): List<JobCard>


}