package com.prodacc.data.repositories

import com.prodacc.data.di.CoroutineDispatchers
import com.prodacc.data.remote.ApiServiceContainer
import com.prodacc.data.remote.dao.NewTimesheet
import com.prodacc.data.remote.dao.Timesheet
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeSheetRepository @Inject constructor(
    private val apiServiceContainer: ApiServiceContainer,
    private val dispatcher: CoroutineDispatchers
) {
    private val service get() = apiServiceContainer.timesheetService

    suspend fun getTimeSheets(): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getAllTimesheets()
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getJobCardTimeSheets(jobCardId: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getTimesheetsByJobCardId(jobCardId)
            if (response.isSuccessful) {
                LoadingResult.Success(response.body() ?: emptyList())
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getTimeSheet(id: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.getTimesheetById(id)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.Error("No TimeSheet Response")
                } else {
                    LoadingResult.TimeSheet(response.body()!!)
                }

            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }


    suspend fun addTimeSheet(timesheet: NewTimesheet): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.addTimesheet(timesheet)
            if (response.isSuccessful) {
                if (response.body() == null) {
                    LoadingResult.Error("No TimeSheet Response")
                } else {
                    LoadingResult.TimeSheet(response.body()!!)
                }
            } else {
                LoadingResult.Error("Error: ${response.code()} ${response.errorBody()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun updateTimesheet(id: UUID, timesheet: Timesheet): LoadingResult =
        withContext(dispatcher.io) {
            try {
                val response = service.updateTimesheet(id, timesheet)
                if (response.isSuccessful) {
                    if (response.body() == null) {
                        LoadingResult.Error("No TimeSheet Response")
                    } else {
                        LoadingResult.TimeSheet(response.body()!!)
                    }
                } else {
                    LoadingResult.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> LoadingResult.Error("Network Error")
                    else -> LoadingResult.Error(e.message ?: "Unknown Error")
                }
            }
        }

    suspend fun deleteTimesheet(id: UUID): LoadingResult = withContext(dispatcher.io) {
        try {
            val response = service.deleteTimesheet(id)
            if (response.isSuccessful) {
                LoadingResult.Message("Timesheet Deleted")
            } else {
                LoadingResult.Error("Error: ${response.code()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    sealed class LoadingResult {
        data class Success(val timesheets: List<Timesheet>) : LoadingResult()
        data class Error(val message: String) : LoadingResult()
        data class TimeSheet(val timesheet: Timesheet) : LoadingResult()
        data class Message(val message: String) : LoadingResult()
    }
}