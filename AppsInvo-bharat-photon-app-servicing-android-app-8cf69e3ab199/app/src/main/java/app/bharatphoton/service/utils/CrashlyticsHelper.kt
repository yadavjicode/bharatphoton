package app.bharatphoton.service.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsHelper {

    val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
    private const val MAX_LOG_LENGTH = 300

    fun log(message: String) {
        firebaseCrashlytics.log(message)
    }

    fun logException(exception: Throwable) {
        firebaseCrashlytics.recordException(exception)
    }

    fun setCustomKey(key: String, value: String) {
       firebaseCrashlytics.setCustomKey(key, value)
    }

    fun enableCrashlytics(enable: Boolean) {
        firebaseCrashlytics.isCrashlyticsCollectionEnabled = enable
    }

    fun logDebug(tag: String, message: String?) {
        val logMessage = "$tag → ${message?.take(MAX_LOG_LENGTH) ?: "null"}"
        log(logMessage)
        "$tag- $logMessage".mLog()
    }

    fun logError(tag: String, error: Throwable, message: String? = null) {
        val logMessage = "$tag → ${message ?: error.message}"
        log(logMessage)
        logException(error)
        "$tag- $logMessage- $error".mLog()
    }
}