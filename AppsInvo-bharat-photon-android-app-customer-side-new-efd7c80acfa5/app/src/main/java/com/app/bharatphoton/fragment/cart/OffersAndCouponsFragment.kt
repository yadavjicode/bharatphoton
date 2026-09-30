@file:Suppress("DEPRECATION")

package com.app.bharatphoton.fragment.cart

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.CouponsListAdapter
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentOffersAndCouponsBinding
import com.app.bharatphoton.module.couponResponse.ApplyCouponResponse
import com.app.bharatphoton.module.couponResponse.CouponListResponse
import com.app.bharatphoton.module.couponResponse.CouponRemovedResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CouponViewModule
import com.github.ybq.android.spinkit.style.Circle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OffersAndCouponsFragment(
    private var onCouponApplied: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var loader: ProgressDialog
    private var cartId = ""

    private val binding by lazy {
        FragmentOffersAndCouponsBinding.inflate(layoutInflater)
    }

    private val couponsListAdapter by lazy {
        CouponsListAdapter { data, type ->
            when (type) {
                "apply" -> {
                    apiApplyCouponRequest(couponId = data.id ?: "", cartId = cartId)
                }

                "remove" -> {
                    apiRemoveCouponRequest(couponId = data.id ?: "", cartId = cartId)
                }
                else -> {
                    //
                }
            }
        }
    }

    private val viewModule by viewModels<CouponViewModule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        initLoader()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setDataInAdapter()
        onClick()
        apiCouponListRequest(cartId)
        apiCouponListResponse()
        apiApplyCouponResponse()
        apiRemoveCouponResponse()
    }

    private fun onClick() {
        with(binding) {
            apply.setOnAnimatedClickListener {
                dismiss()
            }
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    fun hideLoader() {
        if (loader.isShowing) {
            loader.dismiss()
        }
    }

    fun showLoader() {
        if (!loader.isShowing) {
            loader.show()
        }
    }

    private fun LifecycleOwner.handleOnPressBackButton(onBackPressed: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed.invoke() }

    }

    private fun apiApplyCouponRequest(couponId: String, cartId: String) {
        val request = JsonObject().apply {
            addProperty("couponId", couponId)
            addProperty("cartId", cartId)
        }
        viewModule.applyCoupon(request)
    }

    private fun apiApplyCouponResponse() {
        viewModule.applyCoupon.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        //
                    }
                }

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        applyCouponResponse = Gson().fromJson(it.data, ApplyCouponResponse::class.java)
                        if (applyCouponResponse?.code == 1) {
                            onCouponApplied.invoke()
                            dialog?.dismiss()
                        } else {
                            requireActivity().toast(applyCouponResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        //
                    }
                }
                else -> {
                    //
                }
            }
        }

    }

    private fun apiRemoveCouponRequest(cartId: String, couponId: String) {
        val request = JsonObject().apply {
            addProperty("couponId", couponId)
            addProperty("cartId", cartId)
        }
        viewModule.removeCoupon(request)
    }

    private fun apiRemoveCouponResponse() {
        viewModule.removeCoupon.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error<*> -> {
                    hideLoader()
                }

                is NetworkResult.Loading<*> -> showLoader()
                is NetworkResult.Success<*> -> {
                    hideLoader()
                    try {
                        couponRemovedResponse = Gson().fromJson(it.data, CouponRemovedResponse::class.java)
                        if (couponListResponse?.code == 1) {
                            dialog?.dismiss()
                            onCouponApplied.invoke()
                            requireActivity().toast(couponRemovedResponse?.message ?: "")
                        } else {
                            requireActivity().toast(couponRemovedResponse?.message ?: "")
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

    private fun apiCouponListRequest(cartId: String) {
        viewModule.couponList(cartId)
    }

    private fun apiCouponListResponse() {
        viewModule.couponList.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
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
                        couponListResponse = Gson().fromJson(it.data, CouponListResponse::class.java)
                        if (couponListResponse?.code == 1) {
                            RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.couponListRv)
                            couponsListAdapter.submitList(couponListResponse?.data)
                            couponsListAdapter.updateCoupons(couponListResponse?.data)
                            if (couponListResponse?.data?.isEmpty() == true) {
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

    private fun initData() {
        with(binding) {
            cartId = arguments?.getString("cartId") ?: ""
            handleOnPressBackButton {
                dismiss()
            }
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

    private fun setDataInAdapter() {
        with(binding) {
            couponListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = couponsListAdapter
            }
        }
    }

    companion object {
        val TAG: String = this::class.java.simpleName
        private var applyCouponResponse: ApplyCouponResponse? = null
        private var couponListResponse: CouponListResponse? = null
        private var couponRemovedResponse: CouponRemovedResponse? = null
    }
}