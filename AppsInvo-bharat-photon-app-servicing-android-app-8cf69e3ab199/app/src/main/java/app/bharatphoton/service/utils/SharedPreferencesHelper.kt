package app.bharatphoton.service.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

    companion object {
        private var instance: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            if (instance == null) {
                synchronized(SharedPreferencesHelper::class) {
                    instance = SharedPreferencesHelper(context)
                }
            }
            return instance!!
        }
    }

    // Helper methods to save and retrieve data
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun clearSharedPreferences() {
        sharedPreferences.edit().clear().apply()
    }
}

