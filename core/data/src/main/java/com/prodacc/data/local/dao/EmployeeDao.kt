package com.prodacc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.prodacc.data.local.entities.Employee
import java.util.UUID

@Dao
interface EmployeeDao {
    //select all employees
    @Query("SELECT * FROM employees")
    suspend fun getAllEmployees(): List<Employee>

    //select employee by id
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    suspend fun getEmployeeById(employeeId: UUID): Employee?

    //delete Employee
    @Query("DELETE FROM employees WHERE id = :employeeId")
    suspend fun deleteEmployee(employeeId: UUID)

    //update Employee
    @Update
    suspend fun updateEmployee(employee: Employee)

    //insert Employee
    @Insert
    suspend fun insertEmployee(employee: Employee)

}