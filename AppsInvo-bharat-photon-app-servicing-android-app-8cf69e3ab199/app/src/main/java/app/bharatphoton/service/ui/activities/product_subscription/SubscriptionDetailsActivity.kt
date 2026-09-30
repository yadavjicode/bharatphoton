package app.bharatphoton.service.ui.activities.product_subscription

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.SubscriptionDetailsResponse
import app.bharatphoton.service.api_model_classes.SubscriptionDetailsResponseData
import app.bharatphoton.service.api_model_classes.SubscriptionDetailsResponseSubscriptionList
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivitySubscriptionDetailsBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.product_installation.InstallationPhotosAdapter
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionFinListAdapter
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionInstallationDetailsRemarksAdapter
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionRemarksPhotoAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.ALL_MEDIA_URLS
import app.bharatphoton.service.utils.Constants.CLICKED_MEDIA_URL
import app.bharatphoton.service.utils.Constants.FIN_NO
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.INSTALLATION_STATUS
import app.bharatphoton.service.utils.Constants.IS_FIELD_AVAILABLE
import app.bharatphoton.service.utils.Constants.IS_SPECIFIC_CATEGORY_FOR_FLUSH
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.SUBSCRIPTION_DETAILS
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_INSTALLATION_DETAILS
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_SUBSCRIPTION_DETAILS
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

class SubscriptionDetailsActivity : BaseActivity(), InstallationPhotosAdapter.OnItemClickListener {

    private lateinit var binding: ActivitySubscriptionDetailsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var subscriptionDetails: SubscriptionDetailsResponseData
    private var addressAvailable: Boolean? = null
    private var fromAdapter: String? = null
    private var installationStatus: String? = null
    private var photos: ArrayList<String> = arrayListOf()
    private var positionGet: Int? = null
    private var productId: String? = null
    private var remarkPhotos: ArrayList<String> = arrayListOf()
    private var token: String? = null
    private var urlGet: String? = null

    private val subscriptionInstallationDetailsAdapter by lazy {
        SubscriptionInstallationDetailsRemarksAdapter()
    }

