package com.app.bharatphoton.fragment.profile

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.RenewListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.formatText
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.ViewModelHelperClass
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentRenewPlanBinding
import com.app.bharatphoton.module.purchaseSubscriptionResponse.RenewPlanResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartSubscriptionViewModule
import com.app.bharatphoton.viewModule.PurchaseSubscriptionViewModule
import com.bumptech.glide.Glide
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RenewPlanFragment : BaseFragment<FragmentRenewPlanBinding>() {

    private var id = ""
    private var productId = ""
    private var sinList = ""
    private val cartSubscriptionViewModule by viewModels<CartSubscriptionViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val renewListAdapter by lazy {
        RenewListAdapter { data, type ->
            when (type) {
                "upgradeBtn" -> {
                    val sinList2 = viewModelHelperClass.sinList2LiveData.value ?: mutableSetOf()
                    val count = sinList2.size.takeIf { it > 0 } ?: 1
                    val uniqueFinNumbers = finNumbers.distinct()
                    apiAddSubscriptionToCartRequest(
                        quantity = count, subscriptionId = data.id ?: "", productId = productId, finNumbers = uniqueFinNumbers
                    )
                }
            }
        }
    }

    private val viewModelHelperClass by viewModels<ViewModelHelperClass>()
    private val viewModule by viewModels<PurchaseSubscriptionViewModule>()
    private val finNumbers = mutableSetOf<String>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.renewPlan.postValue(null)
        sinList = ""
        viewModelHelperClass.sinList2LiveData.value?.clear()
        cartSubscriptionViewModule.addSubscriptionToCart.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataInAdapter()
        initData()
        onClick()
        apiRenewPlanResponse()
        apiAddSubscriptionToCartResponse { data ->
            val bundle = Bundle().apply {
                putString("id", data.data?.id)
                putString("type", "renewPlan")
                putString("previousOrderId", renewPlanResponse?.data?.currentPlan?.id?:"")
            }
            navigateToScreen(R.id.subscribeItemListFragment, bundle, navOptions = navOptions)
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentRenewPlanBinding = FragmentRenewPlanBinding.inflate(layoutInflater)

    private fun apiRenewPlanRequest(id: String, sin: String) {
        viewModule.renewPlan(id, sin = sin)
    }

    @SuppressLint("StringFormatInvalid", "SetTextI18n")
    private fun apiRenewPlanResponse() {
        viewModule.renewPlan.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
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
                        renewPlanResponse = Gson().fromJson(it.data, RenewPlanResponse::class.java)
                        if (renewPlanResponse?.code == 1) {
                            with(binding) {
                                val data = renewPlanResponse?.data
                                renewLayout.visibility = View.VISIBLE
                                Glide.with(requireContext()).load(data?.currentPlan?.productImage).placeholder(R.drawable.placeholder).into(productImg)
                                productName.text = data?.currentPlan?.productName
                                categoryName.text = data?.currentPlan?.categoryName
                                modelName.text = "Model Name : ${data?.currentPlan?.modelNo}"
                                planExpireTxt.visibility = if (data?.currentPlan?.daysRemaining == null) View.GONE else View.VISIBLE
                                productId = data?.currentPlan?.productId ?: ""
                                planValueTv.text = formatPrice(price = data?.currentPlan?.price ?: 0.0, currencyType = "INR")

                                data?.installationDetails?.forEach {
                                    it?.finNo?.let { finNo ->
                                        if (!finNumbers.contains(finNo)) {
                                            finNumbers.add(finNo)
                                        }
                                    } ?: ""
                                }

                                if (data?.avilablePlan?.isNotEmpty() == true) {
                                    binding.planListRv.post {
                                        renewListAdapter.submitList(data.avilablePlan?: emptyList())
                                        binding.planListRv.invalidate()
                                        binding.planListRv.requestLayout()
                                        planListRv.adapter?.notifyDataSetChanged()
                                    }
                                    noPlanAvailableTv.visibility = View.GONE
                                } else {
                                    noPlanAvailableTv.visibility = View.VISIBLE
                                }

                                if (data?.currentPlan?.daysRemaining == 0) {
                                    planExpireTxt.text = "Your plan will expire today!"
                                } else if (data?.currentPlan?.daysRemaining!! < 0) {
                                    planExpireTxt.text = "Your plan has expired"
                                    planExpireTxt.setTextColor(Color.RED)
                                } else {
                                    planExpireTxt.text = formatText(
                                        prefix = "Your plan is expire on ", plan = "${data.currentPlan?.daysRemaining?:0} Days", color = resources.getColor(R.color.Deep_Saffron)
                                    )
                                }



                                when (data.currentPlan?.mode) {
                                    "M" -> planDetailsTv.text = "Monthly"
                                    "Q" -> planDetailsTv.text = "Quarterly"
                                    "H" -> planDetailsTv.text = "Half Yearly"
                                    "Y" -> planDetailsTv.text = "Yearly"
                                }
                                when (data.currentPlan?.planType) {
                                    0 -> planTypeTv.text = "LITE"
                                    1 -> planTypeTv.text = "PLUS"
                                    2 -> planTypeTv.text = "PREMIUM"
                                }
                            }
                        } else {
                            requireActivity().toast(renewPlanResponse?.message ?: "")
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
            topBar.apply {
                title.text = getString(R.string.renew_plan)
                topBarFunctionality.visibility = View.VISIBLE
                notificationCountTv.text = SharedData.notificationCount.toString()
                cart.visibility = View.GONE
            }
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            handleOnPressBackButton { navigateBack() }
            id = arguments?.getString("subscriptionId", "") ?: ""
            sinList = arguments?.getString("sinList") ?: ""
            val currentList = viewModelHelperClass.sinList2LiveData.value ?: mutableSetOf()
            currentList.addAll(arguments?.getStringArrayList("sinList") ?: arrayListOf())
            viewModelHelperClass.sinList2LiveData.value = currentList
            val sinParam = currentList.joinToString(",")
            apiRenewPlanRequest(id = id, sin = if (currentList.isNotEmpty()) sinParam else sinList)
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
                call.setOnAnimatedClickListener { openDialer() }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            planListRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = renewListAdapter
                setHasFixedSize(false)
                isNestedScrollingEnabled = false

            }
        }
    }

    companion object {
        private var renewPlanResponse: RenewPlanResponse? = null
    }
}