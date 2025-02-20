package com.prodacc.data.remote

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.prodacc.data.NetworkManager
import com.prodacc.data.remote.services.ClientService
import com.prodacc.data.remote.services.CommentService
import com.prodacc.data.remote.services.ControlChecklistService
import com.prodacc.data.remote.services.EmployeeService
import com.prodacc.data.remote.services.JobCardReportService
import com.prodacc.data.remote.services.JobCardService
import com.prodacc.data.remote.services.JobCardStatusService
import com.prodacc.data.remote.services.JobCardTechniciansService
import com.prodacc.data.remote.services.LogInService
import com.prodacc.data.remote.services.ProductService
import com.prodacc.data.remote.services.ServiceChecklistService
import com.prodacc.data.remote.services.StateChecklistService
import com.prodacc.data.remote.services.TimesheetService
import com.prodacc.data.remote.services.UserService
import com.prodacc.data.remote.services.VehicleService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiServiceContainer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkManager: NetworkManager,
    private val tokenManager: TokenManager,
    private val gson: Gson,
    private val okHttpClient: OkHttpClient,
    private val retrofit: Retrofit
) : NetworkManager.NetworkChangeListener {
    private val logger = Logger.getLogger(ApiServiceContainer::class.java.name)
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Services
    private var _logInService: LogInService = retrofit.create(LogInService::class.java)
    private var _jobCardService: JobCardService = retrofit.create(JobCardService::class.java)
    private var _vehicleService: VehicleService = retrofit.create(VehicleService::class.java)
    private var _clientService: ClientService = retrofit.create(ClientService::class.java)
    private var _employeeService: EmployeeService = retrofit.create(EmployeeService::class.java)
    private var _userService: UserService = retrofit.create(UserService::class.java)
    private var _productService: ProductService = retrofit.create(ProductService::class.java)
    private var _controlChecklistService = retrofit.create(ControlChecklistService::class.java)
    private var _jobCardReportService = retrofit.create(JobCardReportService::class.java)
    private var _jobCardStatusService = retrofit.create(JobCardStatusService::class.java)
    private var _jobCardTechniciansService = retrofit.create(JobCardTechniciansService::class.java)
    private var _serviceChecklistService = retrofit.create(ServiceChecklistService::class.java)
    private var _stateChecklistService = retrofit.create(StateChecklistService::class.java)
    private var _timesheetService = retrofit.create(TimesheetService::class.java)
    private var _commentService = retrofit.create(CommentService::class.java)

    init {
        networkManager.addNetworkChangeListener(this)
        initializeServices()
    }

    override fun onNetworkChanged() {
        serviceScope.launch {
            updateServices()
        }
    }

    private fun updateServices() {
        serviceScope.launch {
            try {
                initializeServices()
            } catch (e: Exception) {
                Log.e("Error updating services: ", "${e.message}" )
            }
        }
    }

    private fun initializeServices() {
        _logInService = retrofit.create(LogInService::class.java)
        _jobCardService = retrofit.create(JobCardService::class.java)
        _vehicleService = retrofit.create(VehicleService::class.java)
        _clientService = retrofit.create(ClientService::class.java)
        _employeeService = retrofit.create(EmployeeService::class.java)
        _userService = retrofit.create(UserService::class.java)
        _controlChecklistService = retrofit.create(ControlChecklistService::class.java)
        _jobCardReportService = retrofit.create(JobCardReportService::class.java)
        _jobCardStatusService = retrofit.create(JobCardStatusService::class.java)
        _jobCardTechniciansService = retrofit.create(JobCardTechniciansService::class.java)
        _serviceChecklistService = retrofit.create(ServiceChecklistService::class.java)
        _stateChecklistService = retrofit.create(StateChecklistService::class.java)
        _timesheetService = retrofit.create(TimesheetService::class.java)
        _commentService = retrofit.create(CommentService::class.java)
        _productService = retrofit.create(ProductService::class.java)
    }

    // Service getters
    val logInService: LogInService get() = _logInService
    val jobCardService: JobCardService get() = _jobCardService
    val vehicleService: VehicleService get() = _vehicleService
    val clientService: ClientService get() = _clientService
    val employeeService: EmployeeService get() = _employeeService
    val userService: UserService get() = _userService
    val controlChecklistService: ControlChecklistService get() = _controlChecklistService
    val jobCardReportService: JobCardReportService get() = _jobCardReportService
    val jobCardStatusService: JobCardStatusService get() = _jobCardStatusService
    val jobCardTechniciansService: JobCardTechniciansService get() = _jobCardTechniciansService
    val serviceChecklistService: ServiceChecklistService get() = _serviceChecklistService
    val stateChecklistService: StateChecklistService get() = _stateChecklistService
    val timesheetService: TimesheetService get() = _timesheetService
    val commentService: CommentService get() = _commentService
    val productService: ProductService get() = _productService

    fun cleanup() {
        networkManager.removeNetworkChangeListener(this)
        serviceScope.cancel()
    }
}