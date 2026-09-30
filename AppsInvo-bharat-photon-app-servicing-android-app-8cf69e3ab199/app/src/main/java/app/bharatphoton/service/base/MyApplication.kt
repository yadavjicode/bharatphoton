package app.bharatphoton.service.base

import android.app.Application
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatDelegate
import androidx.media3.common.util.UnstableApi
import app.bharatphoton.service.BuildConfig
import app.bharatphoton.service.utils.CacheManager
import app.bharatphoton.service.utils.CrashlyticsConstants.BUILD_TYPE
import app.bharatphoton.service.utils.CrashlyticsConstants.VERSION_CODE
import app.bharatphoton.service.utils.CrashlyticsConstants.VERSION_NAME
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.mLog
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            // Initialize Firebase and Add Crashlytics acc to the build
            FirebaseApp.initializeApp(this)
            BuildConfig.DEBUG.mLog()
            CrashlyticsHelper.enableCrashlytics(!BuildConfig.DEBUG)
            CrashlyticsHelper.setCustomKey(BUILD_TYPE, BuildConfig.BUILD_TYPE)
            CrashlyticsHelper.setCustomKey(VERSION_NAME, BuildConfig.VERSION_NAME.toString())
            CrashlyticsHelper.setCustomKey(VERSION_CODE, BuildConfig.VERSION_CODE.toString())
    }

    @OptIn(UnstableApi::class)
    override fun onTerminate() {
        super.onTerminate()
        CacheManager.releaseCache()
    }

}