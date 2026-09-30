package com.app.bharatphoton.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.app.bharatphoton.R
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.Constant.SharedPref.PROFILE_PIC
import com.app.bharatphoton.common.SharedData.accessKey
import com.app.bharatphoton.common.SharedData.bucketName
import com.app.bharatphoton.common.SharedData.region
import com.app.bharatphoton.common.SharedData.secretKey
import com.app.bharatphoton.common.SharedPrefManager
import com.app.bharatphoton.common.SharedViewModule
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.databinding.ActivityMainBinding
import com.app.bharatphoton.module.authResponse.S3Response
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {


    private lateinit var navController: NavController

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val navigationMap by lazy {
        mapOf(R.id.homeFragment to 0, R.id.categoriesFragment to 1, R.id.myOrderFragment to 2, R.id.profileFragment to 3)
    }

    private val viewModule by viewModels<AuthViewModule>()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(this)[SharedViewModule::class.java]

        setContentView(binding.root)
        setUpNavigationComponent()
        initData()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        apiS3Request()
        apiS3Response()
        observeProfilePicture()
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        paymentResultLiveData.value = PaymentResult.Error(p0, p1)
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        val razorpaySignature = p1?.signature
        val razorpayPaymentId = p1?.paymentId
        val razorpayOrderId = p1?.orderId
        paymentResultLiveData.value = PaymentResult.Success(
            paymentId = razorpayPaymentId ?: "", orderId = razorpayOrderId, signature = razorpaySignature
        )
    }

    fun setStatusBarColor(colorRes: Int, useLightIcons: Boolean) {
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, colorRes)

        val decor = window.decorView
        decor.systemUiVisibility = if (useLightIcons) {
            decor.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else {
            decor.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }


    private fun NavController.navUpdateSelectedTabUI(selectedIndex: Int) {
        binding.bottomBar.LayoutMain.forEachIndexed { index, view ->
            val child = view as LinearLayout
            val imageView = child.getChildAt(0) as ImageView
            val textView = child.getChildAt(1) as TextView

            val isSelected = index == selectedIndex

            child.apply {
                setBackgroundResource(if (isSelected) R.drawable._style_bottom_nav_tab else 0)
                isEnabled = !isSelected
                setOnClickListener {
                    navigationMap.entries.find { map -> map.value == index }?.key?.let { destinationId -> navigate(destinationId) }
                }
            }

            imageView.isEnabled = !isSelected
            textView.visibility = if (isSelected) View.VISIBLE else View.GONE

        }
    }

    private fun apiS3Request() {
        viewModule.s3()
    }

    private fun apiS3Response() {
        viewModule.s3.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    s3Response = Gson().fromJson(it.data, S3Response::class.java)
                    try {
                        if (s3Response?.code == 1) {
                            bucketName = s3Response?.data?.bucketName.toString()
                            accessKey = s3Response?.data?.accessKeyId.toString()
                            secretKey = s3Response?.data?.secretAccessKey.toString()
                            val fullRegion = s3Response?.data?.region.toString()
                            region = fullRegion.substringAfterLast(" ")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    //
                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            val profilePic = sharedPrefManager.getString(PROFILE_PIC)
            Glide.with(this@MainActivity).load(profilePic).placeholder(R.drawable.placeholder).into(bottomBar.ImageViewProfile)
        }
    }

    private fun observeProfilePicture() {
        sharedViewModel._profilePicUrl.observe(this) { profilePicUrl ->
            Glide.with(this).load(profilePicUrl).placeholder(R.drawable.placeholder).into(binding.bottomBar.ImageViewProfile)
        }
    }

    private fun setUpNavigationComponent() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navHost = navHostFragment.navController

        navHost.apply {
            setGraph(R.navigation.nav_graph)
            addOnDestinationChangedListener { _, destination, _ ->
                navigationMap[destination.id]?.let { index -> navUpdateSelectedTabUI(index) }
                val authToken = sharedPrefManager.getString(Constant.SharedPref.AUTH_TOKEN)

                when (destination.id) {
                    R.id.myOrderFragment, R.id.profileFragment -> {
                        if (authToken.isBlank()) {
                            navigate(R.id.loginFragment)
                        } else {
                            binding.bottomBar.LayoutMain.visibility = View.VISIBLE
                        }
                    }

                    R.id.homeFragment, R.id.categoriesFragment -> {
                        binding.bottomBar.LayoutMain.visibility = View.VISIBLE
                    }

                    else -> {
                        binding.bottomBar.LayoutMain.visibility = View.GONE
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 100
        private var s3Response: S3Response? = null
        val paymentResultLiveData = MutableLiveData<PaymentResult?>()
        private lateinit var sharedViewModel: SharedViewModule

    }

}

sealed class PaymentResult {

    data class Error(val errorCode: Int, val errorMessage: String?) : PaymentResult()

    data class Success(val paymentId: String?, val orderId: String?, val signature: String?) : PaymentResult()

}