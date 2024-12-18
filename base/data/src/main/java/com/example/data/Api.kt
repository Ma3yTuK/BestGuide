package com.example.data

import com.example.data.services.AchievementService
import com.example.data.services.AuthService
import com.example.data.services.PlaceService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val BASE_URL = "http://192.168.34.156:8080"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()

object Api {
    val authService: AuthService
        get() = retrofit.create(AuthService::class.java)
    val placeService: PlaceService
        get() = retrofit.create(PlaceService::class.java)
    val achievementService: AchievementService
        get() = retrofit.create(AchievementService::class.java)
}