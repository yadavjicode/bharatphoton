package app.bharatphoton.service.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.error401.Error401
import app.bharatphoton.service.api_model_classes.update_device_token.request.UpdateDeviceTokenRequest
import app.bharatphoton.service.api_model_classes.update_device_token.response.UpdateDeviceTokenResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityHomeBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.fragments.AssignedTasksFragment
import app.bharatphoton.service.ui.fragments.NotificationFragment
import app.bharatphoton.service.ui.fragments.ProfileFragment
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.NAME
import app.bharatphoton.service.utils.Constants.PROFILE_PIC
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class HomeActivity : BaseActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token : String? = null
    private var firstItem = 1
    private var secondItem = 2
    private var thirdItem = 3

    // Register the activity result launcher for requesting notification permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            showAnimatedCustomToast("Notification Permission Granted")
        } else {
            showAnimatedCustomToast("Notification Permission Denied")
        }
    }

    override fun onResume() {
        super.onResume()
        profileDetails()

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        //Generating firebase FCM token for notification
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val fcmToken = task.result
            try {
                updateDeviceTokenApi(fcmToken)
            }catch (e:Exception){ }
        })
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        if (!isNotificationPermissionGranted()) {
            requestNotificationPermission()
        }

        runOnUiThread {
            val navigateTo = intent.getStringExtra("NAVIGATE_TO")
            when(navigateTo){
                "NOTIFICATION" -> navigateToNotificationFragment()
                else -> navigateToAssignedTaskFragment()
            }
        }

        getStorageDetailsApiCall(this)

        //Bottom Navigation
        binding.llNav1.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            navigateToNotificationFragment()
        }

        binding.llNav2.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            navigateToAssignedTaskFragment()
        }

        binding.llNav3.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
            navigateToProfileFragment()
        }

        // Handle the back press event
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backStackEntryCount = supportFragmentManager.backStackEntryCount
                if (backStackEntryCount > 0) {
                    showAnimatedCustomToast("Press again to exit!")
                    for (i in 0 until backStackEntryCount) {
                        supportFragmentManager.popBackStack()
                    }

                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.flFrame,AssignedTasksFragment())
                    fragmentTransaction.commit()
                    binding.llNavBg.setBackgroundResource(R.drawable.nav2)
                    binding.llNav2.visibility = View.INVISIBLE
                    binding.llNav1.isVisible = true
                    binding.llNav3.isVisible = true
                    binding.llSelectedNav2.isVisible = true
                    binding.llSelectedNav1.isVisible = false
                    binding.llSelectedNav3.isVisible = false

                    supportFragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                } else {
                    finish()
                }
            }
        })
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below, notifications are enabled by default
            true
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showAnimatedCustomToast("Notification permission is not required for this version.")
        }
    }

    private fun replaceFragment(fragment: Fragment, selectedItem: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flFrame, fragment).addToBackStack(selectedItem.toString())
        transaction.commit()
    }

    private fun navigateToNotificationFragment() {
        binding.llNavBg.setBackgroundResource(R.drawable.nav1)

        binding.llNav1.visibility = View.INVISIBLE
        binding.llNav2.isVisible = true
        binding.llNav3.isVisible = true

        binding.llSelectedNav1.isVisible = true
        binding.llSelectedNav2.isVisible = false
        binding.llSelectedNav3.isVisible = false

        replaceFragment(NotificationFragment(),firstItem)
    }

    private fun navigateToAssignedTaskFragment() {
        binding.llNavBg.setBackgroundResource(R.drawable.nav2)

        binding.llNav2.visibility = View.INVISIBLE
        binding.llNav1.isVisible = true
        binding.llNav3.isVisible = true

        binding.llSelectedNav2.isVisible = true
        binding.llSelectedNav1.isVisible = false
        binding.llSelectedNav3.isVisible = false

        replaceFragment(AssignedTasksFragment(),secondItem)
    }

    private fun navigateToProfileFragment(){
        binding.llNav3.visibility = View.INVISIBLE
        binding.llNav1.isVisible = true
        binding.llNav2.isVisible = true

        binding.llSelectedNav3.isVisible = true
        binding.llSelectedNav1.isVisible = false
        binding.llSelectedNav2.isVisible = false

        binding.llNavBg.setBackgroundResource(R.drawable.nav3)

        replaceFragment(ProfileFragment(),thirdItem)
    }

    private fun updateDeviceTokenApi(fcmToken : String) {
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val requestBody = UpdateDeviceTokenRequest(deviceId = getDeviceId(this), deviceToken = fcmToken, deviceType = "android")
        val getDetail = retrofitBuilder.updateDeviceToken(token.toString(), requestBody)
        getDetail.enqueue(object : Callback<UpdateDeviceTokenResponse> {
            override fun onResponse(call: Call<UpdateDeviceTokenResponse>, response: Response<UpdateDeviceTokenResponse>)
            {
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1){
//                    val successMessage = response.body()?.message ?: "Token Updated."
                    }
                    else if (response.code() == 401) {
                        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), Error401::class.java)
                        showAnimatedCustomToast(errorResponse.message ?: "")
                    }
                    else if (response.body()?.code == 0){
                        showAnimatedCustomToast(response.body()?.message ?: "")
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@HomeActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("updateDeviceTokenApi-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<UpdateDeviceTokenResponse>, t: Throwable) {
                try {
                    ApiErrorHandler.handleException(this@HomeActivity, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("updateDeviceTokenApi-API call failed ${e.message}")
                }
            }
        })
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @SuppressLint("SetTextI18n")
    fun profileDetails(){

        profileDetailsApiCall(this, token, sharedPreferencesHelper)

        val name = sharedPreferencesHelper.getString(NAME,"NA")
        val profilePic = sharedPreferencesHelper.getString(PROFILE_PIC,"")
        binding.tvName.text = "Hi,\n$name"
        Glide.with(this)
            .load(profilePic)
            .placeholder(R.drawable.default_gray_person_image)
            .error(R.drawable.default_gray_person_image)
            .into(binding.ivProfile)
    }

}