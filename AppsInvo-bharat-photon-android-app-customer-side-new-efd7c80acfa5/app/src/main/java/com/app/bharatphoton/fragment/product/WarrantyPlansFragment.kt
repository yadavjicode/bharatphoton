package com.app.bharatphoton.fragment.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.activity.MainActivity
import com.app.bharatphoton.activity.PaymentResult
import com.app.bharatphoton.adapter.WarrantyPlansAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.cartItemCount
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.startPayment
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentWarrantyPlansBinding
import com.app.bharatphoton.module.warrantyApiResponse.AvailableWarrantyResponse
import com.app.bharatphoton.module.warrantyApiResponse.RenewWarrantyResponse
import com.app.bharatphoton.module.warrantyApiResponse.VerifyWarrantyPayment
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.WarrantyViewModule
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class WarrantyPlansFragment : BaseFragment<FragmentWarrantyPlansBinding>() {
    private var mode = ""
    private var orderId = ""
    private var productId = ""
    private var razorPayOrderId = ""
    private var sinList = arrayListOf<String>()
    private var razorPayPaymentId = ""
    private var razorPaySignature = ""
    private var warrentyId = ""
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val viewModule by viewModels<WarrantyViewModule>()

    private val warrantyPlansAdapter by lazy {
        WarrantyPlansAdapter { type, data ->
            when (type) {
                "root" -> {
                    warrentyId = data.id ?: ""
                    mode = data.mode ?: ""
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiAvailableWarrantyRequest(productId)
        apiAvailableWarrantyResponse()
        apiRenewWarrantyResponse()
        apiVerifyPaymentResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentWarrantyPlansBinding = FragmentWarrantyPlansBinding.inflate(layoutInflater)

    private fun apiAvailableWarrantyRequest(productId: String) {
        viewModule.availableWarranty(productId)
    }

    private fun apiAvailableWarrantyResponse() {
        viewModule.availableWarranty.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        availableWarrantyResponse = Gson().fromJson(it.data, AvailableWarrantyResponse::class.java)
                        if (availableWarrantyResponse?.code == 1) {
                            val data = availableWarrantyResponse?.data
                            if (availableWarrantyResponse?.data?.isNotEmpty() == true) {
                                warrentyId = data?.get(0)?.id ?: ""
                                mode = data?.get(0)?.mode ?: ""
                                warrantyPlansAdapter.submitList(data)
                                RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.warrantyPlansRv)
                            } else {
                                if (data.isNullOrEmpty()) {
                                    binding.notFound.visibility = View.VISIBLE
                                    binding.continueBtn.visibility = View.GONE
                                } else {
                                    binding.notFound.visibility = View.GONE
                                    binding.continueBtn.visibility = View.VISIBLE
                                }
                            }

                        } else {
                            requireActivity().toast(availableWarrantyResponse?.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiRenewWarrantyRequest() {
        val request = JsonObject().apply {
            addProperty("productId", productId)
            addProperty("orderId", orderId)
            addProperty("warrentyId", warrentyId)
            addProperty("mode", mode)
            addProperty("quantity", sinList.count())
            add("finNo", JsonArray().apply {
                sinList.forEach { add(it) }
            })
        }
        viewModule.renewWarranty(request)
    }

    private fun apiRenewWarrantyResponse() {
        viewModule.renewWarranty.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.continueBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.continueBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.continueBtn.hideLoading()
                    try {
                        renewWarrantyResponse = Gson().fromJson(it.data, RenewWarrantyResponse::class.java)
                        if (renewWarrantyResponse?.code == 1) {
                            startPayment(
                                renewWarrantyResponse?.data?.razorpayOrderId ?: "", activity = requireActivity(), amount = renewWarrantyResponse?.data?.amountInPaisa.toString()
                            )
                            getListenerFromActivity()
                        } else {
                            requireActivity().toast(renewWarrantyResponse?.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun apiVerifyPaymentRequest() {
        val request = JsonObject().apply {
            addProperty("razorpay_order_id", razorPayOrderId)
            addProperty("razorpay_payment_id", razorPayPaymentId)
            addProperty("razorpay_signature", razorPaySignature)
        }
        viewModule.verifyWarrantyPayment(request)
    }

    private fun apiVerifyPaymentResponse() {
        viewModule.verifyWarrantyPayment.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        verifyWarrantyPayment = Gson().fromJson(it.data, VerifyWarrantyPayment::class.java)
                        if (verifyWarrantyPayment?.code == 1) {
                            MainActivity.paymentResultLiveData.postValue(null)
                            navigateToScreen(R.id.paymentSuccessFragment, null, navOptions = navOptions)
//                            requireActivity().toast(verifyWarrantyPayment?.message?:"")
                        } else {
                            requireActivity().toast(verifyWarrantyPayment?.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun getListenerFromActivity() {
        MainActivity.paymentResultLiveData.observe(viewLifecycleOwner) { paymentResult ->
            paymentResult?.let {
                when (paymentResult) {
                    is PaymentResult.Success -> {
                        razorPayPaymentId = paymentResult.paymentId ?: ""
                        razorPayOrderId = paymentResult.orderId ?: ""
                        razorPaySignature = paymentResult.signature ?: ""
                        apiVerifyPaymentRequest()
//                    apiVerifyPaymentResponse()
                    }

                    is PaymentResult.Error -> {
//                    requireActivity().toast("Payment Failed")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.cartItemCount.text = cartItemCount.toString()
            sinList = arguments?.getStringArrayList("sinList") ?: arrayListOf()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            topBar.title.text = getString(R.string.warranty_plans)
            productId = arguments?.getString("ProductId", "") ?: ""
            orderId = arguments?.getString("orderId", "") ?: ""
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }

            continueBtn.setOnAnimatedClickListener {
                apiRenewWarrantyRequest()
            }

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, null, navOptions)
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            warrantyPlansRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = warrantyPlansAdapter
            }
        }
    }

    companion object {
        private var availableWarrantyResponse: AvailableWarrantyResponse? = null
        private var renewWarrantyResponse: RenewWarrantyResponse? = null
        private var verifyWarrantyPayment: VerifyWarrantyPayment? = null
    }
}