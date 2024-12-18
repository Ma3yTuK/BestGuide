package com.example.bestguide

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.example.achievements.Achievements
import com.example.bestguide.navigation.HomeNavigation
import com.example.bestguide.ui.theme.BestGuideTheme
import com.example.data.repositories.AchievementRepository
import com.example.data.repositories.PlaceRepository
import com.example.landmarks.HomeRoute
import com.example.landmarks.Landmarks
import com.example.staticaccess.StaticPropertyController
import com.google.android.libraries.places.api.Places

object ManifestUtils {
    fun getApiKeyFromManifest(context: Context): String? {
        return try {
            val applicationInfo = context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            bundle.getString("com.google.android.geo.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val staticPropertyController = StaticPropertyController(applicationContext)

        PlaceRepository.initialize(staticPropertyController::readToken)
        AchievementRepository.initialize(staticPropertyController::readToken)

        val apiKey = ManifestUtils.getApiKeyFromManifest(this)
        if (!Places.isInitialized() && apiKey != null) {
            Places.initialize(applicationContext, apiKey)
        }
        val placesClient = Places.createClient(applicationContext)

        enableEdgeToEdge()
        setContent {
            BestGuideTheme {
                val navController: NavHostController = rememberNavController()

                var selectedItem by remember { mutableIntStateOf(0) }
                val items = listOf("Landmarks", "Achievements")
                val routes = listOf(Landmarks, Achievements)
                val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Star)
                val unselectedIcons =
                    listOf(Icons.Outlined.Home, Icons.Outlined.Star)

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                            contentDescription = item
                                        )
                                    },
                                    label = { Text(item) },
                                    selected = selectedItem == index,
                                    onClick = {
                                        navController.navigate(routes[index], NavOptions.Builder().setRestoreState(true).setPopUpTo<HomeRoute>(false, true).build())
                                        selectedItem = index
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    HomeNavigation(
                        placesClient,
                        navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}