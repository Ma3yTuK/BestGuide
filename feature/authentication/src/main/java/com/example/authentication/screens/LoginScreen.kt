package com.example.authentication.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.viewmodels.AuthViewModel


@Composable
fun LoginRoute(
    readToken: suspend () -> String?,
    writeToken: suspend (String) -> Unit,
    deleteToken: suspend () -> Unit,
    goHome: () -> Unit,
    onRegistrationClicked: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel {
        AuthViewModel(
            readToken = readToken,
            writeToken = writeToken,
            deleteToken = deleteToken,
            goHome = goHome
        )
    },
) {
    val authState = viewModel.currentAuthState.collectAsStateWithLifecycle()

    LoginScreen(
        authState.value.isUpdating,
        authState.value.error,
        viewModel::login,
        onRegistrationClicked,
        modifier
    )
}

@Composable
fun LoginScreen(
    isUpdating: Boolean,
    error: String?,
    login: (String, String) -> Unit,
    onRegistrationClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (error != null) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { login(email, password) },
            enabled = !isUpdating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isUpdating) "Logging in..." else "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { onRegistrationClicked() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Don't have an account? Register")
        }
    }
}