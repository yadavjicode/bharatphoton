@file:Suppress("DEPRECATION")

package com.app.bharatphoton.baseUtils

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.app.bharatphoton.R
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.notificationCount
import com.app.bharatphoton.common.SharedPrefManager
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.module.addressResponse.AddressListResponse
import com.app.bharatphoton.module.cartResponse.AddToCartResponse
import com.app.bharatphoton.module.cartResponse.CartCountResponse
import com.app.bharatphoton.module.cartSubscriptionResponse.AddSubscriptionToCartApiResponse
import com.app.bharatphoton.module.complaintResponse.RegisterComplaintResponse
import com.app.bharatphoton.module.listingResponse.CityListResponse
import com.app.bharatphoton.module.listingResponse.ProductByCategoryIdListingResponse
import com.app.bharatphoton.module.listingResponse.StateListResponse
import com.app.bharatphoton.module.notificationRessponse.NotificationCountResponse
import com.app.bharatphoton.module.wishlistResponse.AddToWishLstResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.app.bharatphoton.viewModule.CartSubscriptionViewModule
import com.app.bharatphoton.viewModule.CartViewModule
import com.app.bharatphoton.viewModule.ComplaintViewModule
import com.app.bharatphoton.viewModule.ListingViewModule
import com.app.bharatphoton.viewModule.NotificationViewModule
import com.app.bharatphoton.viewModule.WishListViewModule
import com.facebook.internal.Validate.hasLocationPermission
import com.github.ybq.android.spinkit.style.Circle
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import javax.inject.Inject


abstract class BaseFragment<v : ViewBinding> : Fragment() {

    private lateinit var loader: ProgressDialog
    private val addressViewModule by viewModels<AddressViewModule>()
    private val cartSubscriptionViewModule by viewModels<CartSubscriptionViewModule>()
    private val cartViewModule by viewModels<CartViewModule>()
    private val complaintViewModule by viewModels<ComplaintViewModule>()
    private val listViewModule by viewModels<ListingViewModule>()
    private val notificationViewModule by viewModels<NotificationViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager

