@file:Suppress("KotlinConstantConditions")

package com.app.bharatphoton.fragment.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.app.bharatphoton.R
import com.app.bharatphoton.common.SharedPrefManager
import com.app.bharatphoton.common.SpinnerHelper
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentChangeAddressBottomSheetBinding
import com.app.bharatphoton.module.addressResponse.AddAddressResponse
import com.app.bharatphoton.module.listingResponse.CityListResponse
import com.app.bharatphoton.module.listingResponse.StateListResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.app.bharatphoton.viewModule.ListingViewModule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class ChangeAddressBottomSheetFragment(val onSuccess: ((AddAddressResponse) -> Unit)? = null) : BottomSheetDialogFragment() {

    private var cityItem = ""
    private var selectedItem = ""
    private var address = ""
    private var zipCode = ""
    private var addressId = ""
    private var firstName = ""
    private var lastName = ""
    private var phoneNumber = ""
    private val binding by lazy { FragmentChangeAddressBottomSheetBinding.inflate(layoutInflater) }
    private val listViewModule by viewModels<ListingViewModule>()
    private val viewModule by viewModels<AddressViewModule>()
    private val cityList = mutableListOf<String>()
    private val stateList = mutableListOf<String>()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        addField()
        apiStateRequest()
        apiStateResponse()
        apiCityListResponse()
        stateSpinner()
        apiAddAddressResponse()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun LifecycleOwner.handleOnPressBackButton(onBackPressed: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed.invoke() }
    }

    private fun addField() {
        with(binding) {
            address = arguments?.getString("address") ?: ""
            zipCode = arguments?.getString("zipCode") ?: ""
            phoneNumber = arguments?.getString("phoneNumber") ?: ""
            firstName = arguments?.getString("firstName") ?: ""
            lastName = arguments?.getString("lastName") ?: ""
            val state = arguments?.getString("state")
            val city = arguments?.getString("city")
            addressId = arguments?.getString("addressId") ?: ""

            addressEt.setText(address)
            zipCodeEt.setText(zipCode)
            phoneNumberEt.setText(phoneNumber)
            firstNameEt.setText(firstName)
            lastNameEt.setText(lastName)

            if (stateList.isNotEmpty()) {
                val stateIndex = stateList.indexOf(state)
                if (stateIndex != -1) {
                    stateSpinner.setSelection(stateIndex)
                }
            }

            if (cityList.isNotEmpty()) {
                val cityIndex = cityList.indexOf(city)
                if (cityIndex != -1) {
                    citySpinner.setSelection(cityIndex)
                }
            }

        }
    }

    private fun apiAddAddressRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("id", addressId)
                addProperty("firstName", firstNameEt.text.toString())
                addProperty("lastName", lastNameEt.text.toString())
                addProperty("country_code", ccp.selectedCountryCodeWithPlus.toString())
                addProperty("phoneNo", phoneNumberEt.text.toString().trim())
                addProperty("address", addressEt.text.toString())
                addProperty("ZipCode", zipCodeEt.text.toString())
                addProperty("state", selectedItem)
                addProperty("city", cityItem)
                addProperty("addressType", "0")
            }
            viewModule.addAddress(request)
        }
    }

    private fun apiAddAddressResponse() {
        viewModule.addAddress.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.done.hideLoading()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    binding.done.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.done.showLoading()
                is NetworkResult.Success -> {
                    binding.done.hideLoading()
                    try {
                        addAddressResponse = Gson().fromJson(it.data, AddAddressResponse::class.java)
                        if (addAddressResponse?.code == 1) {
                            onSuccess?.invoke(addAddressResponse!!)
                            dismiss()
                        } else {
                            requireActivity().toast(addAddressResponse?.message.toString())
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

    private fun apiCityListResponse() {
        listViewModule.cityList.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {

                    try {
                        cityListResponse = Gson().fromJson(it.data, CityListResponse::class.java)
                        if (cityListResponse?.code == 1) {
                            cityList.clear()
                            cityListResponse?.data?.forEach { city ->
                                cityList.add(city ?: "")
                            }
                            spinnerCity()
                            val selectedCity = arguments?.getString("city", "")
                            val cityIndex = cityList.indexOf(selectedCity)
                            if (cityIndex != -1) {
                                binding.citySpinner.setSelection(cityIndex)
                            }
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

    private fun apiCityLitRequest(stateName: String) {
        listViewModule.cityListing(stateName)
    }

    private fun apiStateRequest() {
        listViewModule.stateListing()
    }

    private fun apiStateResponse() {
        listViewModule.stateList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {

                }

                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    try {
                        stateListResponse = Gson().fromJson(it.data, StateListResponse::class.java)
                        if (stateListResponse?.code == 1) {
                            stateList.clear()
                            stateListResponse?.data?.forEach { state ->
                                stateList.add(state ?: "")
                            }

                            SpinnerHelper.spinner(context = requireContext(), spinner = binding.stateSpinner, statusList = stateList)
                            val selectedState = arguments?.getString("state", "")
                            val stateIndex = stateList.indexOf(selectedState)
                            if (stateIndex != -1) {
                                binding.stateSpinner.setSelection(stateIndex)
                            }
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

    private fun initData() {
        with(binding) {
            handleOnPressBackButton {
                dismiss()
            }
        }
    }

    private fun onClick() {
        with(binding) {
            done.setOnAnimatedClickListener {
                if (validate()) {
                    apiAddAddressRequest()
                }
            }
        }
    }

    private fun validate(): Boolean {
        with(binding) {
            if (addressEt.text.toString() == "") {
                addressEt.error = getString(R.string.please_enter_address)
                return false
            } else if (zipCodeEt.text.toString() == "") {
                zipCodeEt.error = getString(R.string.please_enter_zip_code)
                return false
            }
        }
        return true
    }

    private fun spinnerCity() {
        SpinnerHelper.spinner(context = requireContext(), spinner = binding.citySpinner, statusList = cityList)
        binding.citySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                cityItem = parent?.getItemAtPosition(position).toString()
                sharedPrefManager.saveString("cityPosition", position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun stateSpinner() {
        binding.stateSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedItem = parent?.getItemAtPosition(position).toString()
                apiCityLitRequest(stateName = selectedItem)
                sharedPrefManager.saveString("statePosition", position.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    companion object {
        private var stateListResponse: StateListResponse? = null
        private var addAddressResponse: AddAddressResponse? = null
        private var cityListResponse: CityListResponse? = null
    }
}