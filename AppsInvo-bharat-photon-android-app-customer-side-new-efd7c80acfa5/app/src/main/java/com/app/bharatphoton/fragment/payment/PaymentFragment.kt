package com.app.bharatphoton.fragment.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.activity.MainActivity
import com.app.bharatphoton.activity.PaymentResult
import com.app.bharatphoton.adapter.MyAddressListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.startPayment
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentPaymentBinding
import com.app.bharatphoton.fragment.address.DeleteAddressBottomSheetFragment
import com.app.bharatphoton.module.addressResponse.AddressListResponse.AddressListData
import com.app.bharatphoton.module.orderResponse.CreateOrderResponse
import com.app.bharatphoton.module.orderResponse.VerifyPaymentResponse
import com.app.bharatphoton.module.purchaseSubscriptionResponse.PurchaseSubscriptionResponse
import com.app.bharatphoton.module.purchaseSubscriptionResponse.VerifySubscriptionPaymentResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.app.bharatphoton.viewModule.OrderViewModule
import com.app.bharatphoton.viewModule.PurchaseSubscriptionViewModule
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.razorpay.Checkout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>() {

    private var addressId = ""
    private var amount = ""
    private var cartId = ""
    private var paymentType = 1
    private var previousOrderId = ""
    private var razorPayOrderId = ""
    private var razorPayPaymentId = ""
    private var razorPaySignature = ""
    private var subscriptionAddress = ""
    private var subscriptionCartId = ""
    private var type = ""
    private val addressViewModule by viewModels<AddressViewModule>()

    private val myAddressListAdapter by lazy {
        MyAddressListAdapter(
            { data, view, type ->
                when (type) {
                    "more" -> {
                        view?.let { showMorePopUp(view = it, data) }
                    }

                    "root" -> {
//                    selectAddress(data)
                        subscriptionAddress = data.id ?: ""
                    }
                }
            },
            addressId = subscriptionAddress,
        )
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val purchaseSubscriptionViewModule by viewModels<PurchaseSubscriptionViewModule>()
    private val viewModule by viewModels<OrderViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.createOrder.postValue(null)
        purchaseSubscriptionViewModule.purchaseSubscription.postValue(null)
        addressViewModule.addressList.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiCreateOrderResponse()
        apiVerifyPaymentResponse()
        apiVerifySubscriptionPaymentResponse()
        apiAddressListResponse { addressListResponse ->
            if (addressListResponse.data?.isNotEmpty() == true) {
                RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.addressRv)
                addressId = addressListResponse.data[0]?.id ?: ""
                myAddressListAdapter.submitList(addressListResponse.data)
                initializeSelectedAddress(addressListResponse.data)
            } else {
                binding.addNewAddressTv.visibility = View.VISIBLE
            }
        }
        optionLayout(defaultSelectedTab)
        setDataInAdapter()
        apiPurchaseSubscriptionResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPaymentBinding = FragmentPaymentBinding.inflate(layoutInflater)

    private fun apiCreateOrderRequest() {
        val request = JsonObject().apply {
            addProperty("cartId", cartId)
            addProperty("addressId", addressId)
            addProperty("paymentMode", paymentType)
        }
        viewModule.createOrder(request)
    }

    private fun apiCreateOrderResponse() {
        viewModule.createOrder.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    binding.doneBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.doneBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.doneBtn.hideLoading()
                    try {
                        createOrderResponse = Gson().fromJson(it.data, CreateOrderResponse::class.java)
                        if (createOrderResponse?.code == 1) {
                            // For UPI
                            if (createOrderResponse?.data?.paymentMode == 1) {
                                val data = createOrderResponse?.data
                                val hasAvailableInstallation = data?.products?.any { it?.installationPrice != 0 } == true
                                data?.products?.forEach {
                                    it?.installationPrice.mlog()
                                }
                                SharedData.type = if (hasAvailableInstallation) getString(R.string.available) else getString(R.string.not_available)
                                createOrderResponse?.data?.razorpayOrderId.mlog()
                                val key = context?.getString(R.string.razorpaykey)
                                startPayment(createOrderResponse?.data?.razorpayOrderId ?: "", activity = requireActivity(), amount = amount)
                                getListenerFromActivity()
                            }
                        } else if (createOrderResponse?.code == 2) {
                            // For COD
                            val data = createOrderResponse?.data
                            val hasAvailableInstallation = data?.products?.any { it?.installationPrice != 0 } == true
                            SharedData.type = if (hasAvailableInstallation) getString(R.string.available) else getString(R.string.not_available)
                            navigateToConfirmOrderScreen()
                        } else {
                            requireActivity().toast(createOrderResponse?.message.toString())
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

    private fun apiPurchaseSubscriptionRequest(type: Int) {
        val request = JsonObject().apply {
            addProperty("type", type)
            addProperty("subscriptionCartId", subscriptionCartId)
            addProperty("addressId", subscriptionAddress)
            addProperty("paymentMode", paymentType)
            addProperty("previousOrderId", previousOrderId)
        }
        purchaseSubscriptionViewModule.purchaseSubscription(request)
    }

    private fun apiPurchaseSubscriptionResponse() {
        purchaseSubscriptionViewModule.purchaseSubscription.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    binding.doneBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.doneBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.doneBtn.hideLoading()
                    try {
                        purchaseSubscriptionResponse = Gson().fromJson(it.data, PurchaseSubscriptionResponse::class.java)
                        if (purchaseSubscriptionResponse?.code == 1) {
                            val data = purchaseSubscriptionResponse?.data
                            if (data?.paymentMode == 1) {
                                startPayment(data.razorpayOrderId ?: "", activity = requireActivity(), amount = data.amountInPaisa.toString())
                                getListenerFromActivity()
                            }
                        } else if (purchaseSubscriptionResponse?.code == 0) {
                            requireActivity().toast(purchaseSubscriptionResponse?.message ?: "")

                        } else {
                            navigateToConfirmOrderScreenFromSubscriptionList()
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
        viewModule.verifyPayment(request)
    }

    private fun apiVerifyPaymentResponse() {
        viewModule.verifyPayment.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                }

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        verifyPaymentResponse = Gson().fromJson(it.data, VerifyPaymentResponse::class.java)
                        if (verifyPaymentResponse?.code == 1) {
                            val data = createOrderResponse?.data
                            val bundle = Bundle().apply {
                                putString("orderNo", data?.uuid?:"")
                                putString("address", data?.address?:"")
                                putString("zipCode", data?.zipCode.toString())
                                putString("state", data?.state?:"")
                                putString("city", data?.city?:"")
                                putString("id", data?.id?:"")
                            }
                            MainActivity.paymentResultLiveData.postValue(null)
                            navigateToScreen(
                                R.id.confirmOrderFragment, bundle = bundle, navOptions = navOptions
                            )
                        } else {
                            requireActivity().toast(verifyPaymentResponse?.message ?: "")
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

    private fun apiVerifySubscriptionPaymentRequest(razorPaySignature: String, razorPayOrderId: String, razorPayPaymentId: String) {
        val request = JsonObject().apply {
            addProperty("razorpay_order_id", razorPayOrderId)
            addProperty("razorpay_payment_id", razorPayPaymentId)
            addProperty("razorpay_signature", razorPaySignature)
        }
        purchaseSubscriptionViewModule.verifySubscriptionPayment(request)
    }

    private fun apiVerifySubscriptionPaymentResponse() {
        purchaseSubscriptionViewModule.verifySubscriptionPayment.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                }

                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        verifySubscriptionPaymentResponse = Gson().fromJson(it.data, VerifySubscriptionPaymentResponse::class.java)
                        if (verifySubscriptionPaymentResponse?.code == 1) {
                            MainActivity.paymentResultLiveData.postValue(null)
                            navigateToConfirmOrderScreenFromSubscriptionList()
                        } else {
                            requireActivity().toast(verifySubscriptionPaymentResponse?.message ?: "")
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
                        when (type) {
                            "subscribeList" -> {
                                apiVerifySubscriptionPaymentRequest(razorPaySignature = razorPaySignature, razorPayOrderId = razorPayOrderId, razorPayPaymentId = razorPayPaymentId)
                            }

                            "renewPlan" -> {
                                apiVerifySubscriptionPaymentRequest(razorPaySignature = razorPaySignature, razorPayOrderId = razorPayOrderId, razorPayPaymentId = razorPayPaymentId)
                            }

                            else -> {
                                apiVerifyPaymentRequest()
                            }
                        }
                    }

                    is PaymentResult.Error -> {
//                    requireActivity().toast("Payment Failed")
                    }
                    else -> {
                        //
                    }
                }

            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            RecyclerAnimationHelper.animation(requireContext(), root)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.cart.visibility = View.GONE
            addressId = arguments?.getString("addressId","") ?: ""
            cartId = arguments?.getString("cartId","") ?: ""
            amount = arguments?.getString("amount","") ?: ""
            Checkout.preload(requireContext())
            type = arguments?.getString("type", "") ?: ""
            subscriptionCartId = arguments?.getString("subscriptionCartId", "") ?: ""
            previousOrderId = arguments?.getString("previousOrderId", "") ?: ""
            topBar.title.text = getString(R.string.payment)
            handleOnPressBackButton { navigateBack() }
            when (type) {
                "subscribeList" -> {
                    apiAddressListRequest("")
                    addressLayout.visibility = View.VISIBLE
                }

                "renewPlan" -> {
                    apiAddressListRequest("")
                    addressLayout.visibility = View.VISIBLE
                    cashLayout.visibility = View.GONE
                    cashTv.visibility = View.GONE
                }

                else -> {
                    addressLayout.visibility = View.GONE
                }
            }
        }
    }

    private fun initializeSelectedAddress(addressList: List<AddressListData?>?) {
        subscriptionAddress = addressList?.firstOrNull { it?.selectedAddress == "1" }?.id ?: ""
    }

    private fun navigateToConfirmOrderScreen() {
        val data = createOrderResponse?.data
        val bundle = Bundle().apply {
            putString("orderNo", data?.uuid?:"")
            putString("address", data?.address?:"")
            putString("zipCode", data?.zipCode.toString())
            putString("state", data?.state?:"")
            putString("city", data?.city?:"")
            putString("id", data?.id?:"")
        }
        navigateToScreen(
            R.id.confirmOrderFragment, bundle = bundle, navOptions = navOptions
        )
    }

    private fun navigateToConfirmOrderScreenFromSubscriptionList() {
        val data = purchaseSubscriptionResponse?.data
        val bundle = Bundle().apply {
            putString("address", data?.address?:"")
            putString("zipCode", data?.zipCode.toString())
            putString("state", data?.state?:"")
            putString("city", data?.city?:"")
            putString("id", data?.id?:"")
            putString("orderNo", data?.uuid?:"")
            putString("screenType", type?:"")
            putString("subscribeList", "subscribeList")
        }
        // Change here Screen for confirm renew subscription screen when client need with check screen type
        navigateToScreen(
            R.id.confirmOrderFragment, bundle = bundle, navOptions = navOptions
        )
    }

    private fun onClick() {
        with(binding) {
            addNewAddressTv.setOnAnimatedClickListener {
                navigateToScreen(R.id.addAddressFragment, bundle = null, navOptions = navOptions)
            }

            doneBtn.setOnAnimatedClickListener {
                if (type == "subscribeList") {
                    if (subscriptionAddress != "") {
                        apiPurchaseSubscriptionRequest(1)
                    } else {
                        requireActivity().toast("Please select an address")
                    }

                } else if (type == "renewPlan") {
                    if (subscriptionAddress != "") {
                        apiPurchaseSubscriptionRequest(2)
                    } else {
                        requireActivity().toast(getString(R.string.please_select_an_address))
                    }
                } else {
                    if (cartId != "" && addressId != "") {
                        apiCreateOrderRequest()
                    }
                }
            }

            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
        }
    }

    private fun optionLayout(selectedTab: Int) {
        with(binding) {
            for (i in 0 until optionLayout.childCount) {
                val child = optionLayout.getChildAt(i)

                if (child is CardView) {
                    val relativeLayout = child.getChildAt(0) as? RelativeLayout ?: continue
                    val radioButton = relativeLayout.findViewById<ImageView>(
                        if (i == 0) R.id.onlinePaymentRadio else R.id.cashRadio
                    ) ?: continue

                    radioButton.setImageResource(
                        if (i == selectedTab) R.drawable.radio_selected else R.drawable.radio_btn_unselect
                    )

                    relativeLayout.setOnAnimatedClickListener {
                        if (i == 0) {
                            paymentType = 1
                        } else {
                            paymentType = 2
                        }
                        optionLayout(i)
                    }
                }
            }
        }
    }

    private fun selectAddress(data: AddressListData) {
        val currentList = myAddressListAdapter.currentList.toMutableList()
        val updatedList = currentList.map {
            if (it.id == data.id) {
                it.copy(true)
            } else {
                it.copy(false)
            }
        }
        lifecycleScope.launch {
            myAddressListAdapter.submitList(updatedList)
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            addressRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myAddressListAdapter
            }
        }
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
                        putString("addressId", data.id?:"")
                        putString("firstName", data.firstName?:"")
                        putString("lastName", data.lastName?:"")
                        putString("address", data.address?:"")
                        putString("zipCode", data.zipCode.toString())
                        putString("state", data.state?:"")
                        putString("phoneNumber", data.phoneNo.toString())
                        putString("city", data.city?:"")
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
//                                apiDeleteAddressRequest(id = data.id ?: "")
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
        private var createOrderResponse: CreateOrderResponse? = null
        private var verifyPaymentResponse: VerifyPaymentResponse? = null
        private var purchaseSubscriptionResponse: PurchaseSubscriptionResponse? = null
        private var verifySubscriptionPaymentResponse: VerifySubscriptionPaymentResponse? = null
        private var defaultSelectedTab = 0
    }

}