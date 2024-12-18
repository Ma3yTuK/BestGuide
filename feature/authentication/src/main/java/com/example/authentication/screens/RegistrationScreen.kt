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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.authentication.viewmodels.AuthViewModel
import com.example.data.models.UserModel


@Composable
fun RegistrationRoute(
    readToken: suspend () -> String?,
    writeToken: suspend (String) -> Unit,
    deleteToken: suspend () -> Unit,
    goHome: () -> Unit,
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

    RegistrationScreen(
        authState.value.isUpdating,
        authState.value.error,
        viewModel::register,
        modifier
    )
}

@Composable
fun RegistrationScreen(
    isUpdating: Boolean,
    error: String?,
    register: (UserModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordMismatchError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Register", style = MaterialTheme.typography.titleLarge)

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

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (passwordMismatchError) {
            Text(
                text = "Passwords do not match",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    passwordMismatchError = false
                    register(UserModel(username = email, password = password))
                } else {
                    passwordMismatchError = true
                }
            },
            enabled = !isUpdating,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isUpdating) "Registering..." else "Register")
        }
    }
}