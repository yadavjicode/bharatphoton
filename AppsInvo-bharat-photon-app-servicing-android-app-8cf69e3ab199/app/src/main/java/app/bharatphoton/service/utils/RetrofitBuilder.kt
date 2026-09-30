package app.bharatphoton.service.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import app.bharatphoton.service.BuildConfig
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHTTP = OkHttpClient.Builder().addInterceptor(logger)

    private val builder = Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHTTP.build())
    private val retrofit = builder.build()
    fun <T> buildRetrofit(serviceType: Class<T>):T{
        return retrofit.create(serviceType)
    }

}