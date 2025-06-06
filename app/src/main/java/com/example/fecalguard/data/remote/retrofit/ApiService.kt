package com.example.fecalguard.data.remote.retrofit

import com.example.fecalguard.data.remote.response.DiagnosisResponse
import com.example.fecalguard.data.remote.response.HistoryResponse
import com.example.fecalguard.data.remote.response.LoginResponse
import com.example.fecalguard.data.remote.response.MessageResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("api/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String
    ): MessageResponse

    @POST("api/login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String,
    ): LoginResponse

    @GET("api/history")
    suspend fun getHistory(): List<DiagnosisResponse>

    @Multipart
    @POST("api/predict")
    suspend fun predict(
        @Part file: MultipartBody.Part
    ): DiagnosisResponse
}
