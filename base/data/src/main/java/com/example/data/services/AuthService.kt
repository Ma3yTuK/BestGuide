package com.example.data.services

import com.example.data.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    @GET("token")
    suspend fun getToken(@Header("Authorization") authHeader: String): Response<String>

    @POST("register")
    suspend fun register(@Body user: UserModel): Response<String>
}