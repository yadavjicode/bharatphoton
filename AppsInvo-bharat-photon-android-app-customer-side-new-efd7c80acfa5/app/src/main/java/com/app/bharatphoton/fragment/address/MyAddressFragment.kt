package com.app.bharatphoton.fragment.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.MyAddressListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentMyAddressBinding
import com.app.bharatphoton.module.addressResponse.AddressListResponse.AddressListData
import com.app.bharatphoton.module.addressResponse.DeleteAddressResponse
import com.app.bharatphoton.module.addressResponse.UpdateDeliveryAddressResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class MyAddressFragment : BaseFragment<FragmentMyAddressBinding>() {


    private val myAddressListAdapter by lazy {
        MyAddressListAdapter({ data, view, type ->
            when (type) {
                "more" -> {
                    if (view != null) {
                        showMorePopUp(view = view, data)
                    }
                }

                "root" -> {
//                    selectAddress(data)
                    apiUpdateDeliveryAddressRequest(id = data.id ?: "")
                }
            }
        })
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<AddressViewModule>()

    override fun onResume() {
        super.onResume()
        setDataInView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiAddressListRequest("")
        apiAddressListResponse { addressListResponse ->
            myAddressListAdapter.submitList(addressListResponse.data)
            RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.myAddressRv)
            if (addressListResponse.data?.isNotEmpty() == true) {
                binding.notFound.visibility = View.GONE
            } else {
                binding.notFound.visibility = View.VISIBLE
            }
        }
        apiDeleteAddressResponse()
        apiUpdateDeliveryAddressResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentMyAddressBinding = FragmentMyAddressBinding.inflate(layoutInflater)

    private fun apiDeleteAddressRequest(id: String) {
        viewModule.deleteAddress(id)
    }

    private fun apiDeleteAddressResponse() {
        viewModule.deleteAddress.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage!!).optString("Message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        //
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        deleteAddressResponse = Gson().fromJson(it.data, DeleteAddressResponse::class.java)
                        if (deleteAddressResponse?.code == 1) {
                            val currentList = myAddressListAdapter.currentList.toMutableList()
                            val updateList = currentList.filter { item ->
                                item.id != deleteAddressResponse?.data?.id
                            }
                            myAddressListAdapter.submitList(updateList)
                            if (updateList.isEmpty()) {
                                binding.notFound.visibility = View.VISIBLE
                            } else {
                                binding.notFound.visibility = View.GONE
                            }
                        } else {
                            requireActivity().toast(it.errorMessage.toString())
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

    private fun apiUpdateDeliveryAddressRequest(id: String) {
        val request = JsonObject().apply {
            addProperty("addressId", id)
        }
        viewModule.updateDeliveryAddress(request)
    }

    private fun apiUpdateDeliveryAddressResponse() {
        viewModule.updateDeliveryAddress.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage!!).optString("Message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        updateDeliveryAddressResponse = Gson().fromJson(it.data, UpdateDeliveryAddressResponse::class.java)
                        if (updateDeliveryAddressResponse?.code == 1) {
                            navigateBack()
                        } else {
                            requireActivity().toast(updateDeliveryAddressResponse?.message.toString())
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
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.title.text = getString(R.string.my_address)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.topBarFunctionality.visibility = View.VISIBLE

            refresh { onComplete ->
                apiAddressListRequest("")
                onComplete()
            }
            handleOnPressBackButton {
                navigateBack()
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }

            addNewAddress.setOnAnimatedClickListener {
                navigateToScreen(
                    id = R.id.addAddressFragment, bundle = null, navOptions = navOptions
                )
            }
        }
    }

    private fun selectAddress(data: AddressListData) {
        val currentList = myAddressListAdapter.currentList.toMutableList()

        val updatedList = currentList.map {
            if (it.id == data.id) {
                it.copy(isSelect = true)
            } else {
                it.copy(isSelect = false)
            }
        }
        lifecycleScope.launch {
            myAddressListAdapter.submitList(updatedList)
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            myAddressRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myAddressListAdapter
            }
        }
    }

    private fun setDataInView() {

    }

    private fun showMorePopUp(view: View, data: AddressListData) {
        val popUp = android.widget.PopupMenu(requireContext(), view)
        popUp.menuInflater.inflate(R.menu.more, popUp.menu)
        try {
            val fields = popUp.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popUp)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons = classPopupHelper.getMethod(
                        "setForceShowIcon", Boolean::class.javaPrimitiveType
                    )
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        popUp.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.edit -> {
                    val bundle = Bundle().apply {
                        putString("addressId", data.id)
                        putString("firstName", data.firstName)
                        putString("lastName", data.lastName)
                        putString("address", data.address)
                        putString("zipCode", data.zipCode.toString())
                        putString("state", data.state)
                        putString("phoneNumber", data.phoneNo.toString())
                        putString("city", data.city)
                        putString("type", "edit")
                    }
                    data.firstName.mlog()
                    navigateToScreen(
                        id = R.id.addAddressFragment, bundle = bundle, navOptions = navOptions
                    )
                    true
                }

                R.id.delete -> {
                    val deleteAddress = DeleteAddressBottomSheetFragment { type ->
                        when (type) {
                            "delete" -> {
                                apiDeleteAddressRequest(id = data.id ?: "")
                            }
                        }
                    }
                    deleteAddress.show(childFragmentManager, "deleteAddress")
                    true
                }

                else -> {
                    false
                }
            }
        }
        popUp.show()
    }

    companion object {
        private var deleteAddressResponse: DeleteAddressResponse? = null
        private var updateDeliveryAddressResponse: UpdateDeliveryAddressResponse? = null
    }

}