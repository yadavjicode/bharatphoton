package com.app.bharatphoton.fragment.product

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.FeaturesListAdapter
import com.app.bharatphoton.adapter.HomeOffersViewPagerAdapter
import com.app.bharatphoton.adapter.ProductDetailsSpecificationAdapter
import com.app.bharatphoton.adapter.ReviewAdapter
import com.app.bharatphoton.adapter.ReviewImageAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedData.deviceId
import com.app.bharatphoton.common.Utils.downloadFile
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentProductDetailsBinding
import com.app.bharatphoton.fragment.profile.EnquireNowDialogFragment
import com.app.bharatphoton.module.productResponse.ProductDetailsResponse
import com.app.bharatphoton.module.productResponse.ProductDetailsResponse.Data.ProductDetails
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.CartViewModule
import com.app.bharatphoton.viewModule.ProductViewModule
import com.google.gson.Gson
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class ProductDetailsFragment : BaseFragment<FragmentProductDetailsBinding>() {
    private var imageScrollJob: Job? = null
    private var isLoaded = true
    private var pdfUrl = ""
    private var productId = ""
    private var type = ""
    private val cartViewModule by viewModels<CartViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val productDetailsSpecificationAdapter by lazy {
        ProductDetailsSpecificationAdapter()
    }

    private val featuresAdapter by lazy {
        FeaturesListAdapter()
    }

    private val reviewAdapter by lazy {
        ReviewAdapter()
    }

    private val reviewImageAdapter by lazy {
        ReviewImageAdapter { type, data ->

        }
    }

    private val viewModule by viewModels<ProductViewModule>()
    private val image = mutableListOf<String>()
    var count = 1

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.productDetails.postValue(null)
        cartViewModule.addToCart.postValue(null)
        cartViewModule.cartItemCount.postValue(null)
        imageScrollJob?.cancel()
        image.clear()
    }

    override fun onPause() {
        super.onPause()
        imageScrollJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        image.clear()
        imageChangeAutomatic()
        setDataInView()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        count()
        setDataInAdapter()
        apiProductDetailsRequest(productId, deviceId)
        apiAddToWishListResponse()
        apiProductDetailsResponse()
        imageChangeAutomatic()
        apiAddToCartResponse { addToCartResponse ->
            with(binding) {
                when (type) {
                    "PurchaseNow" -> {
                        purchaseNowBtn.hideLoading()
                        countLayout.visibility = View.VISIBLE
                        if (addToCartResponse.data?.products?.isNotEmpty() == true) {
                            itemCount.text = addToCartResponse.data.products[0]?.quantity.toString()
                        }
                        navigateToScreen(R.id.cartFragment, null, navOptions)
                    }

                    "count" -> {
                        progressBar.visibility = View.GONE
                        cartProgress.visibility = View.GONE
                        plus.visibility = View.VISIBLE
                        minus.visibility = View.VISIBLE
                        cartLayout.isEnabled = true
                        apiProductDetailsRequest(productId, deviceId)
                    }

                    else -> {
                        progressBar.visibility = View.GONE
                        cartProgress.visibility = View.GONE
                        plus.visibility = View.VISIBLE
                        minus.visibility = View.VISIBLE
                        cartLayout.isEnabled = true
                        apiProductDetailsRequest(productId, deviceId)
                    }
                }
            }
        }
        apiCartCountResponse { cartCount ->
            binding.topBar.cartItemCount.text = cartCount.toString()
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentProductDetailsBinding = FragmentProductDetailsBinding.inflate(layoutInflater)

    private fun apiProductDetailsRequest(id: String, deviceId: String) {
        viewModule.getProductDetails(id, deviceId = deviceId)
    }

    @SuppressLint("SetTextI18n")
    private fun apiProductDetailsResponse() {
        viewModule.productDetails.observe(viewLifecycleOwner) { it ->
            if (it is NetworkResult.Loading) showLoader() else hideLoader()
            if (it is NetworkResult.Error) {
                try {
                    val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                    requireActivity().toast(errorMessage)
                } catch (e: Exception) {
                    e.message.mlog()
                }
            }
            if (it is NetworkResult.Success) {
                try {
                    productDetailsResponse = Gson().fromJson(it.data, ProductDetailsResponse::class.java)
                } catch (e: Exception) {
                    e.message.mlog()
                }
                if (productDetailsResponse?.code == 1) {
                    with(binding) {
                        if (isLoaded) {
                            RecyclerAnimationHelper.animation(requireContext(), root)
                            isLoaded = false
                        }
                        parentFragment.visibility = View.VISIBLE


                        val data = productDetailsResponse?.data
                        youtube.visibility = if (data?.productDetails?.youtube?.isNotEmpty() == true) View.VISIBLE else View.GONE
                        productName.text = data?.productDetails?.productName ?: ""
                        categoryName.text = data?.productDetails?.categoryName ?: ""
                        productDescription.text = Html.fromHtml(data?.productDetails?.description ?: "", Html.FROM_HTML_MODE_LEGACY)



                        if (data?.productDetails?.quantity == 0) {
                            purchaseNowBtn.visibility = View.GONE
                            viewCart.visibility = View.GONE
                            outOfStockBtn.visibility = View.VISIBLE
                        } else {
                            outOfStockBtn.visibility = View.GONE
                        }

                        image.clear()

                        cartLayout.visibility = if (data?.productDetails?.cartQuantity == 0) View.VISIBLE else View.GONE
                        countLayout.visibility = if (data?.productDetails?.cartQuantity == 0) View.GONE else View.VISIBLE


                        if (data?.productDetails?.isWishlist == 1) {
                            like.setImageResource(R.drawable.like)
                        } else {
                            like.setImageResource(R.drawable.wishlist)
                        }

                        data?.productDetails?.image?.forEach { imageItem ->
                            imageItem?.url?.let {
                                image.add(it)
                            }
                        }
                        pdfUrl = data?.productDetails?.uploadBrocheure ?: ""
                        viewPager()

                        count = data?.productDetails?.cartQuantity ?: 0
                        itemCount.text = count.toString()

                        if (productDetailsResponse?.data?.productDetails?.uploadBrocheure?.isEmpty() == true) {
                            downloadBrochureLayout.visibility = View.GONE
                        } else {
                            downloadBrochureLayout.visibility = View.VISIBLE
                        }

                        productDetailsSpecificationAdapter.submitList(productDetailsResponse?.data?.productDetails?.specification ?: emptyList())

                        // ===================================================== YouTube Player View =========================================================================================
                        if (data?.productDetails?.youtube?.isNotEmpty() == true) {
                            val installationVideoUrl = data.productDetails.youtube[0]?.installationVideo ?: ""
                            val installationVideoId = extractVideoIdFromUrl(installationVideoUrl)

                            viewLifecycleOwner.lifecycleScope.launch {
                                setupYouTubePlayerView(installationVideoPlayerView, installationVideoId)
                            }
                        }

                        if (data?.productDetails?.youtube?.isNotEmpty() == true) {
                            val demoVideoUrl = data.productDetails.youtube[0]?.demoVideo ?: ""
                            val demoVideoId = extractVideoIdFromUrl(demoVideoUrl)/*   "demoVideoId -> $demoVideoId".mlog()
                            "demoVideoUrl -> $demoVideoUrl".mlog()
                            "Extracted YouTube ID: '${demoVideoId.trim()}' (length=${demoVideoId.trim().length})".mlog()*/

                            setupYouTubePlayerView(demoVideoPlayerView, demoVideoId)
                        }

                        if (data?.subscriptionPrice?.price == null) {
                            rent.visibility = View.GONE
                        } else {
                            rent.visibility = View.VISIBLE
                            val rentPrice = formatPrice(price = data.subscriptionPrice.price.toDouble(), currencyType = "INR")
                            rent.text = "Rent@ $rentPrice"
                        }

                        if (data?.reviews?.isEmpty() == true) {
                            reviewLayout.visibility = View.GONE
                        } else {
                            reviewLayout.visibility = View.VISIBLE
                            reviewAdapter.submitList(data?.reviews)
                        }

                        featuresAdapter.submitList(data?.productDetails?.features ?: emptyList())

                        val actualPrice = formatPrice(data?.productDetails?.price?.toDouble() ?: 0.0, "INR")
                        val discountedPrice = formatPrice(data?.productDetails?.priceToDisplay?.toDouble() ?: 0.0, "INR")
                        price.text = actualPrice
                        discountPrice.apply {
                            text = discountedPrice
                            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        }

                    }
                } else {
                    requireActivity().toast(productDetailsResponse?.message ?: "")
                }
            }
        }
    }


    private fun setupYouTubePlayerView(playerView: YouTubePlayerView, videoId: String?) {
        if (videoId.isNullOrEmpty()) return

//        playerView.release() // release any old webview player
        viewLifecycleOwner.lifecycle.addObserver(playerView)

        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                "🎥 Player ready for $videoId".mlog()
                youTubePlayer.cueVideo(videoId, 0f)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                "❌ YouTube Error: $error".mlog()
            }
        })
    }


    private fun count() {
        with(binding) {
            plus.setOnAnimatedClickListener {
                handleQuantityUpdate(1)
            }

            minus.setOnAnimatedClickListener {
                if (count >= 1) handleQuantityUpdate(-1)
            }
        }
    }

    private fun extractVideoIdFromUrl(url: String): String {
        return when {
            url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
            url.contains("youtube.com/watch?v=") -> url.substringAfter("v=").substringBefore("&")
            else -> url // fallback if it's already a video ID
        }
    }


    private fun handleQuantityUpdate(quantity: Int) {
        type = "count"
        productDetailsResponse?.data?.productDetails?.let { data ->
            if (quantity > 0 && !isStockAvailable(data)) {
                requireActivity().toast(getString(R.string.item_out_of_stock))
                return
            }

            toggleButtons(isLoading = true)
            apiAddToCartRequest(deviceId = deviceId, productId = productId, quantity = quantity)
        }
    }

    private fun imageChangeAutomatic() {
        imageScrollJob?.cancel()
        imageScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                if (image.isNotEmpty()) {
                    delay(3000)
                    val nextItem = (binding.viewPager.currentItem + 1) % image.size
                    binding.viewPager.setCurrentItem(nextItem, true)
                } else {
                    imageScrollJob?.cancel()
                    break
                }
            }
        }
    }

    private fun initData() = with(binding) {
        topBar.apply {
            cartItemCount.text = SharedData.cartItemCount.toString()
            notificationCountTv.text = SharedData.notificationCount.toString()
        }

        NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
            apiNotificationCountResponse { topBar.notificationCountTv.text = it.count.toString() }
        }

        productId = arguments?.getString("productId","")?:""

        topBar.title.isVisible = false
        specifications.apply {
            post { showIndicator(this) }
            setTextColor(resources.getColor(R.color.Deep_Saffron))
        }

        handleOnPressBackButton { navigateBack() }
    }

    private fun isStockAvailable(data: ProductDetails) = data.isQuantityUnlimited == 1 || (data.cartQuantity ?: 0) < (data.quantity ?: 0)

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }

            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions)
            }

            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }

            enquireNowBtn.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    if (productDetailsResponse?.data?.relatedProducts?.isNotEmpty() == true) {
                        putString("categoryName", productDetailsResponse?.data?.relatedProducts?.get(0)?.categoryName?:"")
                        putString("categoryId", productDetailsResponse?.data?.productDetails?.categoryId?:"")
                    }
                }

                val enquireNowDialogFragment = EnquireNowDialogFragment().apply {
                    arguments = bundle
                }

                enquireNowDialogFragment.show(childFragmentManager, "EnquireNowDialogFragment")
            }


            cartLayout.setOnAnimatedClickListener {
                type = "count"
                val productDetails = productDetailsResponse?.data?.productDetails
                cartLayout.isEnabled = false
                if (productDetails != null) {
                    if (productDetails.isQuantityUnlimited == 1) {
                        apiAddToCartRequest(deviceId = deviceId, productId = productId, quantity = 1)
                    } else if (productDetails.cartQuantity!! < (productDetails.quantity ?: 0)) {
                        cartProgress.visibility = View.VISIBLE
                        apiAddToCartRequest(deviceId = deviceId, productId = productId, quantity = 1)
                    } else {
                        requireActivity().toast(getString(R.string.item_is_out_of_stock))
                    }
                } else {
                    requireActivity().toast(getString(R.string.product_details_are_unavailable))
                }
            }

            share.setOnAnimatedClickListener {
                share()
            }
            viewCart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions)
            }
            downloadBrochureLayout.setOnAnimatedClickListener {
                downloadFile(context = requireContext(), url = pdfUrl, fileName = "brochure")
                requireActivity().toast("Downloading...")
            }

            purchaseNowBtn.setOnAnimatedClickListener {
                if (itemCount.text == "0") {
                    purchaseNowBtn.showLoading()
                    type = "PurchaseNow"
                    apiAddToCartRequest(deviceId = deviceId, productId = productId, quantity = 1)
                } else {
                    navigateToScreen(R.id.cartFragment, null, navOptions)
                }
            }

            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, null, navOptions)
            }

            like.setOnAnimatedClickListener {
                if (productDetailsResponse?.data?.productDetails?.isWishlist == 1) {
                    like.setImageResource(R.drawable.wishlist)
                    productDetailsResponse?.data?.productDetails?.isWishlist = 0
                } else {
                    like.setImageResource(R.drawable.like)
                    productDetailsResponse?.data?.productDetails?.isWishlist = 1
                }
                apiAddToWishListRequest(productId)
            }
            viewAll.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("productId", productId)
                }
                navigateToScreen(R.id.reviewFragment, bundle, navOptions = navOptions)
            }

            specifications.setOnAnimatedClickListener {
                resetTextColors()
                showIndicator(specifications)
                specificationsRv.visibility = View.VISIBLE

                youtubeLayout.visibility = View.GONE
                featuresLayout.visibility = View.GONE
                specifications.setTextColor(resources.getColor(R.color.Deep_Saffron))
            }
            features.setOnAnimatedClickListener {
                resetTextColors()
                showIndicator(features)
                specificationsRv.visibility = View.GONE
                youtubeLayout.visibility = View.GONE
                if (productDetailsResponse?.data?.reviews?.isEmpty() == true) {
                    reviewLayout.visibility = View.GONE
                } else {
                    reviewLayout.visibility = View.VISIBLE
                    reviewAdapter.submitList(productDetailsResponse?.data?.reviews)
                }
                featuresLayout.visibility = View.VISIBLE
                features.setTextColor(resources.getColor(R.color.Deep_Saffron))
            }

            youtube.setOnAnimatedClickListener {
                resetTextColors()
                showIndicator(youtube)
                specificationsRv.visibility = View.GONE
                reviewLayout.visibility = View.GONE
                featuresLayout.visibility = View.GONE
                youtubeLayout.visibility = View.VISIBLE
                youtube.setTextColor(resources.getColor(R.color.Deep_Saffron))
            }
            rent.setOnAnimatedClickListener {
                val pinCodeBottomSheetFragment = PinCodeBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString("productId", productId)
                    }
                }
                pinCodeBottomSheetFragment.show(childFragmentManager, "")
            }
        }
    }

    private fun resetTextColors() {
        with(binding) {
            specifications.setTextColor(resources.getColor(R.color.black))
            features.setTextColor(resources.getColor(R.color.black))
            youtube.setTextColor(resources.getColor(R.color.black))
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            reviewImageRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = reviewImageAdapter
            }

            specificationsRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = productDetailsSpecificationAdapter
            }

            reviewRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = reviewAdapter
            }

            featuresRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = featuresAdapter
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInView() {


    }


    private fun share() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.bharatphoton.com/product/${productDetailsResponse?.data?.productDetails?.productSlug}")
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun showIndicator(clickedView: View) {
        with(binding.indicatorView2) {
            visibility = View.VISIBLE
            val left = clickedView.left
            val right = clickedView.right
            translationX = left.toFloat()
            layoutParams.width = right - left
            requestLayout()
        }
    }

    private fun toggleButtons(isLoading: Boolean) = with(binding) {
        plus.visibility = if (isLoading) View.GONE else View.VISIBLE
        minus.visibility = if (isLoading) View.GONE else View.VISIBLE
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun viewPager() {
        with(binding) {
            val adapter = HomeOffersViewPagerAdapter { type ->
                when (type) {
                    "image" -> {
                        val imageList: MutableList<String> = image
                        val bundle = Bundle().apply {
                            putStringArrayList("productImages", ArrayList(imageList))
                        }
                        val dialog = ZoomPhotosDialogFragment().apply {
                            arguments = bundle
                        }
                        dialog.show(childFragmentManager, "ZoomPhotosDialogFragment")
                    }
                }
            }
            viewPager.adapter = adapter
            adapter.submitList(image)
            dotsIndicator.attachTo(viewPager)
        }
    }

    companion object {
        private var productDetailsResponse: ProductDetailsResponse? = null
    }

}