package com.yourcompany.digitaltok.data.repository

import com.yourcompany.digitaltok.data.model.PriorityTemplate
import com.yourcompany.digitaltok.data.model.PriorityTemplateDetail
import com.yourcompany.digitaltok.data.network.RetrofitClient

class PriorityRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getPriorityTemplates(): Result<List<PriorityTemplate>> = try {
        val response = apiService.getPriorityTemplates()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Result.success(body.result.items)
            } else {
                val errorMessage = body?.message ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Result.failure(Exception("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPriorityTemplateDetail(templateId: Int): Result<PriorityTemplateDetail> = try {
        val response = apiService.getPriorityTemplateDetail(templateId)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Unknown error"
                Result.failure(Exception(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Result.failure(Exception("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
