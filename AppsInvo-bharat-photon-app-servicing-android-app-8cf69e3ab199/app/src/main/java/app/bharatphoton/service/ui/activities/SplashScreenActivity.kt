package app.bharatphoton.service.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.databinding.ActivitySplashScreenBinding
import app.bharatphoton.service.utils.Constants.IS_ALREADY_LOGGED_IN
import app.bharatphoton.service.utils.CrashlyticsConstants.SPLASH
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.SharedPreferencesHelper

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySplashScreenBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(SPLASH,"SplashScreenActivity")

        sharedPreferencesHelper= SharedPreferencesHelper.getInstance(this)
        val alreadyLoggedIn =sharedPreferencesHelper.getString(IS_ALREADY_LOGGED_IN,"False")
        Handler().postDelayed({
            binding.ivSplashAnimation.isVisible=true
            val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            binding.ivSplashAnimation.startAnimation(animation)
        },500)
        Handler().postDelayed({
            if (alreadyLoggedIn == "True"){
                val intent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        },2000)
    }
}