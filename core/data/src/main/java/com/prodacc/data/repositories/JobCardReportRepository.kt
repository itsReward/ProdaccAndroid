package com.prodacc.data.repositories

import com.prodacc.data.remote.ApiInstance
import com.prodacc.data.remote.dao.JobCardReport
import com.prodacc.data.remote.dao.NewJobCardReport
import com.prodacc.data.remote.dao.UpdateJobCardReport
import java.io.IOException
import java.util.UUID

class JobCardReportRepository {
    private val service = ApiInstance.jobCardReportService


    suspend fun getJobCardReports(jobCardId: UUID): LoadingResult {
        return try {
            val reports = service.getJobCardReports(jobCardId)
            if (reports.isSuccessful) {
                LoadingResult.Success(reports.body()!!)
            } else {
                LoadingResult.Error(reports.message())
            }
        } catch (e: Exception) {
            when (e) {
                is java.net.UnknownHostException -> LoadingResult.Error("No internet connection")
                is IOException -> LoadingResult.Error("Network error")
                else -> LoadingResult.Error(e.message ?: "Empty Error Message")
            }
        }
    }

    suspend fun newJobCardReport(report: JobCardReport): LoadingResult {
        return try {
            val response = service.createReport(
                NewJobCardReport(
                    jobReport = report.jobReport,
                    jobCardId = report.jobCardId,
                    employeeId = report.employeeId,
                    reportType = report.reportType
                )
            )
            if (response.isSuccessful) {
                LoadingResult.SingleEntitySuccess(response.body()!!)
            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun updateJobCardReport(id: UUID, report: JobCardReport): LoadingResult {
        return try {
            val response = service.updateReport(
                id,
                UpdateJobCardReport(
                    jobReport = report.jobReport,
                    jobCardId = report.jobCardId,
                    employeeId = report.employeeId,
                    reportType = report.reportType
                )
            )
            if (response.isSuccessful) {
                LoadingResult.SingleEntitySuccess(response.body()!!)

            } else {
                LoadingResult.Error(response.message())
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> LoadingResult.Error("Network Error")
                else -> LoadingResult.Error(e.message ?: "Unknown Error")
            }
        }
    }

    suspend fun getReportById(id: UUID): LoadingResult {
        return try {
            val response = service.getReportById(id)
            if (response.isSuccessful) {
                LoadingResult.SingleEntitySuccess(response.body()!!)
            } else {
                LoadingResult.Error("Failed to fetch report: ${response.message()}")
            }
        } catch (e: Exception) {
            when (e) {
                is java.net.UnknownHostException -> LoadingResult.Error("No internet connection")
                is IOException -> LoadingResult.Error("Network error")
                else -> LoadingResult.Error("Failed to fetch reports")
            }
        }
    }


    sealed class LoadingResult {
        data class Success(val response: List<JobCardReport>) : LoadingResult()
        data class SingleEntitySuccess(val response: JobCardReport) : LoadingResult()
        data class Error(val message: String) : LoadingResult()

    }
}