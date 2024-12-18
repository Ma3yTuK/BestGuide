package com.example.data.repositories

import android.util.Base64
import com.example.data.Api
import com.example.data.handlers.processError
import com.example.data.models.UserModel

object AuthRepository {
    suspend fun getToken(username: String, password: String): String {
        return processError(Api.authService.getToken("Basic " + Base64.encodeToString(("$username:$password").toByteArray(), Base64.NO_WRAP)))
    }

    suspend fun register(user: UserModel): String {
        return processError(Api.authService.register(user))
    }
}