    private val subscriptionRemarksPhotoAdapter by lazy {
        SubscriptionRemarksPhotoAdapter { type, data, position ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(PRODUCT_SUBSCRIPTION_DETAILS,"SubscriptionDetailsActivity")

        setDataInAdapter()

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")

        productId = intent.getStringExtra(PRODUCT_ID)
        fromAdapter = intent.getStringExtra(FROM_ADAPTER)
        installationStatus = intent.getStringExtra(INSTALLATION_STATUS)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
        applySlideInAnimation(binding.clSubscriptionDetails)
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
        subscriptionDetailsApiCall()
    }

    private fun setDataInAdapter() {
        with(binding) {
            remarkRv.apply {
                layoutManager = LinearLayoutManager(this@SubscriptionDetailsActivity)
                adapter = subscriptionInstallationDetailsAdapter
            }
            rvRemarksPhotos.apply {
                layoutManager = LinearLayoutManager(this@SubscriptionDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = subscriptionRemarksPhotoAdapter
            }
        }
    }

    private fun subscriptionDetailsApiCall() {
        binding.shimmerLayout.apply {
            startShimmer()
            binding.nsvDetails.isVisible = false
            visibility = View.VISIBLE
        }
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.subscriptionDetails(token.toString(), installationStatus, productId)
        getDetail.enqueue(object : Callback<SubscriptionDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<SubscriptionDetailsResponse>, response: Response<SubscriptionDetailsResponse>) {
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

                                subscriptionDetails = responseData
                                subscriptionInstallationDetailsAdapter.submitList(responseData.installationDetails)
                                subscriptionRemarksPhotoAdapter.submitList(responseData.installationDetails)

                                remarkPhotos.clear()
                                responseData.installationDetails?.forEach {
                                    it.image?.forEach {
                                        remarkPhotos.add(it.url ?: "")
                                    }
                                }

                                //Product details

                                val productList = responseData.subscriptionList
                                Glide.with(this@SubscriptionDetailsActivity)
                                    .load(productList?.productImage)
                                    .placeholder(R.drawable.default_gray_image_img)
                                    .error(R.drawable.default_gray_image_img)
                                    .into(binding.ivProductImg)
                                binding.tvOrderId.text = "#" + (productList?.uuid ?: "NA")
                                binding.tvProductName.text = productList?.categoryName ?: "NA"
                                binding.tvProductDescription.text = productList?.productName ?: "NA"
                                binding.tvModelName.text = productList?.modelNo ?: "NA"
                                binding.tvProductQuantity.text = productList?.quantity ?: "NA"
                                val price = if (productList?.price != null) {
                                    "\u20B9 ${productList.price} / Pcs."
                                } else {
                                    "NA"
                                }
                                binding.tvInstallationPrice.text = price

                                //User details

                                val userDetails = responseData.userDetails
                                if (!userDetails?.firstName.isNullOrEmpty() && !userDetails?.lastName.isNullOrEmpty()) {
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + userDetails?.lastName?.replaceFirstChar { it.uppercaseChar() }
                                } else if(!userDetails?.firstName.isNullOrEmpty() && userDetails?.lastName.isNullOrEmpty()){
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() }
                                } else {
                                    binding.tvUserName.text = "NA"
                                }
                                if (userDetails?.phoneNo != null) {
                                    binding.tvUserPhone.text = userDetails.country_code + " " + userDetails.phoneNo.toString()
                                } else {
                                    binding.tvUserPhone.text = "NA"
                                }
                                binding.tvUserEmail.text = userDetails?.email ?: "NA"
                                Glide.with(this@SubscriptionDetailsActivity).load(userDetails?.profileImage).placeholder(R.drawable.default_gray_person_image).error(R.drawable.default_gray_person_image).into(binding.ivUserImg)

                                //Installation details

                                when (fromAdapter) {
                                    "SubscriptionInstallationCompletedAdapter" -> {
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
                                    binding.tvPincode.text = (addressDetails?.ZipCode ?: "NA").toString()
                                }

                                //Installation images

                                val images = addressDetails?.image?.toMutableList()
                                if (!images.isNullOrEmpty()) {
                                    binding.tvNoPhoto.isVisible = false
                                    binding.rvInstallationPhotos.layoutManager = LinearLayoutManager(this@SubscriptionDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                                    binding.rvInstallationPhotos.adapter = InstallationPhotosAdapter(this@SubscriptionDetailsActivity, images, this@SubscriptionDetailsActivity)

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

                                //FIN set and navigate to sin setup

                                val productListTemp: MutableList<SubscriptionDetailsResponseSubscriptionList> = mutableListOf()
                                if (productList != null) {
                                    productListTemp.add(productList)
                                }
                                val finList = responseData.installationDetails?.toMutableList()
                                "finList -> $finList".mLog()
                                binding.rvFin.layoutManager = LinearLayoutManager(this@SubscriptionDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                                binding.rvFin.adapter = SubscriptionFinListAdapter(this@SubscriptionDetailsActivity, finList) { finNo, sinUpdateIdGet ->
                                    when (fromAdapter) {
                                        "SubscriptionInstallationCompletedAdapter" -> {
                                            val sinUpdateId = sinUpdateIdGet
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@SubscriptionDetailsActivity, SubscriptionSetupActivity::class.java)
                                                intent.putExtra(SUBSCRIPTION_DETAILS, subscriptionDetails)
                                                intent.putExtra(IS_FIELD_AVAILABLE, productList?.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, productList?.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "SubscriptionInstallationPendingAdapter")
                                                intent.putExtra(SIN_UPDATE_ID, sinUpdateId)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(PRODUCT_ID, productId)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        "SubscriptionInstallationPendingAdapter" -> {
                                            val sinUpdateId = sinUpdateIdGet
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@SubscriptionDetailsActivity, SubscriptionSetupActivity::class.java)
                                                intent.putExtra(SUBSCRIPTION_DETAILS, subscriptionDetails)
                                                intent.putExtra(IS_FIELD_AVAILABLE, productList?.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, productList?.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "SubscriptionInstallationPendingAdapter")
                                                intent.putExtra(SIN_UPDATE_ID, sinUpdateId)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(PRODUCT_ID, productId)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        "SubscriptionInstallationNewAdapter" -> {
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@SubscriptionDetailsActivity, SubscriptionSetupActivity::class.java)
                                                intent.putExtra(SUBSCRIPTION_DETAILS, subscriptionDetails)
                                                intent.putExtra(FIN_NO, finNo)
                                                intent.putExtra(IS_FIELD_AVAILABLE, productList?.isFieldAvailable)
                                                intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, productList?.isUrinalSensorAvilable)
                                                intent.putExtra(FROM_ADAPTER, "SubscriptionInstallationNewAdapter")
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                        else -> {
                                            if (addressAvailable != null && addressAvailable != false) {
                                                val intent = Intent(this@SubscriptionDetailsActivity, SubscriptionSetupActivity::class.java)
                                                intent.putExtra(SUBSCRIPTION_DETAILS, subscriptionDetails)
                                                startActivity(intent)
                                            } else {
                                                showAnimatedCustomToast("Address Not Available!")
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.message.mLog()
                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@SubscriptionDetailsActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("subscriptionDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<SubscriptionDetailsResponse>, t: Throwable) {
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.nsvDetails.isVisible = true
                    visibility = View.GONE
                }
                try {
                    ApiErrorHandler.handleException(this@SubscriptionDetailsActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("subscriptionDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }

}