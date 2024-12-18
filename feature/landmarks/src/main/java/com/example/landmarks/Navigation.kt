package com.example.landmarks

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.landmarks.screens.HomeRoute
import com.example.landmarks.screens.PlaceRoute
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.serialization.Serializable

@Serializable object HomeRoute
@Serializable object Landmarks
@Serializable data class PlaceRouteD(val placeId: String)

fun NavController.navigateToHome(navOptions: NavOptions? = null) =
    navigate(route = HomeRoute, navOptions)

fun NavController.navigateToPlace(placeId: String, navOptions: NavOptions? = null) =
    navigate(route = PlaceRouteD(placeId), navOptions)

fun NavGraphBuilder.landmarksNavigation(
    onMarkerClicked: (String) -> Unit,
    placesClient: PlacesClient,
    modifier: Modifier = Modifier
) {
    navigation<Landmarks>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeRoute(
                placesClient,
                onMarkerClicked,
                modifier = modifier
            )
        }
        composable<PlaceRouteD> { backStackEntry ->
            val placeRoute: PlaceRouteD = backStackEntry.toRoute()
            PlaceRoute(
                placeRoute.placeId,
                placesClient,
                modifier = modifier
            )
        }
    }
}