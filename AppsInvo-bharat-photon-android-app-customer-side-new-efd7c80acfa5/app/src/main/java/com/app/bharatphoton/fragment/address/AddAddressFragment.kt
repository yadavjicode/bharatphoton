package com.app.bharatphoton.fragment.address

import SpinnerDataModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.hideKeyBoardWithEditTextLength
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentAddAdressBinding
import com.app.bharatphoton.module.addressResponse.AddAddressResponse
import com.app.bharatphoton.module.addressResponse.AddressListResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import dropDown
import org.json.JSONObject

@Suppress("KotlinConstantConditions", "DEPRECATION")
@AndroidEntryPoint
class AddAddressFragment : BaseFragment<FragmentAddAdressBinding>() {
    private var addressId = ""
    private var type = ""
    private var selectState = ""
    private var selectedCity = ""
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val viewModule by viewModels<AddressViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        setFields()
        apiStateRequest()
        apiStateResponse { stateListResponse ->
            binding.stateSpinner.dropDown(list = stateListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.stateSpinner.text = it.title.toString()
                apiCityListRequest(stateName = it.title.toString())
                selectState = it.title.toString()
            })
        }
        apiCityListResponse { cityListResponse ->
            binding.citySpinner.dropDown(list = cityListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.citySpinner.text = it.title.toString()
                selectedCity = it.title.toString()
            })
        }
        apiAddAddressResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentAddAdressBinding = FragmentAddAdressBinding.inflate(layoutInflater)

    private fun apiAddAddressRequest() {
        with(binding) {
            val request = JsonObject().apply {
                if (addressId != "") {
                    addProperty("id", addressId)
                }
                addProperty("firstName", firstName.text.toString())
                addProperty("lastName", lastName.text.toString())
                addProperty("country_code", ccp.selectedCountryCodeWithPlus.toString())
                addProperty("phoneNo", phoneNumber.text.toString().trim())
                addProperty("address", address.text.toString())
                addProperty("ZipCode", zipcode.text.toString())
                addProperty("state", selectState)
                addProperty("city", selectedCity)
                addProperty("addressType", "0")
            }
            viewModule.addAddress(request)
        }
    }

    private fun apiAddAddressResponse() {
        viewModule.addAddress.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.submitBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        //
                    }
                }

                is NetworkResult.Loading -> binding.submitBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.submitBtn.hideLoading()
                    try {
                        addAddressListResponse = Gson().fromJson(it.data, AddAddressResponse::class.java)
                        if (addAddressListResponse?.code == 1) {
                            navigateBack()
                        } else {
                            requireActivity().toast(addAddressListResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        //
                    }
                }
                else -> {

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            apiCartCountResponse { count ->
                binding.topBar.cartItemCount.text = count.toString()
            }
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            handleOnPressBackButton { navigateBack() }
            addressListResponse = arguments?.getParcelable("addressData")
            type = arguments?.getString("type") ?: ""
            hideKeyBoardWithEditTextLength(editText = zipcode, context = requireContext(), view = requireView(), length = 6)
            hideKeyBoardWithEditTextLength(editText = phoneNumber, context = requireContext(), view = requireView(), length = 10)
        }
    }

    private fun setClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            submitBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiAddAddressRequest()
                }
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
        }
    }

    private fun setFields() {
        with(binding) {
            firstName.setText(arguments?.getString("firstName", ""))
            lastName.setText(arguments?.getString("lastName", ""))
            phoneNumber.setText(arguments?.getString("phoneNumber", ""))
            address.setText(arguments?.getString("address", ""))
            zipcode.setText(arguments?.getString("zipCode", ""))
            addressId = arguments?.getString("addressId") ?: ""
            selectState = arguments?.getString("state", "") ?: ""
            selectedCity = arguments?.getString("city", "") ?: ""
            stateSpinner.text = selectState
            citySpinner.text = selectedCity
            if (selectState != "") {
                apiCityListRequest(stateName = selectState)
            }
            if (addressId == "") {
                topBar.title.text = getString(R.string.add_address)
            } else {
                topBar.title.text = getString(R.string.edit_address)
            }
        }
    }

    private fun validate(): Boolean {
        with(binding) {
            if (firstName.text.toString().trim().isBlank()) {
                firstName.error = getString(R.string.please_enter_first_name)
                return false
            } else if (lastName.text.toString().isBlank()) {
                lastName.error = getString(R.string.please_enter_last_name)
                return false
            } else if (phoneNumber.text.toString().isBlank()) {
                phoneNumber.error = getString(R.string.please_enter_mobile_number)
                return false
            } else if (phoneNumber.text.toString().length <= 4) {
                phoneNumber.error = getString(R.string.please_enter_valid_mobile_number)
                return false
            } else if (address.text.toString().isBlank()) {
                address.error = getString(R.string.please_enter_email_address)
                return false
            } else if (zipcode.text.toString().isBlank()) {
                zipcode.error = getString(R.string.please_enter_zip_code)
                return false
            }
        }
        return true
    }

    companion object {
        private var addAddressListResponse: AddAddressResponse? = null
        private var addressListResponse: AddressListResponse? = null
    }
}