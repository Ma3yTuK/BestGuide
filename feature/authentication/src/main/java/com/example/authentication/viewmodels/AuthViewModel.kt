package com.example.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.handlers.InputValidationException
import com.example.data.models.UserModel
import com.example.data.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CurrentAuthState(
    val token: String? = null,
    val isUpdating: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val readToken: suspend () -> String?,
    private val writeToken: suspend (String) -> Unit,
    private val deleteToken: suspend () -> Unit,
    private val goHome: () -> Unit
) : ViewModel() {
    private val _currentAuthState = MutableStateFlow(CurrentAuthState())
    val currentAuthState = _currentAuthState.asStateFlow()

    init {
        updateToken()
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _currentAuthState.update { currentState ->
            currentState.copy(isUpdating = true)
        }

        try {
            setToken(AuthRepository.getToken(username, password))
            goHome()
        } catch (inputValidationException: InputValidationException) {
            _currentAuthState.update { currentState ->
                currentState.copy(error = inputValidationException.message, isUpdating = false)
            }
        } catch (e: Exception) {
            _currentAuthState.update { currentState ->
                currentState.copy(error = "Cannot connect", isUpdating = false)
            }
        }
    }

    fun register(user: UserModel) = viewModelScope.launch {
        _currentAuthState.update { currentState ->
            currentState.copy(isUpdating = true)
        }

        try {
            setToken(AuthRepository.register(user))
            goHome()
        } catch (inputValidationException: InputValidationException) {
            _currentAuthState.update { currentState ->
                currentState.copy(error = inputValidationException.message, isUpdating = false)
            }
        } catch (e: Exception) {
            _currentAuthState.update { currentState ->
                currentState.copy(error = "Cannot connect", isUpdating = false)
            }
        }
    }

    fun logout() = viewModelScope.launch {
        deleteToken()
        updateToken()
    }

    private fun setToken(token: String) = viewModelScope.launch  {
        writeToken(token)
        updateToken()
    }

    private fun updateToken() = viewModelScope.launch  {
        _currentAuthState.value = CurrentAuthState(
            token = readToken(),
            isUpdating = false
        )
    }
}