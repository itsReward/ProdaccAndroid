package com.example.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.entities.Vehicle
import java.util.UUID

@Dao
interface VehicleDao {
    //select all vehicles
    @Query("SELECT * FROM vehicles")
    suspend fun loadAllVehicles(): List<Vehicle>

    //select vehicles by id
    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun loadVehicleById(id: UUID): Vehicle

    //load vehicles by client id
    @Query("SELECT * FROM vehicles WHERE clientId = :clientId")
    suspend fun loadVehiclesByClientId(clientId: UUID): List<Vehicle>

    //insert vehicle
    @Insert
    suspend fun insertVehicle(vehicle: Vehicle)

    //update vehicle
    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    //delete vehicle
    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteVehicle(id: UUID)

}