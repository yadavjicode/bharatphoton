package com.app.bharatphoton.application

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@Suppress("DEPRECATION")
@HiltAndroidApp
class myApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
        FirebaseApp.initializeApp(this)
//        AndroidThreeTen.init(this)

    }
}