package com.example.landmarks.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.landmarks.viewmodels.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import timber.log.Timber

@Composable
fun HomeRoute(
    placesClient: PlacesClient,
    onMarkerClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel {
        MapViewModel()
    },
) {
    HomeScreen(
        viewModel,
        placesClient,
        onMarkerClicked,
        modifier
    )
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun HomeScreen(
    mapViewModel: MapViewModel,
    placesClient: PlacesClient,
    onMarkerClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current

    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Timber.e("Location permission was denied by the user.")
        }
    }

    LaunchedEffect(mapViewModel.currentLatLong) {
        if (mapViewModel.currentLatLong != null)
            cameraPositionState.animate(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(mapViewModel.currentLatLong!!, 10f)))
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
//                userLocation?.let {
//                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
//                }
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        try {
            mapViewModel.fetchPlaces()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ){
            userLocation?.let {
                Marker(
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                    state = MarkerState(position = it), // Place the marker at the user's location
                    title = "Your Location", // Set the title for the marker
                    snippet = "This is where you are currently located." // Set the snippet for the marker
                )
            }
            Clustering(
                items = mapViewModel.fetchedPlaces,
                onClusterItemInfoWindowClick = {
                    onMarkerClicked(it.placeId)
                },
                clusterItemContent = null
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var text by remember { mutableStateOf("") }

                AnimatedVisibility(
                    mapViewModel.locationAutofill.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mapViewModel.locationAutofill.count()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable(
                                        onClick = {
                                            text = mapViewModel.locationAutofill[it].address
                                            mapViewModel.getCoordinates(mapViewModel.locationAutofill[it], placesClient)
                                            mapViewModel.locationAutofill.clear()
                                        },
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple()
                                    )
                            ) {
                                Text(mapViewModel.locationAutofill[it].address)
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        mapViewModel.searchPlaces(it, placesClient)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}