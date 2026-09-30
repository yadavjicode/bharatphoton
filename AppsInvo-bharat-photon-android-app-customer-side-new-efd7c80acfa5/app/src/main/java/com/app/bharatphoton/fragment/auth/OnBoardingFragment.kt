package com.app.bharatphoton.fragment.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.zoomInOutAnimation
import com.app.bharatphoton.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>() {
    private val navOption by lazy {
        navigateWithAnimation(requireContext())
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentOnBoardingBinding = FragmentOnBoardingBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        zoomInOutAnimation(listOf(binding.next))
    }

    private fun setClick() {
        with(binding) {
            next.setOnAnimatedClickListener {
                navigateToScreen(R.id.loginFragment, null, navOption)
            }
        }
    }

    private fun initData() {

    }

}
