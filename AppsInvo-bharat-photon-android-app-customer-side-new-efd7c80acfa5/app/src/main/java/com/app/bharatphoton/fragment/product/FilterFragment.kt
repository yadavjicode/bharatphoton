package com.app.bharatphoton.fragment.product

import SpinnerDataModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentFilterBinding
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ListingViewModule
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dropDown

@AndroidEntryPoint
class FilterBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModule by viewModels<ListingViewModule>()

    private val binding by lazy { FragmentFilterBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        apiCategoryLisRequest()
        apiCategoryLisResponse()
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun apiCategoryLisRequest() {
        viewModule.categoryListing()
    }

    private fun apiCategoryLisResponse() {
        viewModule.categoryList.observe(viewLifecycleOwner) {
            if (it == null){
                return@observe
            }
            if (it is NetworkResult.Loading) {
//                showLoader()
            }
            if (it is NetworkResult.Success) {
                categoryListingResponse = Gson().fromJson(it.data, CategoryListingResponse::class.java)
                if (categoryListingResponse?.code == 1) {
                    binding.categoryTv.dropDown(categoryListingResponse?.data?.map {
                        SpinnerDataModel(it?.id, it?.categoryName)
                    } ?: emptyList(), -1) {
                        binding.categoryTv.text = it.title.toString()
                    }
                } else {
                    requireActivity().toast(categoryListingResponse?.message.toString())
                }
            }

            if (it is NetworkResult.Error) {
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    companion object {
        private var categoryListingResponse: CategoryListingResponse? = null
    }

    private fun initData() {
        with(binding) {

        }
    }

}