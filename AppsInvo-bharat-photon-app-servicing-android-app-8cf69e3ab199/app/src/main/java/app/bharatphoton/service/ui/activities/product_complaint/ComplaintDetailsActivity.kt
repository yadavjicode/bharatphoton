package app.bharatphoton.service.ui.activities.product_complaint

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponse
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponseComplaintDetails
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponseData
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivityComplaintDetailsBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.product_complaint.ComplaintInstallationPhotosAdapter
import app.bharatphoton.service.ui.adapter.product_complaint.ComplaintRemarksPhotosAdapter
import app.bharatphoton.service.ui.adapter.product_complaint.ComplaintSinListAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.CacheManager
import app.bharatphoton.service.utils.Constants.ALL_MEDIA_URLS
import app.bharatphoton.service.utils.Constants.CLICKED_MEDIA_URL
import app.bharatphoton.service.utils.Constants.COMPLAINT_DETAILS
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COMPLAINT_STATUS
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.IS_FIELD_AVAILABLE
import app.bharatphoton.service.utils.Constants.IS_SPECIFIC_CATEGORY_FOR_FLUSH
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.PRODUCT_DETAILS
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_DETAILS
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

class ComplaintDetailsActivity : BaseActivity(), ComplaintInstallationPhotosAdapter.OnImageClickListener, ComplaintRemarksPhotosAdapter.OnItemClickListener {
    private lateinit var binding: ActivityComplaintDetailsBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token: String? = null
    private var complaintId: String? = null
    private var fromAdapter: String? = null
    private var complaintStatus: String? = null
    private var addressAvailable: Boolean? = null
    private var positionGet: Int? = null
    private var urlGet: String? = null
    private var complaintUrlGet: String? = null
    private var complaintPositionGet: Int? = null
    private var photos: ArrayList<String> = arrayListOf()
    private var remarkPhotos: ArrayList<String> = arrayListOf()
    private lateinit var complaintDetails: ComplaintDetailsResponseData

    @OptIn(UnstableApi::class)
    override fun onDestroy() {
        super.onDestroy()
        urlGet = null
        positionGet = null
        CacheManager.releaseCache()
    }

    override fun onPostResume() {
        super.onPostResume()
        complaintDetailsApiCall()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(PRODUCT_COMPLAINT_DETAILS,"ComplaintDetailsActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")
        complaintId = intent.getStringExtra(COMPLAINT_ID)
        fromAdapter = intent.getStringExtra(FROM_ADAPTER)
        complaintStatus = intent.getStringExtra(COMPLAINT_STATUS)

        binding.ivBack.setOnClickWithAnimation {
            finish()
        }
        applySlideInAnimation(binding.clComplaintDetails)

    }

