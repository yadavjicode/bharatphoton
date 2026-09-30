package com.app.bharatphoton.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentLocationBinding


class LocationFragment : BaseFragment<FragmentLocationBinding>() {
    private val navOption by lazy {
        navigateWithAnimation(requireContext())
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentLocationBinding = FragmentLocationBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
    }

    private fun setClick() {
        with(binding) {
            allowLocation.setOnAnimatedClickListener {
//                requestLocationUpdate()
            }
            manuallyLocation.setOnAnimatedClickListener {
                navigateToScreen(R.id.loginFragment, null, navOption)
            }
        }
    }

    private fun initData() {

    }
}