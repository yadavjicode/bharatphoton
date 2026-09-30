package com.app.bharatphoton.fragment.profile

import SpinnerDataModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.ComplaintHistoryAdapter
import com.app.bharatphoton.adapter.CustomerSupportOptionsAdapter
import com.app.bharatphoton.adapter.FAQListAdapter
import com.app.bharatphoton.adapter.InstallationVideoAdapter
import com.app.bharatphoton.adapter.LoaderAdapter
import com.app.bharatphoton.adapter.TroubleShootingVideoAdapter
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentCustomerSupportBinding
import com.app.bharatphoton.module.Module
import com.app.bharatphoton.module.cmsResponse.FAQResponse
import com.app.bharatphoton.module.cmsResponse.TroubleShootVideos
import com.app.bharatphoton.module.listingResponse.CategoryListingResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.app.bharatphoton.viewModule.AuthViewModule
import com.app.bharatphoton.viewModule.ComplaintViewModule
import com.app.bharatphoton.viewModule.ListingViewModule
import com.app.bharatphoton.viewModule.ProductViewModule
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import dropDown
import org.json.JSONObject
import java.io.File

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CustomerSupportFragment : BaseFragment<FragmentCustomerSupportBinding>() {

    private var addressId = ""

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data")
            val image = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
            if (image != null) {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = image, onSuccess = { url ->
                    binding.progressBar.visibility = View.GONE
                    binding.complaintImage.visibility = View.VISIBLE
                    imageUrl = url
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.complaintImage)
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            }
        }
    }

    private var categoryId = ""
    private var complaintReason = ""

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val mimeType = requireActivity().contentResolver.getType(uri)
            val file: File? = when {
                mimeType?.startsWith("image/") == true -> {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    imageBitmapToFile(context = requireContext(), bitmap = bitmap)
                }

                mimeType?.startsWith("video/") == true -> {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    inputStream?.let { input ->
                        val uniqueFileName = "temp_video_${System.currentTimeMillis()}.mp4"
                        val videoFile = File(requireContext().cacheDir, uniqueFileName)

                        videoFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                        videoFile
                    }
                }

                else -> null
            }

            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    imageUrl = url
                    binding.progressBar.visibility = View.GONE
                    binding.complaintImage.visibility = View.VISIBLE
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.complaintImage)
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            } else {
                hideLoader()
                Toast.makeText(requireContext(), "Unsupported media type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private var imageUrl = ""
    private var productId = ""
    private var typeIndex = 0
    private val authViewModule by viewModels<AuthViewModule>()

    private val complaintHistoryAdapter by lazy {
        ComplaintHistoryAdapter { data, type ->
            when (type) {
                "root" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data.id)
                    }
                    navigateToScreen(R.id.complaintHistoryDetailsFragment, bundle, navOptions = navOptions)
                }
            }
        }
    }

    private val customerSupportOptionsAdapter by lazy {
        CustomerSupportOptionsAdapter { type ->
            typeIndex = type
            if (type == 0) {
                binding.complaintRegisterLayout.visibility = View.VISIBLE
                binding.scrollLayout.visibility = View.VISIBLE
            } else {
                binding.complaintRegisterLayout.visibility = View.GONE
            }
            if (type == 1) {
                RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.faqListRv)
                binding.customerCareLayout.visibility = View.VISIBLE
                binding.scrollLayout.visibility = View.VISIBLE
                apiFaqListRequest()
            } else {
                binding.customerCareLayout.visibility = View.GONE
            }
            if (type == 2) {
                RecyclerAnimationHelper.applyRecyclerAnimation(requireContext(), binding.ComplaintHistoryRv)
                binding.ComplaintHistoryLayout.visibility = View.VISIBLE
                binding.scrollLayout.visibility = View.GONE
                apiComplaintHistoryRequest()
            } else binding.ComplaintHistoryLayout.visibility = View.GONE
            if (type == 3) {
                binding.troubleShootingVideoLayout.visibility = View.VISIBLE
                binding.scrollLayout.visibility = View.VISIBLE

            } else {
                binding.troubleShootingVideoLayout.visibility = View.GONE
            }
        }
    }

    private val faqListAdapter by lazy {
        FAQListAdapter { type ->
            when (type) {
                "Expand" -> {

                }
            }
        }
    }

    private val installationVideoAdapter by lazy { InstallationVideoAdapter() }
    private val listingModule by viewModels<ListingViewModule>()
    private val addressListViewModule by viewModels<AddressViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private val productViewModule by viewModels<ProductViewModule>()
    private val troubleShootingVideoAdapter by lazy { TroubleShootingVideoAdapter() }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private val viewModule by viewModels<ComplaintViewModule>()
    private val complaintHistoryList = mutableListOf<Module>()
    private val optionList = mutableListOf<Module>()

    override fun onDestroyView() {
        super.onDestroyView()
        optionList.clear()
        complaintHistoryList.clear()
        authViewModule.troubleShootVideos.postValue(null)
        listingModule.categoryList.postValue(null)
        listingModule.subCategoryList.postValue(null)
        productViewModule.productList.postValue(null)
        authViewModule.faqList.postValue(null)
        addressListViewModule.addressList.postValue(null)
        type = ""
        viewModule.complaintListHistory(null)
        viewModule.registerComplaint.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        selectReason()
        apiCategoryLisRequest()
        apiCategoryLisResponse()
        apiProductListResponse()
        apiFaqListResponse()
        sinTextFormat()
        apiComplaintRegisterResponse { registerComplaintResponse ->
            binding.submitBtn.hideLoading()
            val data = registerComplaintResponse.data
            val bundle = Bundle().apply {
                putString("complaintNo", data?.uuid)
                putString("complaintDate", data?.createdAt)
            }
            navigateToScreen(id = R.id.complaintRegisteredFragment, bundle = bundle, navOptions = navOptions)
        }
        optionLayoutForTroubleShootVideo(null)
        apiTroubleShootVideoResponse()
        apiAddressListRequest("")
        apiAddressListResponse { addressListResponse ->
            if (addressListResponse.data?.isNotEmpty() == true) {
                binding.addAddressTv.visibility = View.GONE
            } else {
                binding.addAddressTv.visibility = View.VISIBLE
            }
            addressListResponse.data?.let {
                binding.addressTv.dropDown(list = it.map {
                    SpinnerDataModel(id = it?.id, title = it?.address + ", " + it?.city + ", " + it?.state + ", " + it?.zipCode)
                }, isShowIndexValue = -1, onClickItem = {
                    binding.addressTv.text = it.title.toString()
                    addressId = it.id.toString()
                })
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentCustomerSupportBinding = FragmentCustomerSupportBinding.inflate(layoutInflater)

    private fun apiCategoryLisRequest() {
        listingModule.categoryListing()
    }

    private fun sinTextFormat() {
        with(binding) {
            sinTv.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false
                private var deletingHyphen = false

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    // Detect hyphen deletion
                    if (count > 0 && after == 0 && s?.get(start) == '-') {
                        deletingHyphen = true
                    }
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    if (isFormatting || editable == null) return
                    isFormatting = true

                    val original = editable.toString()
                    val cleanString = original.replace("-", "").uppercase()

                    val trimmedString = cleanString.take(6)

                    val formattedString = StringBuilder()
                    for (i in trimmedString.indices) {
                        if (i > 0 && i % 2 == 0) {
                            formattedString.append("-")
                        }
                        formattedString.append(trimmedString[i])
                    }

                    val newText = formattedString.toString()

                    if (newText != original) {
                        sinTv.removeTextChangedListener(this)
                        sinTv.setText(newText)
                        sinTv.setSelection(newText.length)
                        sinTv.addTextChangedListener(this)
                    }

                    isFormatting = false
                    deletingHyphen = false
                }
            })
        }
    }


    private fun apiCategoryLisResponse() {

        listingModule.categoryList.observe(viewLifecycleOwner) { it ->
            if (it == null){
                return@observe
            }
            if (it is NetworkResult.Loading) {
                //
            }
            if (it is NetworkResult.Success) {
                categoryListingResponse = Gson().fromJson(it.data, CategoryListingResponse::class.java)
                if (categoryListingResponse?.code == 1) {
                    binding.categoryTv.dropDown(categoryListingResponse?.data?.map {
                        SpinnerDataModel(it?.id, it?.categoryName)
                    } ?: emptyList(), -1) { it ->
                        binding.categoryTv.text = it.title.toString()
                        categoryId = it.id.toString()
                        apiProductListRequest(it.id.toString())
                    }

                    // For TroubleShooting Video
                    binding.categoryList.dropDown(categoryListingResponse?.data?.map {
                        SpinnerDataModel(it?.id, it?.categoryName)
                    } ?: emptyList(), -1) {
                        binding.categoryList.text = it.title.toString()
                        categoryId = it.id.toString()
                        apiProductListRequest(it.id.toString())
                    }
                } else {
                    requireActivity().toast(categoryListingResponse?.message.toString())
                }
            }

            if (it is NetworkResult.Error) {
                //
            }
        }
    }

    private fun apiComplaintHistoryRequest() {
        viewModule.complaintListHistory("")
        viewModule.complaintListHistory.observe(viewLifecycleOwner) { pagingData ->
            pagingData?.observe(viewLifecycleOwner) { data ->
                complaintHistoryAdapter.submitData(lifecycle, data)
            } ?: run {
                complaintHistoryAdapter.submitData(lifecycle, PagingData.empty())
            }
        }

    }

    private fun apiFaqListRequest() {
        authViewModule.faqList()
    }

    private fun apiFaqListResponse() {
        authViewModule.faqList.observe(viewLifecycleOwner) {
            if (it == null) {
                hideLoader()
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
                        FAQListResponse = Gson().fromJson(it.data, FAQResponse::class.java)
                        if (FAQListResponse?.code == 1) {
                            faqListAdapter.submitList(FAQListResponse?.data)
                        } else {
                            requireActivity().toast(FAQListResponse?.message ?: "")
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

    private fun apiProductListRequest(categoryId: String) {
        listingModule.productList(categoryId = categoryId)

    }

    private fun apiProductListResponse() {
        apiProductListingResponse { data ->
            productId = data.data?.get(0)?.id ?: ""
            binding.productNameTv.text = data.data?.get(0)?.productName
            // For Register Complaint


            // For TroubleShooting Video
            val allProducts = mutableListOf<SpinnerDataModel>()
            data.data?.forEach {
                allProducts.add(SpinnerDataModel(it?.id, it?.productName))

            }
            binding.productNameTv.dropDown(allProducts) { selectedItem ->
                binding.productNameTv.text = selectedItem.title.toString()
                productId = selectedItem.id.toString()
//                                apiTroubleShootVideoRequest()
            }

            binding.productList.dropDown(allProducts) { selectedItem ->
                binding.productList.text = selectedItem.title.toString()
                productId = selectedItem.id.toString()
                apiTroubleShootVideoRequest()
            }

        }


    }

    private fun apiTroubleShootVideoRequest() {
        authViewModule.troubleShootVideo(categoryId = categoryId, productId = productId)
    }

    private fun apiTroubleShootVideoResponse() {
        authViewModule.troubleShootVideos.observe(viewLifecycleOwner) {
            if (it==null){
                hideLoader()
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

                is NetworkResult.Loading -> showLoader()
                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        troubleShootVideos = Gson().fromJson(it.data, TroubleShootVideos::class.java)
                        if (troubleShootVideos?.code == 1) {
                            val data = troubleShootVideos?.data
                            data?.userManual.mlog()
                            if (type == "UserManual" && data?.userManual != null) {
                                val bundle = Bundle().apply {
                                    putString("url", data.userManual)
                                }
                                navigateToScreen(R.id.userManualFragment, bundle, navOptions)
                            }
                            if (data == null) {
                                binding.videoNotFound.visibility = View.VISIBLE
                            } else {
                                binding.videoNotFound.visibility = View.GONE
                            }

                        } else {
                            requireActivity().toast(troubleShootVideos?.message ?: "")
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

    @SuppressLint("SetTextI18n")
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
            animation(context = requireContext(), views = root)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.title.text = getString(R.string.customer_support)
            topBar.title.textSize = 16f
            handleOnPressBackButton { navigateBack() }
            customerCarePhoneNoTv.text = "18001216254"
            typeIndex.mlog()
            if (typeIndex == 0) binding.complaintRegisterLayout.visibility = View.VISIBLE else binding.complaintRegisterLayout.visibility = View.GONE
            if (typeIndex == 1) binding.customerCareLayout.visibility = View.VISIBLE else binding.customerCareLayout.visibility = View.GONE
            if (typeIndex == 2) {
                binding.ComplaintHistoryLayout.visibility = View.VISIBLE
                binding.scrollLayout.visibility = View.GONE
            } else {
                binding.ComplaintHistoryLayout.visibility = View.GONE
                binding.scrollLayout.visibility = View.VISIBLE
            }
            if (typeIndex == 3) binding.troubleShootingVideoLayout.visibility = View.VISIBLE else binding.troubleShootingVideoLayout.visibility = View.GONE

        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, null, navOptions) }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
            optionList.apply {
                add(Module(id = 0, name = "Register Complaint", image = 0))
                add(Module(id = 0, name = "Customer Care", image = 0))
                add(Module(id = 0, name = "Complaint History", image = 0))
                add(Module(id = 0, name = "Trouble Shooting Video", image = 0))
            }

            addAddressTv.setOnAnimatedClickListener {
                navigateToScreen(R.id.addAddressFragment, bundle = null, navOptions = navOptions)
            }

            customerCarePhoneNoTv.setOnAnimatedClickListener {
                openDialer()
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            frameLayout.setOnAnimatedClickListener {
                showPopup(requireContext(), message = getString(R.string.choose_you_source), positiveButtonText = getString(R.string.camera), negativeButtonText = getString(R.string.gallery), positiveAction = {
                    openCamera()
                }, negativeAction = {
                    openGallery()
                })
            }
            submitBtn.setOnAnimatedClickListener {
                if (validation()) {
                    submitBtn.showLoading()
                    apiComplaintRegisterRequest(
                        categoryId = categoryId,
                        productId = productId,
                        sin = sinTv.text.toString(),
                        complaintReason = complaintReason,
                        description = complaintEditText.text.toString(),
                        imageUrl = imageUrl,
                        addressId = addressId,
                        type = 0,
                        orderId = "",
                        purchasedSubscriptionId = ""
                    )
                }
            }
            customerSupportOptionsAdapter.submitList(optionList)
        }
    }

    private fun openCamera() {
        permissionsBuilder(Manifest.permission.CAMERA).build().send { permissions ->
            if (permissions.allGranted()) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            } else {
                requireActivity().openSettingPopup(getString(R.string.camera_permission_required_for_click_your_picture_for_update_your_profile))
            }
        }
    }

    private fun openGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        galleryLauncher.launch(pickVisualMediaRequest)
    }

    private fun optionLayoutForTroubleShootVideo(selectedTab: Int?) {
        with(binding) {
            optionLayout.forEachIndexed { index, view ->
                val textView = view as TextView
                val isSelected = index == selectedTab
                textView.setBackgroundResource(
                    if (isSelected) R.drawable.edit_text_selected_background else R.drawable._style_edittext_border
                )
                textView.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), if (isSelected) R.color.Deep_Saffron else R.color.black
                    )
                )
                textView.setOnClickListener {
                    optionLayoutForTroubleShootVideo(index)
                    when (index) {
                        0 -> {
                            type = "UserManual"
                            troubleShootVideoRv.visibility = View.GONE
                            installationVideoRv.visibility = View.GONE
                            apiTroubleShootVideoRequest()
                        }

                        1 -> {
                            type = ""
                            troubleShootVideoRv.visibility = View.VISIBLE
                            installationVideoRv.visibility = View.GONE
                            if (categoryId != "" && productId != "") {
                                apiTroubleShootVideoRequest()
                                troubleShootingVideoAdapter.submitList(troubleShootVideos?.data?.troubleVideos)
                            } else {
                                requireActivity().toast(getString(R.string.please_select_category_and_product))
                            }
                        }

                        2 -> {
                            type = ""
                            installationVideoRv.visibility = View.VISIBLE
                            troubleShootVideoRv.visibility = View.GONE
                            if (categoryId != "" && productId != "") {
                                apiTroubleShootVideoRequest()
                                installationVideoAdapter.submitList(troubleShootVideos?.data?.installationVideo)
                            } else {
                                requireActivity().toast(getString(R.string.please_select_category_and_product))
                            }
                        }
                        else -> {
                            //
                        }

                    }
                }
            }
        }
    }

    private fun selectReason() {
        binding.complaintOption.setOnCheckedChangeListener { radioGroup, id ->
            radioGroup.forEachIndexed { _, view ->
                val radioBtn = view as RadioButton
                if (id == radioBtn.id) {
                    radioBtn.setTextColor(resources.getColor(R.color.Deep_Saffron, null))
                    complaintReason = radioBtn.text.toString()
                } else {
                    radioBtn.setTextColor(resources.getColor(R.color.black, null))
                }

                if (id == radioGroup.getChildAt(3).id) {
                    with(binding) {
                        TextViewYourComplain.visibility = View.VISIBLE
                        LayoutYourComplain.visibility = View.VISIBLE
                    }
                } else {
                    with(binding) {
                        TextViewYourComplain.visibility = View.GONE
                        LayoutYourComplain.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setDataInAdapter() {
        with(binding) {
            customerSupportRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = customerSupportOptionsAdapter
            }
            faqListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = faqListAdapter
            }
            ComplaintHistoryRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = complaintHistoryAdapter.withLoadStateFooter(LoaderAdapter())

                complaintHistoryAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> hideLoader()
                        LoadState.Loading -> showLoader()
                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (complaintHistoryAdapter.itemCount == 0) {
                                notFound.visibility = View.VISIBLE
                            } else {
                                notFound.visibility = View.GONE
                            }
                        }
                        else -> {
                            //
                        }
                    }
                }
            }
            troubleShootVideoRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = troubleShootingVideoAdapter
            }
            installationVideoRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = installationVideoAdapter

            }
        }
    }

    private fun validation(): Boolean {
        with(binding) {
            if (complaintReason.isEmpty()) {
                requireActivity().toast(getString(R.string.please_select_complaint_reason))
                return false
            } else if (productId == "") {
                requireActivity().toast(getString(R.string.please_select_product))
                return false
            } else if (addressId == "") {
                requireActivity().toast(getString(R.string.please_select_address))
                return false
            }
        }
        return true
    }

    companion object {
        private var categoryListingResponse: CategoryListingResponse? = null
        private var troubleShootVideos: TroubleShootVideos? = null
        private var FAQListResponse: FAQResponse? = null
        private var type = ""
    }

}