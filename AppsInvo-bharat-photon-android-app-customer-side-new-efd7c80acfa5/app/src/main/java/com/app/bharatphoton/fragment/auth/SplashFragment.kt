package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.Utils.getDeviceId
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.databinding.FragmentSplashBinding
import com.app.bharatphoton.retrofit.Constants.SharedPref.AUTH_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }
    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSplashBinding = FragmentSplashBinding.inflate(layoutInflater)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        with(binding) {
            deviceId = getDeviceId(requireContext())
        }
    }


    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(3000)
            if (sharedPrefManager.getString(AUTH_TOKEN, "").isNotBlank()) {
                navigateToScreen(R.id.homeFragment, navOptions = navOptions)
            } else {
                navigateToScreen(R.id.onBoardingFragment, navOptions = navOptions)
            }
        }
    }
}