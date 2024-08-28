package com.prodacc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.prodacc.data.local.entities.Client
import java.util.UUID

@Dao
interface ClientDao {
    //Select all clients
    @Query("SELECT * FROM clients")
    suspend fun getAllClients(): List<Client>

    //Insert client
    @Insert
    suspend fun insertClient(client: Client)

    //Delete client
    @Query("DELETE FROM clients WHERE id = :clientId")
    suspend fun deleteClient(clientId: UUID)

    //Update client
    @Update
    suspend fun updateClient(client: Client)

    //Select client by id
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientById(clientId: UUID): Client?



}