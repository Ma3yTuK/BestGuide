package com.example.data.services

import com.example.data.models.AchievementModel
import com.example.data.models.AchievementPartModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AchievementService {
    @POST("locationInfo/{longitude}/{latitude}")
    suspend fun sendLocationInfo(@Path("longitude") longitude: Double, @Path("latitude") latitude: Double, @Header("Authorization") authHeader: String): Response<List<AchievementModel>>

    @GET("achievementPartsByPlace/{id}")
    suspend fun getAchievementPartsByPlace(@Path("id") placeId: String, @Header("Authorization") authHeader: String): Response<List<AchievementPartModel>>

    @GET("achievementsCompleted")
    suspend fun getAchievementsCompleted(@Header("Authorization") authHeader: String): Response<List<AchievementModel>>

    @GET("achievementsNotCompleted")
    suspend fun getAchievementsNotCompleted(@Header("Authorization") authHeader: String): Response<List<AchievementModel>>

    @GET("achievementPartsCompletedByAchievement/{id}")
    suspend fun getAchievementPartsCompletedByAchievement(@Path("id") achievementId: Long, @Header("Authorization") authHeader: String): Response<List<AchievementPartModel>>

    @GET("achievementPartsNotCompletedByAchievement/{id}")
    suspend fun getAchievementPartsNotCompletedByAchievement(@Path("id") achievementId: Long, @Header("Authorization") authHeader: String): Response<List<AchievementPartModel>>
}