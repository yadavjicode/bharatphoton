package com.app.bharatphoton.fragment.order

import SpinnerDataModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage.notificationCount
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.type
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.zoomInOutAnimation
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentConfirmOrderBinding
import com.app.bharatphoton.module.addressResponse.AddInstallationAddressResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import dropDown
import org.json.JSONObject

@AndroidEntryPoint
class ConfirmOrderFragment : BaseFragment<FragmentConfirmOrderBinding>() {
    private var address = ""
    private var city = ""
    private var installationId = ""
    private var isClicked = false
    private var orderNo = ""
    private var order_Id = ""
    private var renewPlan = ""
    private var screenType = ""
    private var state = ""
    private var zipCode = ""
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val viewModel by viewModels<AddressViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.addInstallationAddress.postValue(null)
        viewModel.updateDeliveryAddress.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiStateRequest()

        apiStateResponse { stateListResponse ->

            binding.stateSpinner.dropDown(list = stateListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.stateSpinner.text = it.title.toString()
                apiCityListRequest(stateName = it.title.toString())
                state = it.title.toString()
            })
        }
        apiCityListResponse { cityListResponse ->
            binding.citySpinner.dropDown(list = cityListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.citySpinner.text = it.title.toString()
                city = it.title.toString()
            })
        }
        apiAddInstallationAddressResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentConfirmOrderBinding = FragmentConfirmOrderBinding.inflate(layoutInflater)

    private fun apiAddInstallationAddressRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("orderId", order_Id)
                addProperty("address", addressEt.text.toString())
                addProperty("ZipCode", zipCodeEt.text.toString())
                addProperty("state", state)
                addProperty("city", city)
            }
            viewModel.addInstallationAddress(request)
        }
    }

    private fun apiAddInstallationAddressResponse() {
        viewModel.addInstallationAddress.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.nextBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {
                    binding.nextBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.nextBtn.hideLoading()
                    try {
                        addInstallationAddress = Gson().fromJson(it.data, AddInstallationAddressResponse::class.java)
                        if (addInstallationAddress?.code == 1) {
                            val bundle = Bundle().apply {
                                putString("orderNo", orderNo)
                                putString("orderId", order_Id)
                                putString("installationId", addInstallationAddress?.data?.id)
                                installationId = addInstallationAddress?.data?.id ?: ""
                            }
                            type.mlog()
                            if (renewPlan == "renewPlan") {
                                navigateToScreen(R.id.homeFragment, bundle = null, navOptions = navOptions)
                            } else {
                                navigateToScreen(R.id.uploadPhotoFragment, bundle = bundle, navOptions = navOptions)
                            }
                        } else {
                            requireActivity().toast(addInstallationAddress?.message ?: "")
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

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.cart.visibility = View.GONE
            topBar.notification.visibility = View.GONE
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            RecyclerAnimationHelper.animation(requireContext(), root)
            topBar.title.visibility = View.GONE
            zoomInOutAnimation(listOf(thumbImg))

            orderNo = arguments?.getString("orderNo") ?: ""
            city = arguments?.getString("city") ?: ""
            address = arguments?.getString("address") ?: ""
            state = arguments?.getString("state") ?: ""
            zipCode = arguments?.getString("zipCode") ?: ""
            order_Id = arguments?.getString("id") ?: ""
            renewPlan = arguments?.getString("screenType") ?: ""



            orderNoTv.text = "Order No. : $orderNo"
            screenType = arguments?.getString("subscribeList", "") ?: ""
            "type: $type".mlog()
            "screenType: $screenType".mlog()
            if (screenType == "subscribeList") {
                installationAddress.visibility = View.VISIBLE
            } else {
                if (type == "Available") installationAddress.visibility = View.VISIBLE else installationAddress.visibility = View.GONE
            }

        }
    }

    private fun onClick() {
        with(binding) {
            topBar.topBarFunctionality.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
            handleOnPressBackButton {
                navigateBack()
            }

            nextBtn.setOnAnimatedClickListener {
                if (validation() && (screenType == "subscribeList" || type == "Available")) {
                    apiAddInstallationAddressRequest()
                } else {
                    if (type == "Not Available") {
                        navigateToScreen(R.id.homeFragment, null, navOptions)
                    }
                }
            }


            sameAddress.setOnAnimatedClickListener {
                with(binding) {
                    if (isClicked) {
                        checkImg.setImageResource(R.drawable.uncheck)
                        isClicked = false

                        zipCodeEt.apply {
                            setText("")
                            alpha = 1f
                            isEnabled = true
                        }
                        stateSpinner.apply {
                            text = ""
                            alpha = 1f
                            isEnabled = true
                        }
                        citySpinner.apply {
                            text = ""
                            alpha = 1f
                            isEnabled = true
                        }
                        addressEt.apply {
                            isEnabled = true
                            alpha = 1f
                            setText("")
                        }
                    } else {
                        setDataInField()
                        checkImg.setImageResource(R.drawable.check)
                        isClicked = true
                    }
                }
            }
        }
    }

    private fun setDataInField() {
        with(binding) {

            zipCodeEt.apply {
                setText(zipCode)
                alpha = 0.5f
                isEnabled = false
            }
            stateSpinner.apply {
                text = state
                isEnabled = false
                alpha = 0.5f
            }
            citySpinner.apply {
                text = city
                alpha = 0.5f
                isEnabled = false
            }

            addressEt.apply {
                isEnabled = false
                alpha = 0.5f
                setText(address)
            }
        }
    }

    private fun validation(): Boolean {
        with(binding) {
            if (addressEt.text.toString().isEmpty()) {
                addressEt.error = getString(R.string.please_enter_your_address)
                return false
            } else if (zipCodeEt.text.toString().isEmpty()) {
                zipCodeEt.error = getString(R.string.please_enter_your_zip_code)
                return false
            }
        }
        return true
    }

    companion object {
        private var addInstallationAddress: AddInstallationAddressResponse? = null
    }

}