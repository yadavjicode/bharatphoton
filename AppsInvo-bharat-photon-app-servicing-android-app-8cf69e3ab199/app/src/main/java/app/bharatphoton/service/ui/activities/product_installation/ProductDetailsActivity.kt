package app.bharatphoton.service.ui.activities.product_installation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ProductDetailResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityProductDetailsBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.product_installation.InstallationFinListAdapter
import app.bharatphoton.service.ui.adapter.product_installation.InstallationPhotosAdapter
import app.bharatphoton.service.ui.adapter.product_subscritption.ProductInstallationDetailsRemarksAdapter
import app.bharatphoton.service.ui.adapter.product_subscritption.ProductRemarksPhotoAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.ALL_MEDIA_URLS
import app.bharatphoton.service.utils.Constants.CLICKED_MEDIA_URL
import app.bharatphoton.service.utils.Constants.FIN_NO
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.INSTALLATION_STATUS
import app.bharatphoton.service.utils.Constants.IS_FIELD_AVAILABLE
import app.bharatphoton.service.utils.Constants.IS_SPECIFIC_CATEGORY_FOR_FLUSH
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.PRODUCT_DETAILS
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_INSTALLATION_DETAILS
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_SUBSCRIPTION_NEW_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.MediaViewerDialogFragment
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProductDetailsActivity : BaseActivity(), InstallationPhotosAdapter.OnItemClickListener {

    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var productDetails: ProductDetailResponse.ProductDetailResponseData
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var addressAvailable: Boolean? = null
    private var fromAdapter: String? = null
    private var installationStatus: String? = null
    private var photos: ArrayList<String> = arrayListOf()
    private var positionGet: Int? = null
    private var productId: String? = null
    private var remarkPhotos: ArrayList<String> = arrayListOf()
    private var token: String? = null
    private var urlGet: String? = null

    private val productRemarksPhotoAdapter by lazy {
        ProductRemarksPhotoAdapter { type, data, position ->
            when (type) {
                "root" -> {
                    if (data.image?.isNotEmpty() == true) {
                        urlGet = data.image[0].url
                        positionGet = position

                        val bundle = Bundle().apply {
                            putString(CLICKED_MEDIA_URL, urlGet)
                            putStringArrayList(ALL_MEDIA_URLS, remarkPhotos)
                        }
                        val dialogFragment = MediaViewerDialogFragment()
                        dialogFragment.arguments = bundle
                        dialogFragment.show(supportFragmentManager, "MediaViewerDialog")
                    }
                }
            }
        }
    }

    private val subscriptionInstallationDetailsAdapter by lazy {
        ProductInstallationDetailsRemarksAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(PRODUCT_INSTALLATION_DETAILS,"ProductDetailsActivity")

        setDataInAdapter()

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")
        productId = intent.getStringExtra(PRODUCT_ID)
        fromAdapter = intent.getStringExtra(FROM_ADAPTER)
        installationStatus = intent.getStringExtra(INSTALLATION_STATUS)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
        applySlideInAnimation(binding.clProductDetails)

    }

    override fun onInstallationMediaClick(position: Int, url: String) {
        urlGet = url
        positionGet = position

        val bundle = Bundle().apply {
            putString(CLICKED_MEDIA_URL, urlGet)
            putStringArrayList(ALL_MEDIA_URLS, photos)
        }
        val dialogFragment = MediaViewerDialogFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(supportFragmentManager, "MediaViewerDialog")
    }

    override fun onPostResume() {
        super.onPostResume()
        productDetailsApiCall()
    }

    private fun productDetailsApiCall() {
        binding.shimmerLayout.apply {
            startShimmer()
            binding.nsvDetails.isVisible = false
            visibility = View.VISIBLE
        }
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.productDetails(token.toString(), installationStatus, productId)
        getDetail.enqueue(object : Callback<ProductDetailResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ProductDetailResponse>, response: Response<ProductDetailResponse>)
            {
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.nsvDetails.isVisible = true
                    visibility = View.GONE
                }
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1) {
                        try {
                            val responseData = response.body()?.data

                            if (responseData != null) {

                                productDetails = responseData

                                //Product details
                                remarkPhotos.clear()
                                productRemarksPhotoAdapter.submitList(responseData.installationsData)
                                responseData.installationsData.forEach {
                                    it.image?.forEach { image ->
                                        remarkPhotos.add(image.url ?: "")
                                    }
                                }

                                val productList = responseData.product
                                if (!isDestroyed && !isFinishing) {
                                    Glide.with(this@ProductDetailsActivity)
                                        .load(productList?.image)
                                        .placeholder(R.drawable.default_gray_image_img)
                                        .error(R.drawable.default_gray_image_img)
                                        .into(binding.ivProductImg)
                                }
                                binding.tvOrderId.text = "#" + (responseData.uuid ?: "NA")
                                binding.tvProductName.text = responseData.categoryName ?: "NA"
                                binding.tvProductDescription.text = productList?.productName ?: "NA"
                                binding.tvModelName.text = productList?.modelNo ?: "NA"
                                binding.tvProductQuantity.text = productList?.quantity ?: "NA"
                                val price = if (productList?.installationPrice != null) {
                                    "\u20B9 ${productList.installationPrice} / Pcs."
                                } else {
                                    "NA"
                                }
                                binding.tvInstallationPrice.text = price

                                //FIN set and navigate to sin setup
                                subscriptionInstallationDetailsAdapter.submitList(responseData.installationsData)

                                val finList = responseData.installationsData.toMutableList()
                                "finList -> $finList".mLog()
                                binding.rvFin.layoutManager = LinearLayoutManager(
                                    this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false
                                )
                                binding.rvFin.adapter = InstallationFinListAdapter(
                                    this@ProductDetailsActivity, finList
                                ) { finNo, sinUpdateIdGet ->
                                    when (fromAdapter) {
                                        "ProductInstallationCompletedAdapter" -> {
                                            val sinUpdateId = sinUpdateIdGet
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@ProductDetailsActivity, SINSetUpActivity::class.java)
                                                intent.putExtra(PRODUCT_DETAILS, productDetails)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(IS_FIELD_AVAILABLE, responseData.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, responseData.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "ProductInstallationPendingAdapter")
                                                intent.putExtra(SIN_UPDATE_ID, sinUpdateId)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        "ProductInstallationPendingAdapter" -> {
                                            val sinUpdateId = sinUpdateIdGet
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@ProductDetailsActivity, SINSetUpActivity::class.java)
                                                intent.putExtra(PRODUCT_DETAILS, productDetails)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(IS_FIELD_AVAILABLE, responseData.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, responseData.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "ProductInstallationPendingAdapter")
                                                intent.putExtra(SIN_UPDATE_ID, sinUpdateId)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        "ProductInstallationNewAdapter" -> {
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@ProductDetailsActivity, SINSetUpActivity::class.java)
                                                intent.putExtra(PRODUCT_DETAILS, productDetails)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(IS_FIELD_AVAILABLE, responseData.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, responseData.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "ProductInstallationNewAdapter")
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        else -> {
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(
                                                    this@ProductDetailsActivity, SINSetUpActivity::class.java
                                                )
                                                intent.putExtra(PRODUCT_DETAILS, productDetails)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                    }
                                }

                                //User details

                                val userDetails = responseData.userDetails
                                if (!userDetails?.firstName.isNullOrEmpty() && !userDetails?.lastName.isNullOrEmpty()) {
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + userDetails?.lastName?.replaceFirstChar { it.uppercaseChar() }
                                } else if(!userDetails?.firstName.isNullOrEmpty() && userDetails?.lastName.isNullOrEmpty()){
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() }
                                }else {
                                    binding.tvUserName.text = "NA"
                                }
                                if (userDetails?.phoneNo != null) {
                                    binding.tvUserPhone.text = userDetails.countryCode + " " + userDetails.phoneNo.toString()
                                } else {
                                    binding.tvUserPhone.text = "NA"
                                }
                                binding.tvUserEmail.text = userDetails?.email ?: "NA"
                                Glide.with(this@ProductDetailsActivity).load(userDetails?.profileImage).placeholder(R.drawable.default_gray_person_image).error(R.drawable.default_gray_person_image).into(binding.ivUserImg)

                                //Installation details

                                when (fromAdapter) {
                                    "ProductInstallationCompletedAdapter" -> {
                                        binding.llCompletedData.isVisible = true
                                    }
                                    else -> {
                                        binding.llCompletedData.isVisible = false
                                    }
                                }

                                //Address details

                                val addressDetails = responseData.addressDetails
                                if (addressDetails?.address.isNullOrEmpty() || addressDetails?.address.isNullOrEmpty() || addressDetails?.address.isNullOrEmpty() || addressDetails?.address.isNullOrEmpty()) {
                                    addressAvailable = false
                                    binding.tvNoAddress.isVisible = true
                                    binding.cvAddress.isVisible = false
                                } else {
                                    addressAvailable = true
                                    binding.tvNoAddress.isVisible = false
                                    binding.cvAddress.isVisible = true
                                    binding.tvAddress1.text = addressDetails?.address ?: "NA"
                                    binding.tvCity.text = addressDetails?.city ?: "NA"
                                    binding.tvState.text = addressDetails?.state ?: "NA"
                                    binding.tvPincode.text = (addressDetails?.zipCode ?: "NA").toString()
                                }

                                //Installation images

                                val images = addressDetails?.image?.toMutableList()
                                if (!images.isNullOrEmpty()) {
                                    binding.tvNoPhoto.isVisible = false
                                    binding.rvInstallationPhotos.layoutManager = LinearLayoutManager(
                                        this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false
                                    )
                                    binding.rvInstallationPhotos.adapter = InstallationPhotosAdapter(
                                        this@ProductDetailsActivity, images, this@ProductDetailsActivity
                                    )

                                    photos.clear()
                                    images.forEach { photo ->
                                        if (!photo.url.isNullOrEmpty()) {
                                            photos.add(photo.url)
                                        } else {
                                            binding.tvNoPhoto.isVisible = true
                                            binding.rvInstallationPhotos.isVisible = false
                                        }
                                    }
                                } else {
                                    binding.tvNoPhoto.isVisible = true
                                    binding.rvInstallationPhotos.isVisible = false
                                }
                            }
                        } catch (e: Exception) {
                            e.message.mLog()
                            showAnimatedCustomToast(e.message.toString())
                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@ProductDetailsActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("productDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductDetailResponse>, t: Throwable) {
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.nsvDetails.isVisible = true
                    visibility = View.GONE
                }
                try {
                    ApiErrorHandler.handleException(this@ProductDetailsActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("productDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }

    private fun setDataInAdapter() {
        with(binding) {
            remarkRv.apply {
                layoutManager = LinearLayoutManager(this@ProductDetailsActivity)
                adapter = subscriptionInstallationDetailsAdapter
            }
            rvRemarksPhotos.apply {
                layoutManager = LinearLayoutManager(this@ProductDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = productRemarksPhotoAdapter
            }
        }
    }

}