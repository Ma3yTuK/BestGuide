package com.example.data.models

data class PlaceModel (
    var id: Long? = null,
    var placeId: String,
    var latitude: Double,
    var longitude: Double,
    var name: String
)