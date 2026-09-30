package com.app.bharatphoton.fragment.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.HomeCategoryProductsAdapter
import com.app.bharatphoton.adapter.HomeOffersViewPagerAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.getDeviceId
import com.app.bharatphoton.common.Utils.hideKeyboard
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopUpDialog
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentHomeBinding
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.module.authResponse.UpdateDeviceTokenResponse
import com.app.bharatphoton.module.homePageResponse.HomePageResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.app.bharatphoton.viewModule.HomePageViewModule
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

@Suppress("DEPRECATION", "DEPRECATION")
@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private var imageChangeJob: Job? = null
    private var productList = mutableListOf<Module>()
    private val authViewModule by viewModels<AuthViewModule>()

    private val homeProductsAdapter by lazy {
        HomeCategoryProductsAdapter { data, type ->
            when (type) {
                "products" -> {
                    val bundle = Bundle().apply {
                        putString("categoryId", data.id)
                    }
                    navigateToScreen(
                        id = R.id.productsFragment, bundle = bundle, navOptions = navOptions
                    )
                }
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<HomePageViewModule>()
    private val image = mutableListOf<String>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycleScope.coroutineContext.cancelChildren()
        viewLifecycleOwner.lifecycleScope.cancel()
        productList.clear()
        imageChangeJob?.cancel()
        image.clear()
    }

    override fun onResume() {
        super.onResume()
        binding.search.searchEditText.setText("")
        setDataIntoViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        search()
        fcmToken()
        apiUpdateTokenResponse()
        apiNotificationCountRequest()
        apiNotificationCountResponse {
            SharedData.notificationCount = it.count ?: 0
            binding.topBar.notificationCountTv.text = it.count.toString()
        }
        setDataInAdapter()
        apiHomePageRequest()
        apiHomePageResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)

    private fun apiHomePageRequest() {
        viewModule.homePage()
    }

    private fun apiHomePageResponse() {
        viewModule.homePage.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Loading && homePageResponse == null) showLoader() else hideLoader()
            if (it is NetworkResult.Error) {
                try {
                    val message = JSONObject(it.errorMessage ?: "").optString("message")
                    requireActivity().toast(message)
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
            if (it is NetworkResult.Success) {
                try {
                    homePageResponse = Gson().fromJson(it.data, HomePageResponse::class.java)
                    if (homePageResponse?.code == 1) {
                        binding.homeFragment.visibility = View.VISIBLE
                        val userType = homePageResponse?.data?.cms?.userType.toString()
                        sharedPrefManager.saveString("userType", userType)
                        setDataIntoViews()

                    } else {
                        requireActivity().toast(homePageResponse?.message ?: "")
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    private fun apiUpdateTokenRequest(deviceToken: String) {
        val request = JsonObject().apply {
            addProperty("deviceType", "android")
            addProperty("deviceToken", deviceToken)
        }
        authViewModule.updateDeviceToken(request)
    }

    private fun apiUpdateTokenResponse() {
        authViewModule.updateDeviceToken.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    try {
                        updateDeviceTokenResponse = Gson().fromJson(it.data, UpdateDeviceTokenResponse::class.java)
                        if (updateDeviceTokenResponse?.code == 1) {
//
                        }
                    } catch (e: Exception) {
                        //
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun fcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            } else {
                apiUpdateTokenRequest(task.result)
            }
        }
    }

    private fun imageChangeAutomatic() {
        imageChangeJob?.cancel()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(3000)
                val currentItem = binding.viewPager.currentItem
                val nextItem = if (currentItem + 1 >= image.size) 0 else currentItem + 1
                binding.viewPager.setCurrentItem(nextItem, true)
            }
        }
    }

    private fun initData() {
        with(binding) {
            binding.search.filter.visibility = View.GONE
            deviceId = getDeviceId(requireContext())
            apiCartCountRequest(deviceId = deviceId)
            apiCartCountResponse { count ->
                binding.topBar.cartItemCount.text = count.toString()
            }
            RecyclerAnimationHelper.animation(requireContext(), root)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    showPopUpDialog(context = requireContext(), message = getString(R.string.please_click_on_ok_button_to_enable_notifications), onOk = {
                        checkAndRequestNotificationPermission()
                    })
                }
            }

            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
            }


            refresh { onComplete ->
                apiHomePageRequest()
                onComplete()
            }


        }
    }


    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001 // Request code
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) { // Notification permission request
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast(getString(R.string.notification_permission_granted))
            } else {
                // Permission denied
                showToast(getString(R.string.notification_permission_is_required_for_alerts))
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onClick() {
        with(binding) {
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(
                    id = R.id.cartFragment, bundle = null, navOptions = navOptions
                )
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(
                    id = R.id.notificationFragment, bundle = null, navOptions = navOptions
                )
            }
            myProducts.setOnAnimatedClickListener {
                if (sharedPrefManager.getString(Constant.SharedPref.AUTH_TOKEN).isNotBlank()) {
                    navigateToScreen(
                        id = R.id.myProductFragment, bundle = null, navOptions = navOptions
                    )
                } else {
                    navigateToScreen(R.id.loginFragment, null, navOptions)
                }
            }
            mySubscription.setOnAnimatedClickListener {
                if (sharedPrefManager.getString(Constant.SharedPref.AUTH_TOKEN).isNotBlank()) {
                    navigateToScreen(
                        id = R.id.mySubscriptionFragment, bundle = null, navOptions = navOptions
                    )
                } else {
                    navigateToScreen(R.id.loginFragment, null, navOptions)
                }
            }
            viewMore.setOnAnimatedClickListener {
                navigateToScreen(R.id.categoriesFragment, null, navOptions)
            }
        }
    }

    private fun search() {
        with(binding) {
            search.searchEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val searchQuery = v.text.toString().trim()
                    if (searchQuery.isNotEmpty()) {
                        val bundle = Bundle().apply {
                            putString("search", searchQuery)
                            putString("type", "home")
                        }
                        hideKeyboard(context = requireContext(), view = requireView())
                        navigateToScreen(
                            id = R.id.productsFragment, bundle = bundle, navOptions = navOptions
                        )
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            val gridLayoutManager = GridLayoutManager(requireContext(), 6)

            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        0, 1, 2 -> 2
                        3, 4 -> 3
                        else -> 1
                    }
                }
            }
            categoryRv.apply {
                itemAnimator = null
                layoutManager = gridLayoutManager
                adapter = homeProductsAdapter
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoViews() {
        homePageResponse?.data?.let { data ->
            val newImages = data.banner?.mapNotNull { it?.appImage }?.filter { it.isNotEmpty() } ?: emptyList()

            if (image != newImages) {
                image.clear()
                image.addAll(newImages)
                viewPager()
            }

            val costValue = formatPrice(price = data.costSavedData?.totalCostSaved ?: 0.0, currencyType = "INR")
            binding.costSavedTv.text = costValue
            binding.waterSavedTv.text = "${data.costSavedData?.totalWaterSaved} Ltr"
            homeProductsAdapter.submitList(data.categories)
        }
    }


    private fun viewPager() {
        val adapter = HomeOffersViewPagerAdapter {

        }
        binding.viewPager.adapter = adapter
        adapter.submitList(image)
        binding.dotsIndicator.attachTo(binding.viewPager)

        if (image.isNotEmpty()) {
            binding.notFound.visibility = View.GONE
            imageChangeAutomatic()
        } else {
            binding.notFound.visibility = View.VISIBLE
        }
    }

    companion object {
        private var homePageResponse: HomePageResponse? = null
        private var updateDeviceTokenResponse: UpdateDeviceTokenResponse? = null
    }

}