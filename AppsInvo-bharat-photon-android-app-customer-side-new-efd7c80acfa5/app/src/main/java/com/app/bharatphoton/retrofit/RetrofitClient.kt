package com.app.bharatphoton.retrofit

import com.app.bharatphoton.BuildConfig
import com.app.bharatphoton.common.SharedPrefManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RetrofitClient @Inject constructor(private val sharedPrefManager: SharedPrefManager) {

    private val defaultRequestInterceptor = Interceptor { chain ->
        val authToken = sharedPrefManager.getString(com.app.bharatphoton.retrofit.Constants.SharedPref.AUTH_TOKEN)
        val requestBuilder = chain.request().newBuilder().addHeader("Accept", "application/json")
        requestBuilder.addHeader("Connection", "close")
        if (authToken.isNotBlank()) {
            requestBuilder.addHeader("authtoken", authToken)
        }
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val gson: Gson = GsonBuilder().setLenient().setPrettyPrinting().create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okhttpClient = OkHttpClient.Builder().apply {
        addNetworkInterceptor(loggingInterceptor)
        addInterceptor(defaultRequestInterceptor)
        connectTimeout(com.app.bharatphoton.retrofit.RetrofitClient.Companion.TIME_OUT, TimeUnit.SECONDS)
        readTimeout(com.app.bharatphoton.retrofit.RetrofitClient.Companion.TIME_OUT, TimeUnit.SECONDS)
        writeTimeout(com.app.bharatphoton.retrofit.RetrofitClient.Companion.TIME_OUT, TimeUnit.SECONDS)
    }.build()

    val retrofit: Retrofit = Retrofit.Builder().baseUrl(com.app.bharatphoton.retrofit.RetrofitClient.Companion.BASE_URL).client(okhttpClient).addConverterFactory(
        GsonConverterFactory.create(gson)
    ).build()

    companion object {
        private const val TIME_OUT = 30L
        private const val BASE_URL = BuildConfig.BASE_URL
    }
}