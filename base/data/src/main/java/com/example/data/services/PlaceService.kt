package com.example.data.services

import com.example.data.models.PlaceModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface PlaceService {
    @GET("places")
    suspend fun getPlaces(@Header("Authorization") authHeader: String): Response<Set<PlaceModel>>
}