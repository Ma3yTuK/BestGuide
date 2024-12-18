package com.example.achievements

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.achievements.screens.AchievementsRoute
import kotlinx.serialization.Serializable

@Serializable object AchievementsRoute
@Serializable object Achievements

fun NavController.navigateToAchievements(navOptions: NavOptions? = null) =
    navigate(route = AchievementsRoute, navOptions)

fun NavGraphBuilder.achievementsNavigation() {
    navigation<Achievements>(startDestination = AchievementsRoute) {
        composable<AchievementsRoute> {
            AchievementsRoute()
        }
    }
}