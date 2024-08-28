package com.prodacc.data.repositories

import com.prodacc.data.remote.dao.Employee
import com.prodacc.data.remote.dao.EmployeeJobCard
import java.util.UUID

class EmployeeRepository {
    private val firstNames = listOf(
        "Tendai",
        "Nyasha",
        "Tatenda",
        "Chipo",
        "Ropafadzo",
        "Simba",
        "Kudakwashe",
        "Kelvin",
        "Vimbai",
        "Rutendo",
        "Tafadzwa",
        "Farai",
        "Chenai",
        "Anesu",
        "Kudzai",
        "Shingirai",
        "Munyaradzi",
        "Makanaka",
        "Tanyaradzwa",
        "Tariro",
        "Runyararo"
    )
    private val lastNames = listOf(
        "Moyo",
        "Ndlovu",
        "Mugabe",
        "Chinotimba",
        "Mushonga",
        "Chiwenga",
        "Mandaza",
        "Gumbo",
        "Sibanda",
        "Mpofu",
        "Matsika",
        "Chigumba",
        "Dube",
        "Mugari",
        "Ngwenya",
        "Mugari",
        "Muringani",
        "Masunda",
        "Chikafu",
        "Sithole"
    )
    private val homeAddresses = listOf(
        "123 Main Street, Harare",
        "456 Park Avenue, Bulawayo",
        "789 Robert Mugabe Avenue, Gweru",
        "1011 Jameson Avenue, Mutare",
        "1212 Samora Machel Avenue, Masvingo",
        "1313 Chikanga Road, Mutare",
        "1414 Main Street, Bulawayo",
        "1515 Robert Mugabe Avenue, Gweru",
        "1616 Jameson Avenue, Mutare",
        "1717 Samora Machel Avenue, Masvingo",
        "1818 Chikanga Road, Mutare",
        "1919 Main Street, Bulawayo",
        "2020 Robert Mugabe Avenue, Gweru",
        "2121 Jameson Avenue, Mutare",
        "2222 Samora Machel Avenue, Masvingo",
        "2323 Chikanga Road, Mutare",
        "2424 Main Street, Bulawayo",
        "2525 Robert Mugabe Avenue, Gweru",
        "2626 Jameson Avenue, Mutare",
        "2727 Samora Machel Avenue, Masvingo"
    )
    private val phoneNumbers = listOf(
        "+263 77 234 5678",
        "+263 71 890 1234",
        "+263 78 901 2345",
        "+263 73 123 4567",
        "+263 76 543 2109",
        "+263 77 876 5432",
        "+263 71 123 4567",
        "+263 78 543 2109",
        "+263 73 901 2345",
        "+263 76 123 4567",
        "+263 77 543 2109",
        "+263 71 901 2345",
        "+263 78 123 4567",
        "+263 73 543 2109",
        "+263 76 901 2345",
        "+263 77 123 4567",
        "+263 71 543 2109",
        "+263 78 901 2345",
        "+263 73 123 4567",
        "+263 76 543 2109"
    )
    private val jobTitles = listOf(
        "Software Engineer",
        "Data Analyst",
        "Marketing Manager",
        "Human Resources Specialist",
        "Accountant",
        "Project Manager",
        "Customer Service Representative",
        "Sales Representative",
        "Graphic Designer",
        "Web Developer",
        "Teacher",
        "Doctor",
        "Nurse",
        "Lawyer",
        "Architect",
        "Engineer",
        "Chef",
        "Bartender",
        "Retail Manager",
        "Financial Analyst"
    )
    private val employeeDepartment = listOf("Workshop", "Front", "Executive")



    fun getEmployees() = generateEmployees(20)
    fun getEmployee(id : UUID): Employee{
        return generateEmployees(1).random()
    }


    private fun generateEmployees(i: Int): List<Employee> {
        return List(i) {
            Employee(
                id = UUID.randomUUID(),
                employeeName = firstNames.random(),
                employeeSurname = lastNames.random(),
                rating = 0f,
                employeeRole = jobTitles.random(),
                employeeDepartment = employeeDepartment.random(),
                phoneNumber = phoneNumbers.random(),
                homeAddress = homeAddresses.random(),
                jobCards = List(5){EmployeeJobCard(UUID.randomUUID(), name = "$firstNames ")}
            )
        }
    }
}