    lateinit var binding: v
    val wishListViewModule by viewModels<WishListViewModule>()

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                hasLocationPermission(context = requireContext())
            } else {
                // User clicks No
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLoader()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = initBinding(inflater, container)
        return binding.root
    }

    fun LifecycleOwner.handleOnPressBackButton(callBack: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            callBack.invoke()
        }
    }

    fun apiAddSubscriptionToCartRequest(quantity: Int, subscriptionId: String, productId: String, finNumbers: List<String>) {
        val request = JsonObject().apply {
            addProperty("subscriptionId", subscriptionId)
            addProperty("productId", productId)
            addProperty("quantity", quantity)
            add("finNo", JsonArray().apply {
                finNumbers.forEach { add(it) }
            })
        }
        cartSubscriptionViewModule.addSubscriptionToCart(request)
    }

    fun apiAddSubscriptionToCartResponse(onSuccess: (AddSubscriptionToCartApiResponse) -> Unit) {
        cartSubscriptionViewModule.addSubscriptionToCart.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
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
                        addSubscriptionToCartApiResponse = Gson().fromJson(it.data, AddSubscriptionToCartApiResponse::class.java)
                        if (addSubscriptionToCartApiResponse?.code == 1) {
                            onSuccess(addSubscriptionToCartApiResponse!!)
                        } else if (addSubscriptionToCartApiResponse?.message == "Invalid Token") {
                            navigateToScreen(R.id.loginFragment, null, navOptions = navOptions)
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                else -> {
                    Log.e("API", "Unhandled code: ${it.data}")
                }
            }
        }
    }

    fun apiAddToCartRequest(deviceId: String, productId: String, quantity: Int) {
        val request = JsonObject().apply {
            addProperty("deviceId", deviceId)
            addProperty("productId", productId)
            addProperty("quantity", quantity)

        }
        cartViewModule.addToCart(request)
    }

    fun apiAddToCartResponse(onSuccess: (AddToCartResponse) -> Unit) {
        cartViewModule.addToCart.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> hideLoader()
                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        addToCartResponse = Gson().fromJson(it.data, AddToCartResponse::class.java)
                        if (addToCartResponse?.code == 1) {
                            apiCartCountRequest(deviceId = SharedData.deviceId)
                            apiCartCountResponse {}
                            onSuccess(addToCartResponse!!)
                        } else {
                            requireActivity().toast(addToCartResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }
    }

    fun apiAddToWishListRequest(productId: String) {
        val request = JsonObject()
        request.addProperty("productId", productId)
        wishListViewModule.addToWishList(request)
    }

    fun apiAddToWishListResponse() {
        wishListViewModule.addToWishList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        addToWishLstResponse = Gson().fromJson(it.data, AddToWishLstResponse::class.java)

                    } catch (e: Exception) {
                        e.message.mlog()
                    }

                }

                is NetworkResult.Error -> {
                    hideLoader()
                }
            }
        }
    }

    fun apiAddressListRequest(id: String) {
        addressViewModule.addressList(id)
    }

    fun apiAddressListResponse(onSuccess: (AddressListResponse) -> Unit) {
        addressViewModule.addressList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            if (it is NetworkResult.Loading) showLoader() else hideLoader()
            if (it is NetworkResult.Error) {
                try {
                    val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                    requireActivity().toast(errorMessage)
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
            if (it is NetworkResult.Success) {
                try {
                    addressListResponse = Gson().fromJson(it.data, AddressListResponse::class.java)
                    if (addressListResponse?.code == 1) {
                        onSuccess(addressListResponse!!)
                    } else {
                        requireActivity().toast(it.errorMessage.toString())
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }

        }
    }

    fun apiCartCountRequest(deviceId: String) {
        cartViewModule.cartItemCount(deviceId)
    }

    fun apiCartCountResponse(cartItem: (Int) -> Unit) {
        cartViewModule.cartItemCount.observe(viewLifecycleOwner) {
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
                        cartCountResponse = Gson().fromJson(it.data, CartCountResponse::class.java)
                        if (cartCountResponse?.code == 1) {
                            SharedData.cartItemCount = cartCountResponse?.count ?: 0
                            sharedPrefManager.saveString("cartItemCount", cartCountResponse?.count.toString())
                            cartItem(cartCountResponse?.count ?: 0)
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }
    }

    fun apiCityListRequest(stateName: String) {
        listViewModule.cityListing(stateName)
    }

    fun apiCityListResponse(onSuccess: (CityListResponse) -> Unit) {
        listViewModule.cityList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
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
                        cityListResponse = Gson().fromJson(it.data, CityListResponse::class.java)
                        if (cityListResponse?.code == 1) {
                            onSuccess(cityListResponse!!)
                        }
                    } catch (e: Exception) {
                        //
                    }
                }
            }
        }

    }

    fun apiComplaintRegisterRequest(
        type: Int,
        orderId: String,
        purchasedSubscriptionId: String,
        categoryId: String,
        productId: String,
        sin: String,
        complaintReason: String,
        description: String,
        imageUrl: String,
        addressId: String
    ) {
        val request = JsonObject().apply {
            addProperty("type", type)
            addProperty("categoryId", categoryId)
            addProperty("orderId", orderId)
            addProperty("purchasedSubscriptionId", purchasedSubscriptionId)
            addProperty("productId", productId)
            addProperty("sin", sin)
            addProperty("complaintReason", complaintReason)
            addProperty("addressId", addressId)
            if (description != "") {
                addProperty("description", description)
            }
            add("image", JsonArray().apply {
                add(JsonObject().apply {
                    addProperty("url", imageUrl)
                })
            })
        }
        complaintViewModule.registerComplaint(request)
    }

    fun apiComplaintRegisterResponse(onSuccess: (RegisterComplaintResponse) -> Unit) {
        complaintViewModule.registerComplaint.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> {
                    try {
                        val errorMessage = JSONObject().optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {

                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        registerComplaintResponse = Gson().fromJson(it.data, RegisterComplaintResponse::class.java)
                        if (registerComplaintResponse?.code == 1) {
                            onSuccess(registerComplaintResponse!!)

                        } else {
                            requireActivity().toast(registerComplaintResponse?.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }
    }

    fun apiNotificationCountRequest() {
        notificationViewModule.notificationCount()
    }

    fun apiNotificationCountResponse(onComplete: (NotificationCountResponse) -> Unit) {
        notificationViewModule.notificationCount.observe(viewLifecycleOwner) {
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
                        notificationCountResponse = Gson().fromJson(it.data, NotificationCountResponse::class.java)
                        if (notificationCountResponse?.code == 1) {
                            onComplete(notificationCountResponse!!)
                            notificationCount = notificationCountResponse?.count ?: 0
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }

    }

    fun apiStateRequest() {
        listViewModule.stateListing()
    }

    fun apiStateResponse(onSuccess: (StateListResponse) -> Unit) {
        listViewModule.stateList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Error -> hideLoader()
                is NetworkResult.Loading -> {
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        stateListResponse = Gson().fromJson(it.data, StateListResponse::class.java)
                        if (stateListResponse?.code == 1) {
                            onSuccess(stateListResponse!!)

                        }
                    } catch (e: Exception) {
                        //
                    }
                }
            }
        }
    }

    fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }

    abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): v

    fun navigateBack() {
        findNavController().navigateUp()
    }

    fun navigateToScreen(id: Int, bundle: Bundle? = null, navOptions: NavOptions? = null) {
        val currentDestination = findNavController().currentDestination?.id
        if (currentDestination == id) {
            refreshCurrentFragment(id)
        } else {
            findNavController().navigate(id, bundle, navOptions)
        }
    }

    fun openDialer() {
        val dialerIntent = Intent(Intent.ACTION_DIAL)
        dialerIntent.data = Uri.parse("tel:18001216254")
        startActivity(dialerIntent)
    }

    fun refresh(refreshed: (onComplete: () -> Unit) -> Unit) {
        val refresh = view?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        refresh?.setOnRefreshListener {
            refresh.isRefreshing = true

            refreshed {
                refresh.isRefreshing = false
            }
        }
    }

    fun apiProductListingRequest(categoryId: String) {
        listViewModule.productList(categoryId = categoryId)
    }

    fun apiProductListingResponse(onSuccess: (ProductByCategoryIdListingResponse) -> Unit) {
        listViewModule.productList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            when (it) {
                is NetworkResult.Error<*> -> {
                    hideLoader()
                }

                is NetworkResult.Loading<*> -> {
                    showLoader()
                }

                is NetworkResult.Success<*> -> {
                    hideLoader()
                    try {
                        productByCategoryIdListingResponse = Gson().fromJson(it.data, ProductByCategoryIdListingResponse::class.java)
                        if (productByCategoryIdListingResponse?.code == 1) {
                            onSuccess(productByCategoryIdListingResponse!!)
                        } else {
                            requireActivity().toast(productByCategoryIdListingResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }
            }
        }
    }

    fun showLoader() {
        if (!loader.isShowing) {
            loader.show()
        }
    }

    private fun initLoader() {
        val loaderStyle = Circle().apply {
            color = resources.getColor(R.color.Deep_Saffron)
        }
        loader = ProgressDialog(requireContext(), R.style.TransparentProgressDialog).apply {
            setCancelable(true)
            setIndeterminateDrawable(loaderStyle)
        }
    }

    private fun refreshCurrentFragment(id: Int) {
        val navController = findNavController()
        navController.run {
            popBackStack()
            navigate(id)
        }
    }

    companion object {
        private var cityListResponse: CityListResponse? = null
        private var cartCountResponse: CartCountResponse? = null
        private var addToCartResponse: AddToCartResponse? = null
        private var stateListResponse: StateListResponse? = null
        private var addressListResponse: AddressListResponse? = null
        private var addToWishLstResponse: AddToWishLstResponse? = null
        private var notificationCountResponse: NotificationCountResponse? = null
        private var registerComplaintResponse: RegisterComplaintResponse? = null
        private var addSubscriptionToCartApiResponse: AddSubscriptionToCartApiResponse? = null
        private var productByCategoryIdListingResponse: ProductByCategoryIdListingResponse? = null
    }

}