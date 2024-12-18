package com.example.authentication

import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.authentication.screens.LoginRoute
import com.example.authentication.screens.RegistrationRoute
import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object RegistrationRoute
@Serializable object Auth

fun NavController.navigateToLogin(navOptions: NavOptions? = null) =
    navigate(route = LoginRoute, navOptions)

fun NavController.navigateToRegister(navOptions: NavOptions? = null) =
    navigate(route = RegistrationRoute, navOptions)

fun NavGraphBuilder.authNavigation(
    readToken: suspend () -> String?,
    writeToken: suspend (String) -> Unit,
    deleteToken: suspend () -> Unit,
    goHome: () -> Unit,
    onRegistrationClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    navigation<Auth>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginRoute(
                readToken = readToken,
                writeToken = writeToken,
                deleteToken = deleteToken,
                goHome = goHome,
                onRegistrationClicked = onRegistrationClicked,
                modifier = modifier
            )
        }
        composable<RegistrationRoute> {
            RegistrationRoute(
                readToken = readToken,
                writeToken = writeToken,
                deleteToken = deleteToken,
                goHome = goHome,
                modifier = modifier
            )
        }
    }
}