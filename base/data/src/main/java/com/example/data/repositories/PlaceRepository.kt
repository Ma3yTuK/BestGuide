package com.example.data.repositories

import com.example.data.Api
import com.example.data.handlers.processError
import com.example.data.models.PlaceModel

object PlaceRepository {
    var getToken: suspend () -> String? = { "" }

    fun initialize(getToken: suspend () -> String?) {
        this.getToken = getToken
    }

    suspend fun getPlaces(): Set<PlaceModel> {
        return processError(Api.placeService.getPlaces("Bearer ${getToken()}"))
    }
}