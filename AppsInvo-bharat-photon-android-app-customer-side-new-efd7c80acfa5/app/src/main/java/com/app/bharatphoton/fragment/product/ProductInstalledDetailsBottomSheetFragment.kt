@file:Suppress("DEPRECATION")

package com.app.bharatphoton.fragment.product

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.MyProductSinListAdapter
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentProductInstalledDetailsBottomSheetBinding
import com.app.bharatphoton.module.productResponse.InstalledProductDetailsResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ProductViewModule
import com.github.ybq.android.spinkit.style.Circle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ProductInstalledDetailsBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var loader: ProgressDialog


    private val binding by lazy {
        FragmentProductInstalledDetailsBottomSheetBinding.inflate(layoutInflater)
    }

    private val myProductSinListAdapter by lazy {
        MyProductSinListAdapter { type, data ->
            when (type) {
                "root" -> {
                    if (data.installationStatus == 0) {
                        requireActivity().toast(getString(R.string.sin_details_not_available))
                    } else {
                        dismiss()
                        val bundle = Bundle().apply {
                            putString("orderId", data.orderId)
                            putString("productId", data.productId)
                            putString("id", data.id)
                        }
                        navigateToScreen(
                            id = R.id.sinDetailsFragment, bundle = bundle, navOptions = null
                        )
                    }
                }
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<ProductViewModule>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        initLoader()
        setDataInAdapter()
        apiProductDetailsResponse()
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

    private fun apiProductDetailsRequest(id: String, orderId: String) {
        viewModule.installedProductDetails(id = id, orderId = orderId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiProductDetailsResponse() {
        viewModule.installedProductDetails.observe(viewLifecycleOwner) {
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
                        binding.parentFragment.visibility = View.VISIBLE
                        installedProductDetailsResponse = Gson().fromJson(it.data, InstalledProductDetailsResponse::class.java)
                        if (installedProductDetailsResponse?.code == 1) {
                            val data = installedProductDetailsResponse?.data?.order
                            if (data?.installationInfo?.isNotEmpty() == true) {
                                binding.sinDataListRv.visibility = View.VISIBLE
                                myProductSinListAdapter.submitList(data.installationInfo)
                            } else {
                                binding.sinDataListRv.visibility = View.GONE
                            }
                            with(binding) {
                                productImg.load(data?.products?.image) {
                                    placeholder(R.drawable.my_produt_img)
                                }
                                productName.text = data?.products?.productName?:""
                                modelTxt.text = "Model Name : ${data?.products?.modelNo?:""}"
//                                downloadReport.visibility = if (data?.invoiceUrl != "") View.VISIBLE else View.GONE
                                val costValue = formatPrice(data?.products?.costSaved?.toDouble() ?: 0.0, "INR")
                                if (data?.products?.isFieldAvailable == 1 || data?.products?.isUrinalSensorAvilable == 1) {
                                    savedLayout.visibility = View.VISIBLE
                                } else {
                                    savedLayout.visibility = View.GONE
                                }
                                categoryName.text = data?.products?.categoryName?:""
                                quantityTv.text = "Qty : ${data?.products?.quantity}"
                                if (data?.products?.costSaved != null) {
                                    costSavedTv.text = "$costValue \nCost Saved"
                                } else {
                                    costSavedTv.text = "N/A"
                                }
                                if (data?.products?.waterSaved != null) {
                                    waterSavedTv.text = "${data.products.waterSaved} Ltrs \nWater Saved"
                                } else {
                                    waterSavedTv.text = "N/A"
                                }
                            }
                        } else {
                            requireActivity().toast(installedProductDetailsResponse?.message ?: "")
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
            val id = arguments?.getString("id") ?: ""
            val orderId = arguments?.getString("orderId") ?: ""
            apiProductDetailsRequest(id = id, orderId = orderId)

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

    private fun navigateToScreen(id: Int, bundle: Bundle? = null, navOptions: NavOptions? = null) {
        findNavController().navigate(id, bundle, navOptions)
    }

    private fun onClick() {
        with(binding) {
            downloadReport.setOnAnimatedClickListener {
                dismiss()
                val bundle = Bundle().apply {
                    putString("orderId", installedProductDetailsResponse?.data?.order?.id?:"")
                    putString("productId", installedProductDetailsResponse?.data?.order?.products?.productId?:"")
                    putString("screenType","Product")

                }
                navigateToScreen(R.id.monthlyGraphFragment, bundle, navOptions)
            }

            renewWarrantyBtn.setOnAnimatedClickListener {
                if (installedProductDetailsResponse?.data?.order?.products?.remainingWarrantyDays != null) {
                    if (installedProductDetailsResponse?.data?.order?.products?.remainingWarrantyDays!! > 1) {
                        requireActivity().toast("You can renew plan after ${installedProductDetailsResponse?.data?.order?.products?.remainingWarrantyDays} days")
                    } else {
                        dismiss()
                        val bundle = Bundle().apply {
                            putString("ProductId", installedProductDetailsResponse?.data?.order?.products?.productId)
                            putString("orderId", installedProductDetailsResponse?.data?.order?.id)
                            val sinList = installedProductDetailsResponse?.data?.order?.products?.installationDetails?.mapNotNull { it?.finNo }?.let { ArrayList(it) }
                            putStringArrayList("sinList", sinList)
                        }
                        navigateToScreen(
                            id = R.id.warrantyPlansFragment, bundle = bundle, navOptions = navOptions
                        )
                    }
                } else {
                    requireActivity().toast("Previous plan has not started yet")
                }
            }
            registerComplaint.setOnAnimatedClickListener {
                dismiss()
                val sinList = installedProductDetailsResponse?.data?.order?.installationInfo?.mapNotNull { it?.sin }

                val bundle = Bundle().apply {
                    putString("productId", installedProductDetailsResponse?.data?.order?.products?.productId)
                    putString("productName", installedProductDetailsResponse?.data?.order?.products?.productName)
                    putString("categoryName", installedProductDetailsResponse?.data?.order?.products?.categoryName)
                    putString("categoryId", installedProductDetailsResponse?.data?.order?.products?.categoryId)
                    sinList?.let {
                        putStringArrayList("sinList", ArrayList(it))
                    }
                    putString("orderId", installedProductDetailsResponse?.data?.order?.id)
                    putInt("complaintType", 1)
                }
                navigateToScreen(
                    id = R.id.complaintRegisterFragment, bundle = bundle, navOptions = navOptions
                )
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            sinDataListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myProductSinListAdapter
            }
        }
    }

    companion object {
        private var installedProductDetailsResponse: InstalledProductDetailsResponse? = null
    }
}