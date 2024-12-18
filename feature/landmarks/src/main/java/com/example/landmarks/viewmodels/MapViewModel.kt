package com.example.landmarks.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location.distanceBetween
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.PlaceModel
import com.example.data.repositories.AchievementRepository
import com.example.data.repositories.PlaceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.clustering.ClusterItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

data class AutocompleteResult(
    val address: String,
    val placeId: String
)

data class MyItem(
    val itemPosition: LatLng,
    val itemTitle: String,
    val itemSnippet: String,
    val itemZIndex: Float,
    val placeId: String,
) : ClusterItem {
    override fun getPosition(): LatLng =
        itemPosition

    override fun getTitle(): String =
        itemTitle

    override fun getSnippet(): String =
        itemSnippet

    override fun getZIndex(): Float =
        itemZIndex
}

class MapViewModel : ViewModel() {
    private val threshold = 30

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    val fetchedPlaces = mutableStateListOf<MyItem>()
    private var lastLatLng: LatLng = LatLng(0.0, 0.0)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation ?: return
            val userLatLng = LatLng(locationResult.lastLocation!!.latitude, locationResult.lastLocation!!.longitude)
            _userLocation.value = userLatLng
            this@MapViewModel.processLocation(userLatLng)
        }
    }

    private var searchJob: Job? = null
    private var fetchJob: Job? = null
    var currentLatLong: LatLng? by mutableStateOf(null)

    fun fetchPlaces() {
        fetchJob?.cancel()
        fetchedPlaces.clear()
        fetchJob = viewModelScope.launch {
            try {
                val places: Set<PlaceModel> = PlaceRepository.getPlaces()
                fetchedPlaces.addAll(places.map {
                    MyItem(LatLng(it.latitude, it.longitude), it.name, "snippet", 0F, it.placeId)
                })
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }

    fun searchPlaces(query: String, placesClient: PlacesClient) {
        searchJob?.cancel()
        locationAutofill.clear()
        searchJob = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()
            placesClient
                .findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    locationAutofill += response.autocompletePredictions.map {
                        AutocompleteResult(
                            it.getFullText(null).toString(),
                            it.placeId
                        )
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }

    fun getCoordinates(result: AutocompleteResult, placesClient: PlacesClient) {
        val placeFields = listOf(Place.Field.LOCATION)
        val request = FetchPlaceRequest.newInstance(result.placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    currentLatLong = it.place.location!!
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    @SuppressLint("MissingPermission")
    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.requestLocationUpdates(
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build(),
                    locationCallback,
                    Looper.getMainLooper())
            } catch (e: SecurityException) {
                Timber.e("Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Timber.e("Location permission is not granted.")
        }
    }

    private fun processLocation(latLng: LatLng) {
        val results = floatArrayOf(0.0F)
        distanceBetween(latLng.latitude, latLng.longitude, lastLatLng.latitude, lastLatLng.longitude, results)
        if (results[0] > threshold) {
            lastLatLng = latLng
            viewModelScope.launch {
                try {
                    val achieved = AchievementRepository.getAchievements(latLng.longitude, latLng.latitude)
                    achieved.forEach { achievement ->
                        Timber.e(achievement.name)
                    }
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }
    }
}