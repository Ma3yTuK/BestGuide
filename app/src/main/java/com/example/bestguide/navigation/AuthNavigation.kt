package com.example.bestguide.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.authentication.Auth
import com.example.authentication.authNavigation
import com.example.authentication.navigateToRegister

@Composable
fun AuthNavigation (
    readToken: suspend () -> String?,
    writeToken: suspend (String) -> Unit,
    deleteToken: suspend () -> Unit,
    goHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Auth,
        modifier = modifier
    ) {
        authNavigation(
            readToken,
            writeToken,
            deleteToken,
            goHome,
            navController::navigateToRegister,
            modifier
        )
    }
}