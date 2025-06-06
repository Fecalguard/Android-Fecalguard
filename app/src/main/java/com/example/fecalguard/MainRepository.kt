package com.example.fecalguard

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.fecalguard.data.local.UserPreference
import com.example.fecalguard.data.local.UserSession
import com.example.fecalguard.data.local.database.Disease
import com.example.fecalguard.data.local.database.DiseaseDao
import com.example.fecalguard.data.local.database.DiseaseImage
import com.example.fecalguard.data.local.database.DiseaseImageDao
import com.example.fecalguard.data.local.database.DiseaseWithDetails
import com.example.fecalguard.data.local.database.Medicine
import com.example.fecalguard.data.local.database.MedicineDao
import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.data.remote.response.HistoryResponse
import com.example.fecalguard.data.remote.response.LoginResponse
import com.example.fecalguard.data.remote.response.MessageResponse
import com.example.fecalguard.data.remote.retrofit.ApiService
import com.example.fecalguard.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class MainRepository @Inject constructor (
    private val userPreference : UserPreference,
    private val apiService: ApiService,
    private val diseaseDao: DiseaseDao,
    private val medicineDao: MedicineDao,
    private val diseaseImageDao: DiseaseImageDao
){
    fun register(email: String, password: String): Flow<Result<MessageResponse>> = flow {
        emit(Result.Loading)
        try{
            val response = apiService.register(email, password)

            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    fun login(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        emit(Result.Loading)
        try{
            val response = apiService.login(email, password)

            if (response.accessToken != null) {
                val user = UserSession(
                    token = response.accessToken
                )
                userPreference.saveSession(user)
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getSession(): Flow<UserSession> {
        return userPreference.getSession()
    }

    fun getDiagnosis(
        image: File
    ): Flow<Result<DiagnosisResponse>> = flow {
        emit(Result.Loading)
        try {
            val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = requestImageFile.let {
                MultipartBody.Part.createFormData(
                    "file",
                    image.name,
                    it
                )
            }
            val response = apiService.predict(multipartBody)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }


    fun getAllHistory(): Flow<Result<List<DiagnosisResponse>>> = flow {
        emit(Result.Loading)
        try {
            val response = apiService.getHistory()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Error: ${e.message}"))
        }
    }

    suspend fun getDisease(diseaseId: Int): Disease {
        return diseaseDao.getDisease(diseaseId)
    }

    suspend fun getDiseaseImage(diseaseId: Int): List<DiseaseImage> {
        return diseaseImageDao.getDiseaseImage(diseaseId)
    }

    suspend fun getMedicine(diseaseId: Int): List<Medicine> {
        return medicineDao.getMedicine(diseaseId)
    }
}