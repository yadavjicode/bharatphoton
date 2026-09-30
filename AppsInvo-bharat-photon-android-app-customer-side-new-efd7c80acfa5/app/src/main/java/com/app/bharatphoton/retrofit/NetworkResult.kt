package com.app.bharatphoton.retrofit

sealed class NetworkResult<T>(val data: T? = null, val errorMessage: String? = null) {
    class Error<T>(errorMessage: String) : NetworkResult<T>(errorMessage = errorMessage)

    class Loading<T> : NetworkResult<T>()

    class Success<T>(response: T? = null) : NetworkResult<T>(data = response)

}