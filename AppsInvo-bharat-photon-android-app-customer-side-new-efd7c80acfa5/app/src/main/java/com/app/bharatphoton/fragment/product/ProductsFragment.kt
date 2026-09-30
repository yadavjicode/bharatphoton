package com.app.bharatphoton.fragment.product

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ProductHorizontalAdapter
import com.app.bharatphoton.adapter.ProductVerticalAdapter
import com.app.bharatphoton.adapter.ProductVerticalForSearchAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.Utils.hideKeyboard
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.setupExpandableTextView
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentProductsBinding
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse.CategoryListData
import com.app.bharatphoton.module.productResponse.ProductListingForSearchResponse
import com.app.bharatphoton.module.productResponse.ProductListingResponse
import com.app.bharatphoton.module.productResponse.ProductListingResponse.ProductListData.SubcategoryListing
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ListingViewModule
import com.app.bharatphoton.viewModule.ProductViewModule
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ProductsFragment : BaseFragment<FragmentProductsBinding>() {

    private var categoryId = ""
    private var isAnimateFirst = true
    private var isFirstLoad = true
    private var isFirstTime = true
    private var isRestored = false
    private var productQuantity = 0
    private var progress: ProgressBar? = null
    private var searchTxt = ""
    private var selectedPosition = 0

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val productHorizontalAdapter by lazy {
        ProductHorizontalAdapter { data, type ->
            if (type == "category") {
                updateSelection(data.id ?: "")
                categoryId = data.id ?: ""
                binding.search.searchEditText.setText("")
                searchTxt = ""

                if (searchTxt.isBlank()) {
                    apiProductListRequest(categoryId, "", deviceId)
                    setDataInAdapter()
//                    binding.productVerticalRv.adapter = productVerticalAdapter
                } else {
                    apiProductListRequest("", searchTxt, deviceId)
                    setDataInAdapter()

//                    binding.productVerticalRv.adapter = productVerticalForSearchAdapter
                }
            }
        }
    }

    private val productListViewModule by viewModels<ProductViewModule>()

    private val productVerticalAdapter by lazy {
        "Without Search Adapter".mlog()
        ProductVerticalAdapter { type, data, imageView, progressBar, data2 ->
            when (type) {
                "products" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data?.id)
                    }
                    navigateToScreen(
                        id = R.id.productDetailsFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                "viewAll" -> {
                    val bundle = Bundle().apply {
                        putString("subcategoryId", data2?.products?.get(0)?.subcategoryId?:"")
                        putString("categoryId", categoryId)
                    }
                    navigateToScreen(R.id.viewAllSubcategoryProductListFragment, bundle, navOptions)
                }

                "cart" -> {
                    productQuantity = data?.quantity ?: 0
                    progressBar?.visibility = View.VISIBLE
                    progress = progressBar
                    data?.id?.let {
                        apiAddToCartRequest(deviceId, it, 1)
                    }
                    com.app.bharatphoton.common.CircleAnimationUtil().attachActivity(activity).setTargetView(imageView).setImage(data?.image?.get(0)?.url).setMoveDuration(1000)
                        .setDestView(binding.topBar.cartImg).startAnimation()
                }

                "like" -> {
                    data?.id?.let { apiAddToWishListRequest(it) }
                }
            }
        }
    }

    private val productVerticalForSearchAdapter by lazy {
        "For Search Adapter ".mlog()
        ProductVerticalForSearchAdapter { type, data, imageView, progressBar ->
            when (type) {
                "products" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data.id)
                    }
                    navigateToScreen(
                        id = R.id.productDetailsFragment, bundle = bundle, navOptions = navOptions
                    )
                }

                "cart" -> {
                    productQuantity = data.quantity ?: 0
                    progressBar?.visibility = View.VISIBLE
                    progress = progressBar
                    if (productQuantity == 0) {
                        requireActivity().toast(getString(R.string.out_of_stock))
                    } else {
                        data.id?.let {
                            apiAddToCartRequest(deviceId, it, 1)
                        }
                        com.app.bharatphoton.common.CircleAnimationUtil().attachActivity(activity).setTargetView(imageView).setImage(data.image?.get(0)?.url).setMoveDuration(1000)
                            .setDestView(binding.topBar.cartImg).startAnimation()
                    }
                }

                "like" -> {
                    data.id?.let { apiAddToWishListRequest(it) }
                }
            }
        }
    }

    private val viewModule by viewModels<ListingViewModule>()

    override fun onResume() {
        super.onResume()
        setDataInView()
//        searchTxt = ""
        if (searchTxt.isBlank()) {
            apiProductListRequest(categoryId, "", deviceId)
//            apiProductListResponse()
        } else {
            binding.search.searchEditText.setText(searchTxt)
            apiProductListRequest("", searchTxt, deviceId)
            apiProductListForSearchResponse()
            setDataInAdapter()
        }
        if (isRestored) {
            updateSelection(categoryId)
            scrollToSelectedPositionIfNeeded()
            isRestored = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        search()
        setClick()
        setDataInAdapter()
        apiAddToWishListResponse()
        apiProductListResponse()
        if (isFirstLoad) {
            if (searchTxt.isBlank()) {
                apiProductListRequest(categoryId, "", deviceId)
            } else {
                binding.search.searchEditText.setText(searchTxt)
                apiProductListRequest("", searchTxt, deviceId)
                apiProductListForSearchResponse()
            }
            isFirstLoad = false
        }
        apiCategoryLisRequest()
        apiCategoryLisResponse()
        apiAddToCartResponse { data ->
            progress?.visibility = View.GONE
            SharedData.cartItemCount += 1
            binding.topBar.cartItemCount.text = (SharedData.cartItemCount).toString()
            apiProductListRequest(categoryId, "", deviceId)
        }
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()

        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            selectedPosition = it.getInt("selectedPosition", 0)
            categoryId = it.getString("categoryId", "")
            isRestored = true
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentProductsBinding = FragmentProductsBinding.inflate(layoutInflater)

    private fun apiCategoryLisRequest() {
        viewModule.categoryListing()
    }

    private fun apiCategoryLisResponse() {
        viewModule.categoryList.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Loading) {
                //
            }
            if (it is NetworkResult.Success) {
                categoryListingResponse = Gson().fromJson(it.data, CategoryListingResponse::class.java)
                if (categoryListingResponse?.code == 1) {
                    setDataInView()
                } else {
                    requireActivity().toast(categoryListingResponse?.message.toString())
                }
            }

            if (it is NetworkResult.Error) {
                hideLoader()
            }
        }
    }

    private fun apiProductListForSearchResponse() {
        "With search response".mlog()
        productListViewModule.productList.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Loading) {
                showLoader()
            }
            if (it is NetworkResult.Error) {
                hideLoader()
                try {
                    val message = JSONObject(it.errorMessage ?: "").optString("message")
                    requireActivity().toast(message)
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
            if (it is NetworkResult.Success) {
                hideLoader()
                try {
                    productListingForSearchResponse = Gson().fromJson(it.data, ProductListingForSearchResponse::class.java)
                    if (productListingForSearchResponse?.code == 1) {
//                        binding.descriptionLayout.visibility = View.VISIBLE

                        if (productListingForSearchResponse?.data?.isEmpty() == true) {
                            binding.noFound.visibility = View.VISIBLE
                            binding.descriptionLayout.visibility = View.GONE
                        } else {
                            binding.noFound.visibility = View.GONE
                            binding.descriptionLayout.visibility = View.VISIBLE
                        }
                        if (productListingForSearchResponse?.data?.isNotEmpty() == true) {
                            productListingForSearchResponse?.data?.get(0)?.description?.let { it1 ->
                                setupExpandableTextView(
                                    binding.description, viewMore = binding.viewMore, it1, initialMaxLines = 3
                                )
                            }
                            productVerticalForSearchAdapter.submitList(productListingForSearchResponse?.data)
                            setDataInAdapter()
                        }

                    } else {
                        requireActivity().toast(productListingResponse?.message ?: "")
                    }
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
        }
    }

    private fun apiProductListRequest(categoryId: String, search: String, deviceId: String) {
        productListViewModule.productList(categoryId = categoryId, search = search, deviceId = deviceId)
    }

    private fun apiProductListResponse() {
        "without search Response".mlog()
        productListViewModule.productList.observe(viewLifecycleOwner) {
            if (it is NetworkResult.Loading) {
                showLoader()
            }
            if (it is NetworkResult.Error) {
                hideLoader()
                try {
                    val message = JSONObject(it.errorMessage ?: "").optString("message")
                    requireActivity().toast(message)
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
            if (it is NetworkResult.Success) {
                hideLoader()
                try {
                    productListingResponse = Gson().fromJson(it.data, ProductListingResponse::class.java)
                    if (productListingResponse?.code == 1) {
//                        binding.descriptionLayout.visibility = View.VISIBLE
                        if (isAnimateFirst) {
                            RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.productVerticalRv)
                            isAnimateFirst = false
                        }
                        if (productListingResponse?.data?.subcategoryListing?.isEmpty() == true) {
                            binding.noFound.visibility = View.VISIBLE
                            binding.descriptionLayout.visibility = View.GONE
                        } else {
                            binding.noFound.visibility = View.GONE
                            binding.descriptionLayout.visibility = View.VISIBLE
                        }

                        productListingResponse?.data?.let {
                            binding.categoryName.text = it.categoryName

                            val cleanedDescription = it.description?.replace("<br>", "")?.replace("<p>", "")
                            binding.description.text = Html.fromHtml(cleanedDescription, Html.FROM_HTML_MODE_COMPACT)

                            var isExpanded = false

                            binding.description.post {
                                val lineCount = binding.description.lineCount
                                if (lineCount > 2) {
                                    binding.description.maxLines = 3
                                    binding.description.ellipsize = TextUtils.TruncateAt.END
                                    binding.viewMore.visibility = View.VISIBLE

                                    binding.viewMore.apply {
                                        text = getString(R.string.view_more)
                                        setTextColor(Color.BLUE)
                                        setOnClickListener {
                                            isExpanded = !isExpanded
                                            if (isExpanded) {
                                                binding.description.maxLines = Int.MAX_VALUE
                                                binding.description.ellipsize = null
                                                text = getString(R.string.view_less)
                                            } else {
                                                binding.description.maxLines = 3
                                                binding.description.ellipsize = TextUtils.TruncateAt.END
                                                text = getString(R.string.view_more)
                                            }
                                        }
                                    }
                                } else {
                                    binding.viewMore.visibility = View.GONE
                                    binding.description.ellipsize = null
                                }

                            }

                            val tempList = mutableListOf<SubcategoryListing?>()
                            it.subcategoryListing?.forEach { productList ->
                                tempList.add(productList)
                            }
                            productVerticalAdapter.submitList(tempList)
                        }

                    } else {
                        requireActivity().toast(productListingResponse?.message ?: "")
                    }
                } catch (e: Exception) {
                    "without Search -> ${e.message}".mlog()
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
            topBar.topBarFunctionality.visibility = View.VISIBLE
            search.filter.visibility = View.GONE
            topBar.title.text = getString(R.string.products)
            if (isFirstTime) {
                categoryId = arguments?.getString("categoryId") ?: ""
                searchTxt = arguments?.getString("search", "") ?: ""
                isFirstTime = false
            }
            refresh { onComplete ->
                apiProductListRequest(categoryId, "", deviceId)
                onComplete()
            }

            productListingResponse?.data?.let {
                val cleanedDescription = it.description?.replace("<br>", "")?.replace("<p>", "")
                binding.description.text = Html.fromHtml(cleanedDescription, Html.FROM_HTML_MODE_COMPACT)

                var isExpanded = false

                binding.description.post {
                    val lineCount = binding.description.lineCount
                    if (lineCount > 2) {
                        binding.description.maxLines = 3
                        binding.description.ellipsize = TextUtils.TruncateAt.END
                        binding.viewMore.visibility = View.VISIBLE

                        binding.viewMore.apply {
                            text = getString(R.string.view_more)
                            setTextColor(Color.BLUE)
                            setOnClickListener {
                                isExpanded = !isExpanded
                                if (isExpanded) {
                                    binding.description.maxLines = Int.MAX_VALUE
                                    binding.description.ellipsize = null
                                    text = context.getString(R.string.view_less)
                                } else {
                                    binding.description.maxLines = 3
                                    binding.description.ellipsize = TextUtils.TruncateAt.END
                                    text = getString(R.string.view_more)
                                }
                            }
                        }
                    } else {
                        binding.viewMore.visibility = View.GONE
                        binding.description.ellipsize = null
                    }

                }
            }
        }
    }

    private fun scrollToSelectedPositionIfNeeded() {
        val layoutManager = binding.productHorizontalRv.layoutManager as LinearLayoutManager
        binding.productHorizontalRv.post {
            if (selectedPosition < layoutManager.findFirstVisibleItemPosition() || selectedPosition > layoutManager.findLastVisibleItemPosition()) {
                binding.productHorizontalRv.smoothScrollToPosition(selectedPosition)
            }
        }
    }

    private fun search() {
        with(binding) {
            search.searchEditText.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val searchQuery = v.text.toString()
                    searchTxt = searchQuery
                    apiProductListRequest("", searchQuery, deviceId)
                    apiProductListForSearchResponse()
                    hideKeyboard(context = requireContext(), view = requireView())
                    true
                } else {
                    true
                }
            }
        }
    }

    private fun setClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
                call.setOnAnimatedClickListener { openDialer() }
            }
            search.filter.setOnAnimatedClickListener { FilterBottomSheetFragment().show(childFragmentManager, "FilterBottomSheetFragment") }
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            productHorizontalRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = productHorizontalAdapter
            }

            productVerticalRv.apply {
                itemAnimator = null
                adapter = if (searchTxt.isBlank()) {
                    layoutManager = LinearLayoutManager(requireContext())
                    productVerticalAdapter
                } else {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    productVerticalForSearchAdapter
                }
            }
        }
    }

    private fun setDataInView() {
        categoryListingResponse?.data?.let {
            val tempList = mutableListOf<CategoryListData?>()
            tempList.addAll(it)
            productHorizontalAdapter.submitList(tempList)
            updateSelection(categoryId)
        }
    }

    private fun updateSelection(id: String) {
        val currentList = productHorizontalAdapter.currentList.toMutableList()
        val updatedList = currentList.mapIndexed { index, item ->
            if (item.id == id) {
                selectedPosition = index
                item.copy(isSelected = true)
            } else {
                item.copy(isSelected = false)
            }
        }
        productHorizontalAdapter.submitList(updatedList) {
            scrollToSelectedPositionIfNeeded()
        }
    }

    companion object {
        private var categoryListingResponse: CategoryListingResponse? = null
        private var productListingResponse: ProductListingResponse? = null
        private var productListingForSearchResponse: ProductListingForSearchResponse? = null
    }

}