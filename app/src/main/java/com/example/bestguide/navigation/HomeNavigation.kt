package com.example.bestguide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.achievements.achievementsNavigation
import com.example.landmarks.Landmarks
import com.example.landmarks.landmarksNavigation
import com.example.landmarks.navigateToPlace
import com.google.android.libraries.places.api.net.PlacesClient

@Composable
fun HomeNavigation (
    placesClient: PlacesClient,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Landmarks,
        modifier = modifier
    ) {
        landmarksNavigation(
            navController::navigateToPlace,
            placesClient,
            modifier
        )
        achievementsNavigation()
    }
}