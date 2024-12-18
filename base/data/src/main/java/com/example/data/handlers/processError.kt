package com.example.data.handlers

import org.json.JSONObject
import retrofit2.Response

class InputValidationException(message: String) : Exception(message)

fun <T> processError(res: Response<T>): T { // IMPLEMENT ON DEBUG
    if (res.isSuccessful) {
        return res.body()!!
    } else {
        if (res.code() == 401) {
            throw Exception("Access denied")
        }

        val errMsg = res.errorBody()?.string()?.let {
            try{
                JSONObject(it).getJSONArray("errors").get(0).toString()
            } catch (e: Exception) {
                JSONObject(it).getString("message")
            }
        } ?: run {
            res.code().toString()
        }

        throw InputValidationException(errMsg)
    }
}