package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.entities.User
import java.util.UUID

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun deleteUser(id: UUID)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun loadUser(username: String, password: String): User?

    @Query("SELECT * FROM users ")
    suspend fun loadUsers(): List<User>

}