    private fun complaintDetailsApiCall() {
        binding.shimmerLayout.apply {
            startShimmer()
            binding.nsvDetails.isVisible = false
            visibility = View.VISIBLE
        }
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.complaintDetails(token.toString(), complaintStatus, complaintId)
        getDetail.enqueue(object : Callback<ComplaintDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ComplaintDetailsResponse>, response: Response<ComplaintDetailsResponse>) {
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
                                complaintDetails = responseData
                            }
                            val complaintDetailsResponse = responseData?.complaintDetails


                            //Product details

                            if (!complaintDetailsResponse?.productImage.isNullOrEmpty()) {
                                Glide.with(this@ComplaintDetailsActivity).load(complaintDetailsResponse?.productImage!![0]?.url).placeholder(R.drawable.default_gray_image_img).error(R.drawable.default_gray_image_img).into(binding.productImg)
                            }
                            binding.tvTicketNumber.text = ("#" + complaintDetailsResponse?.uuid)
                            binding.tvProductName.text = complaintDetailsResponse?.categoryName ?: "NA"
                            binding.tvProductDescription.text = complaintDetailsResponse?.productName ?: "NA"
                            binding.tvModelName.text = complaintDetailsResponse?.productModelNo ?: "NA"

                            binding.tvComplaintReason.text = complaintDetailsResponse?.complaintReason ?: "NA"
                            if (!complaintDetailsResponse?.description.isNullOrEmpty()) {
                                binding.tvComplaintDescription.isVisible = true
                                binding.tvComplaintDescription.text = complaintDetailsResponse?.description ?: ""
                            } else {
                                binding.tvComplaintDescription.isVisible = false
                            }
                            binding.tvResolveDate.text = complaintDetailsResponse?.resolveDate?.let { formatDateInStringFormat(it) } ?: "NA"

                            //FIN set and navigate to sin setup

                            val productListTemp: MutableList<ComplaintDetailsResponseComplaintDetails> = mutableListOf()
                            if (complaintDetailsResponse != null) {
                                productListTemp.add(complaintDetailsResponse)
                            }
                            binding.rvSin.layoutManager = LinearLayoutManager(this@ComplaintDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                            binding.rvSin.adapter = ComplaintSinListAdapter(this@ComplaintDetailsActivity, productListTemp) { sinNo ->
                                when (fromAdapter) {
                                    "ProductComplaintCompletedAdapter" -> {
                                        if (addressAvailable != null && addressAvailable != false) {
                                            val intent = Intent(this@ComplaintDetailsActivity, SINUpdateActivity::class.java)
                                            intent.putExtra(COMPLAINT_DETAILS, complaintDetails)
                                            intent.putExtra(IS_FIELD_AVAILABLE, complaintDetailsResponse?.isFieldAvailable)
                                            intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, complaintDetailsResponse?.isUrinalSensorAvailable)
                                            intent.putExtra(FROM_ADAPTER, "ProductComplaintPendingAdapter")
                                            startActivity(intent)
                                        } else {
                                            showAnimatedCustomToast("Address Not Available.")
                                        }
                                    }
                                    "ProductComplaintPendingAdapter" -> {
                                        if (addressAvailable != null && addressAvailable != false) {
                                            val intent = Intent(this@ComplaintDetailsActivity, SINUpdateActivity::class.java)
                                            intent.putExtra(COMPLAINT_DETAILS, complaintDetails)
                                            intent.putExtra(IS_FIELD_AVAILABLE, complaintDetailsResponse?.isFieldAvailable)
                                            intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, complaintDetailsResponse?.isUrinalSensorAvailable)
                                            intent.putExtra(FROM_ADAPTER, "ProductComplaintPendingAdapter")
                                            startActivity(intent)
                                        } else {
                                            showAnimatedCustomToast("Address Not Available.")
                                        }
                                    }
                                    "ProductComplaintNewAdapter" -> {
                                        if (addressAvailable != null && addressAvailable != false) {
                                            val intent = Intent(this@ComplaintDetailsActivity, SINUpdateActivity::class.java)
                                            intent.putExtra(COMPLAINT_DETAILS, complaintDetails)
                                            intent.putExtra(IS_FIELD_AVAILABLE, complaintDetailsResponse?.isFieldAvailable.toString())
                                            intent.putExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH, complaintDetailsResponse?.isUrinalSensorAvailable.toString())
                                            intent.putExtra(FROM_ADAPTER, "ProductComplaintNewAdapter")
                                            "${complaintDetailsResponse?.isFieldAvailable} ${complaintDetailsResponse?.isUrinalSensorAvailable} complaint details".mLog()
                                            startActivity(intent)
                                        } else {
                                            showAnimatedCustomToast("Address Not Available.")
                                        }
                                    }
                                    else -> {
                                        if (addressAvailable != null && addressAvailable != false) {
                                            val intent = Intent(this@ComplaintDetailsActivity, SINUpdateActivity::class.java)
                                            intent.putExtra(COMPLAINT_DETAILS, complaintDetails)
                                            intent.putExtra(FROM_ADAPTER, "ProductComplaintPendingAdapter")
                                            startActivity(intent)
                                        } else {
                                            showAnimatedCustomToast("Address Not Available.")
                                        }
                                    }
                                }
                            }

                            //User Details

                            val userDetails = responseData?.userDetails
                            if (!userDetails?.companyName.isNullOrEmpty()){
                                binding.tvUserName.text = userDetails?.companyName ?: "NA"
                            }else {
                                if (!userDetails?.firstName.isNullOrEmpty() && !userDetails?.lastName.isNullOrEmpty()) {
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() } + " " + userDetails?.lastName?.replaceFirstChar { it.uppercaseChar() }
                                } else if(!userDetails?.firstName.isNullOrEmpty() && userDetails?.lastName.isNullOrEmpty()){
                                    binding.tvUserName.text = userDetails?.firstName?.replaceFirstChar { it.uppercaseChar() }
                                }else {
                                    binding.tvUserName.text = "NA"
                                }
                            }
                            if (!userDetails?.businessPhoneNo.isNullOrEmpty()){
                                binding.tvUserPhone.text = userDetails?.country_code + " " + userDetails?.businessPhoneNo
                            }else {
                                if (userDetails?.phoneNo != null) {
                                    if (!userDetails?.countryCode.isNullOrEmpty()) {
                                        binding.tvUserPhone.text = userDetails.countryCode + " " + userDetails.phoneNo.toString()
                                    }else{
                                        binding.tvUserPhone.text = userDetails.country_code + " " + userDetails.phoneNo.toString()
                                    }
                                } else {
                                    binding.tvUserPhone.text = "NA"
                                }
                            }
                            if (!userDetails?.businessEmail.isNullOrEmpty()){
                                binding.tvUserEmail.text = userDetails?.businessEmail ?: "NA"
                            }else {
                                binding.tvUserEmail.text = userDetails?.email ?: "NA"
                            }
                            Glide.with(this@ComplaintDetailsActivity).load(userDetails?.profileImage).placeholder(R.drawable.default_gray_person_image).error(R.drawable.default_gray_person_image).into(binding.ivUserImg)

                            when (fromAdapter) {
                                "ProductComplaintCompletedAdapter" -> {
                                    binding.apply {
                                        binding.llCompletedData.isVisible = true
                                        tvRepresentativeName.text = complaintDetailsResponse?.customerName ?: "NA"
                                        tvRemark.text = complaintDetailsResponse?.remark ?: "NA"

                                        tvStatus.text = "Resolved"
                                        tvStatus.setTextColor(ContextCompat.getColor(this@ComplaintDetailsActivity, R.color.photon_dark_green))
                                        tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.resolved_highlight, 0, 0, 0)
                                    }
                                }

                                "ProductComplaintPendingAdapter" -> {
                                    binding.apply {
                                        binding.llCompletedData.isVisible = false

                                        tvStatus.text = "In Progress"
                                        tvStatus.setTextColor(ContextCompat.getColor(this@ComplaintDetailsActivity, R.color.photon_dark_blue))
                                        tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.in_progress_highlight, 0, 0, 0)
                                    }
                                }

                                "ProductComplaintNewAdapter" -> {
                                    binding.apply {
                                        binding.llCompletedData.isVisible = false

                                        tvStatus.text = "New"
                                        tvStatus.setTextColor(ContextCompat.getColor(this@ComplaintDetailsActivity, R.color.photon_orange))
                                        tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.new_highlight, 0, 0, 0)
                                    }
                                }
                            }

                            //Address details

                            if (complaintDetailsResponse?.address != null && complaintDetailsResponse.city != null && complaintDetailsResponse.state != null && complaintDetailsResponse.ZipCode != null) {
                                addressAvailable = true
                                binding.tvCity.isVisible = true
                                binding.tvState.isVisible = true
                                binding.tvPincode.isVisible = true
                                binding.tvAddress.text = complaintDetailsResponse.address ?: "NA"
                                binding.tvCity.text = complaintDetailsResponse.city ?: "NA"
                                binding.tvState.text = complaintDetailsResponse.state ?: "NA"
                                binding.tvPincode.text = complaintDetailsResponse.ZipCode ?: "NA"
                            } else if(!complaintDetailsResponse?.address.isNullOrEmpty()){
                                addressAvailable = true
                                binding.tvAddress.text = complaintDetailsResponse?.address ?: "NA"
                                binding.tvCity.isVisible = false
                                binding.tvState.isVisible = false
                                binding.tvPincode.isVisible = false
                            }else {
                                addressAvailable = false
                            }

                            //Complaint media

                            val images = complaintDetailsResponse?.image?.toMutableList()
                            if (!images.isNullOrEmpty()) {
                                photos.clear()
                                binding.rvComplaintMedia.adapter = null
                                binding.rvComplaintMedia.layoutManager = null
                                binding.rvComplaintMedia.removeAllViews()

                                binding.tvNoPhoto.isVisible = false
                                binding.rvComplaintMedia.layoutManager = LinearLayoutManager(this@ComplaintDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                                binding.rvComplaintMedia.adapter = ComplaintInstallationPhotosAdapter(this@ComplaintDetailsActivity, images, this@ComplaintDetailsActivity)

                                images.forEach { photo ->
                                    if (!photo?.url.isNullOrEmpty()) {
                                        photos.add(photo?.url!!)
                                    } else {
                                        binding.tvNoPhoto.isVisible = true
                                        binding.rvComplaintMedia.isVisible = false
                                    }
                                }
                            } else {
                                binding.tvNoPhoto.isVisible = true
                                binding.rvComplaintMedia.isVisible = false
                            }

                            //Remarks media

                            val remarksImages = complaintDetailsResponse?.remarkimage?.toMutableList()
                            if (!remarksImages.isNullOrEmpty()) {
                                remarkPhotos.clear()
                                binding.rvRemarksMedia.adapter = null
                                binding.tvNoRemarksPhoto.isVisible = false
                                binding.rvRemarksMedia.layoutManager = LinearLayoutManager(this@ComplaintDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
                                binding.rvRemarksMedia.adapter = ComplaintRemarksPhotosAdapter(this@ComplaintDetailsActivity, remarksImages, this@ComplaintDetailsActivity)

                                remarksImages.forEach { photo ->
                                    if (!photo?.url.isNullOrEmpty()) {
                                        remarkPhotos.add(photo?.url!!)
                                    } else {
                                        binding.tvNoRemarksPhoto.isVisible = true
                                        binding.rvRemarksMedia.isVisible = false
                                    }
                                }
                            } else {
                                binding.tvNoRemarksPhoto.isVisible = true
                                binding.rvRemarksMedia.isVisible = false
                            }
                        } catch (e: Exception) {
                            e.message.mLog()
                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@ComplaintDetailsActivity, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("complaintDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ComplaintDetailsResponse>, t: Throwable) {
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.nsvDetails.isVisible = true
                    visibility = View.GONE
                }
                try {
                    ApiErrorHandler.handleException(this@ComplaintDetailsActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("complaintDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }

    override fun onComplaintInstallationMediaClick(position: Int, complaintUrl: String) {
        complaintUrlGet = complaintUrl
        complaintPositionGet = position

        val bundle = Bundle().apply {
            putString(CLICKED_MEDIA_URL, complaintUrlGet)
            putStringArrayList(ALL_MEDIA_URLS, photos)
        }
        val dialogFragment = MediaViewerDialogFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(supportFragmentManager, "MediaViewerDialog")
    }

    override fun onComplaintRemarkPhotoClick(position: Int, url: String) {
        urlGet = url
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