package app.bharatphoton.service.utils

import android.content.Context
import android.net.http.HttpException
import android.util.Log
import android.widget.Toast
import app.bharatphoton.service.base.BaseActivity
import com.google.gson.JsonSyntaxException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

object ApiErrorHandler {

    fun handleException(context: Context, throwable: Throwable) {
        val message = when (throwable) {
            is UnknownHostException -> "Unable to reach server. Please check your internet connection."
            is SocketTimeoutException -> "The server is not responding. Please try again later."
            is ConnectException -> "Failed to connect to the server. Please try again later."
            is SSLHandshakeException -> "Secure connection failed. Please check your network security settings."
            is retrofit2.HttpException -> getHttpExceptionMessage(throwable.code())
            is IOException -> "Network error occurred. Please check your connection and try again."
            is JsonSyntaxException -> "Data error. Please try again later."
            is IllegalStateException -> "An unexpected error occurred. Please try again later."
            else -> "Something went wrong."
        }

        (context as BaseActivity).showAnimatedCustomToast(message)
    }

    private fun getHttpExceptionMessage(code: Int): String {
        return when (code) {
            400 -> "Invalid request. Please try again."
            401 -> "Authentication failed. Please log in again."
            403 -> "Access denied. You are not authorized to perform this action."
            404 -> "Requested resource not found."
            500 -> "Server is not responding. Please try again later."
            502,503,504 -> "Server is not responding. Please try again later."
            else -> "Please try again."
        }
    }
}

