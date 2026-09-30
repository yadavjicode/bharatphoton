package com.app.bharatphoton.fragment.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Utils.chaneColoOfOneTextView
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentPaymentSuccessfullBinding


class PaymentSuccessFragment : BaseFragment<FragmentPaymentSuccessfullBinding>() {
    private val navOption by lazy { navigateWithAnimation(requireContext()) }
    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentPaymentSuccessfullBinding = FragmentPaymentSuccessfullBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
    }

    private fun onClick() {
        with(binding) {
            backToHomeBtn.setOnAnimatedClickListener {
                navigateToScreen(R.id.homeFragment, null, navOption)
            }
        }
    }

    private fun initData() {
        with(binding) {
            handleOnPressBackButton { navigateBack() }
            chaneColoOfOneTextView(
                string = getString(R.string.your_product_warranty_is_updated_successfully),
                wordToColor = "Successfully!",
                context = requireContext(),
                color = R.color.Deep_Saffron,
                finalWord = warranty
            )
        }
    }
}