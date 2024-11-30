package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.Client
import com.prodacc.data.remote.dao.ClientVehicle
import java.io.IOException
import java.util.UUID

class ClientRepository {
    val service = ApiInstance.clientService

    val firstNames = listOf(
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
    val lastNames = listOf(
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
    val gender = listOf("male","female")
    val homeAddresses = listOf(
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
    val phoneNumbers = listOf(
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
    val companies = listOf(
        "Econet Wireless Zimbabwe",
        "NetOne",
        "Telecel Zimbabwe",
        "ZB Bank",
        "Standard Chartered Bank",
        "CABS",
        "Old Mutual",
        "Delta Corporation",
        "National Foods Holdings",
        "OK Zimbabwe",
        "TM Pick n Pay Zimbabwe",
        "United Refineries Limited",
        "Dairibord Holdings",
        "Meikles Limited",
        "Simbisa Brands",
        "Hwange Colliery Company",
        "RioZim Limited",
        "Zimplats",
        "Bindura Nickel Corporation",
        "African Sun Hotels"
    )
    val jobTitles = listOf(
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
    val emailAddresses = listOf(
        "tadiwanashe.mupfurume@econet.co.zw",
        "tanyaradzwa.chinyanganya@netone.co.zw",
        "michael.nyoni@telecel.co.zw",
        "ruth.mahere@zbbank.co.zw",
        "tauranai.chinyanga@standardchartered.co.zw",
        "kurien.gumbo@cabs.co.zw",
        "nobukhosi.sipho@oldmutual.co.zw",
        "tafadzwa.mahere@deltacorp.co.zw",
        "tsitsi.mupfurume@nationalfoods.co.zw",
        "tauranai.chinyanga@okzimbabwe.co.zw",
        "kurien.gumbo@tmnpicknpay.co.zw",
        "nobukhosi.sipho@unitedrefineries.co.zw",
        "tafadzwa.mahere@dairibord.co.zw",
        "tsitsi.mupfurume@meikles.co.zw",
        "tauranai.chinyanga@simbisabrands.co.zw",
        "kurien.gumbo@hwangecolliery.co.zw",
        "nobukhosi.sipho@riozim.co.zw",
        "tafadzwa.mahere@zimplats.co.zw",
        "tsitsi.mupfurume@binduranickel.co.zw",
        "tauranai.chinyanga@africansunhotels.co.zw"
    )
    val mercedesModels = listOf(
        "A-Class",
        "B-Class",
        "C-Class",
        "E-Class",
        "S-Class",
        "CLA",
        "CLS",
        "GLA",
        "GLB",
        "GLC",
        "GLE",
        "GLS",
        "G-Class",
        "SL",
        "SLC",
        "AMG GT",
        "EQC",
        "EQS",
        "EQB",
        "EQE"
    )



    private val clients: List<Client> by lazy {
        generateClients(20)
    }

    fun getClientsList(): List<Client> {
        return clients
    }

    fun getClient(id: UUID): Client {
        return clients.last()
    }



    private fun generateClients(i: Int): List<Client> {
        return List(i) {
            Client(
                id = UUID.randomUUID(),
                clientName = firstNames.random(),
                clientSurname = lastNames.random(),
                gender = gender.random(),
                jobTitle = jobTitles.random(),
                company = companies.random(),
                phone = phoneNumbers.random(),
                email = emailAddresses.random(),
                address = homeAddresses.random(),
                vehicles = generateVehiclesForClient(5)
            )
        }
    }

    private fun generateVehiclesForClient(size: Int = 2):List<ClientVehicle>{
        return List(size){
            ClientVehicle(
                id = UUID.randomUUID(),
                model = mercedesModels.random(),
                make = "Mercedes Benz"
            )
        }
    }

    suspend fun getClientsById(id: UUID): LoadingResult {
        return try {
            val response = service.getClient(id)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    return LoadingResult.SingleEntity(null, "Client not found")
                } else {
                    return LoadingResult.SingleEntity(response.body(), null)
                }
            } else{
                return LoadingResult.Error(response.raw().message())
            }
        } catch (e : Exception){
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun getClients(): LoadingResult {
        return try {
            val response = service.getClients()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error(response.raw().message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.NetworkError
                else -> LoadingResult.ErrorSingleMessage(e.message ?: "Unknown error occurred")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val clients: List<Client>) : LoadingResult()
        data class Error(val message: String?) : LoadingResult()
        data class ErrorSingleMessage(val message: String): LoadingResult()
        data object NetworkError : LoadingResult()
        data class SingleEntity(val client: Client?, val error: String?) : LoadingResult()
    }
}