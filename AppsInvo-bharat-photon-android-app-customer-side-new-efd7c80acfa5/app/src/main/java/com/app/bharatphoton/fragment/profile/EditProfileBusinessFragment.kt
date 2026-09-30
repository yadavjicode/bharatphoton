package com.app.bharatphoton.fragment.profile

import SpinnerDataModel
import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentEditProfileBusinessBinding
import com.app.bharatphoton.fragment.auth.ChangePhoneAndEmailBottomSheetFragment
import com.app.bharatphoton.module.profileResponse.BusinessEditProfileResponse
import com.app.bharatphoton.module.profileResponse.ProfileResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ProfileViewModule
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import dropDown
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditProfileBusinessFragment : BaseFragment<FragmentEditProfileBusinessBinding>() {


    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val imageBitmap = it.data?.extras?.get("data")
        if (type == "gst") {
            binding.gstProgressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.VISIBLE
        }
        val image = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
        if (image != null) {
            uploadToAws.uploadFile(file = image, onSuccess = { url ->
                if (type == "gst") {
                    binding.imageLayout.visibility = View.VISIBLE
                    binding.gstProgressBar.visibility = View.GONE
                    gstImageUrl = url
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.placeholder).into(binding.imageView)
                } else {
                    imageUrl = url
                    binding.progressBar.visibility = View.GONE
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.profile)
                }
            }, onError = {
                binding.progressBar.visibility = View.GONE
            }, onProgress = {

            })
        }
    }

    private var documentLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val context = requireContext()
            val file = uriToFile(context, uri)
            if (file != null) {
                binding.gstProgressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    binding.gstProgressBar.visibility = View.GONE
                    binding.imageLayout.visibility = View.VISIBLE
                    gstImageUrl = url

                    if (file.extension.equals("pdf", true)) {
                        val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                        val page = pdfRenderer.openPage(0)
                        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        page.close()
                        pdfRenderer.close()

                        binding.imageView.setImageBitmap(bitmap)
                    } else {
                        Glide.with(requireContext()).load(url).into(binding.imageView)
                    }
                }, onError = {
                    binding.gstProgressBar.visibility = View.GONE
                    requireActivity().toast(getString(R.string.failed_to_upload_document))
                }, onProgress = {
                    // Handle progress if needed
                })
            }
        }
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val file = imageBitmapToFile(context = requireContext(), bitmap = getBitmap(requireActivity().contentResolver, uri))
            if (file != null) {
                if (type == "gst") {
                    binding.gstProgressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.VISIBLE
                }
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    if (type == "gst") {
                        binding.imageLayout.visibility = View.VISIBLE
                        binding.gstProgressBar.visibility = View.GONE
                        gstImageUrl = url
                        Glide.with(requireContext()).load(url).placeholder(R.drawable.placeholder).into(binding.imageView)
                    } else {
                        imageUrl = url
                        binding.progressBar.visibility = View.GONE
                        Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.profile)
                    }
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            }
        }
    }

    private var gstImageUrl = ""
    private var imageUrl = ""
    private var selectState = ""
    private var selectedCity = ""
    private var type = ""

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private val viewModule by viewModels<ProfileViewModule>()
    private val stateList = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        apiProfileRequest()
        apiProfileResponse()
        apiEditProfileResponse()
        apiStateRequest()
        apiStateResponse { stateListResponse ->
            stateList.clear()
            stateListResponse.data?.forEach { state ->
                stateList.add(state ?: "")
            }
            binding.stateSpinner.dropDown(list = stateListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.stateSpinner.text = it.title.toString()
                apiCityListRequest(stateName = it.title.toString())
                selectState = it.title.toString()
            })
        }
        apiCityListResponse { cityListResponse ->
            binding.citySpinner.dropDown(list = cityListResponse.data?.map {
                SpinnerDataModel(id = null, title = it)
            } ?: emptyList(), isShowIndexValue = -1, onClickItem = {
                binding.citySpinner.text = it.title.toString()
                selectedCity = it.title.toString()
            })
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentEditProfileBusinessBinding = FragmentEditProfileBusinessBinding.inflate(layoutInflater)

    private fun ContentResolver.getFileName(uri: Uri): String {
        var name = ""
        val cursor = query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    private fun apiEditProfileRequest() {
        with(binding) {
            val request = JsonObject().apply {
                if (imageUrl.isNotEmpty()) {
                    addProperty("profileImage", imageUrl)
                }
                addProperty("address", address.text.toString().trim())
                addProperty("zipCode", zipCodeTV.text.toString().trim())
                addProperty("secondayPhoneNo", secondaryMobileNumber.text.toString().trim())
                addProperty("state", selectState)
                addProperty("city", selectedCity)
                addProperty("gstNumber", gstEt.text.toString().trim())
                addProperty("companyName", companyNameEt.text.toString().trim())
                if (gstImageUrl.isNotEmpty()) {
                    addProperty("gstFile", gstImageUrl)
                }
                addProperty("contactPerson", contactPersonName.text.toString().trim())
                addProperty("designation", designation.text.toString().trim())
                addProperty("contactNo", contactMobileNumber.text.toString().trim())
            }
            viewModule.personalEditProfile(request)
        }
    }

    private fun apiEditProfileResponse() {
        viewModule.personalEditProfile.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    businessEditProfileResponse = Gson().fromJson(it.data, BusinessEditProfileResponse::class.java)
                    if (businessEditProfileResponse?.code == 1) {
                        sharedPrefManager.saveString(Constant.SharedPref.PROFILE_PIC, businessEditProfileResponse?.data?.profileImage ?: "")
                        navigateBack()
                    } else {
                        requireActivity().toast(businessEditProfileResponse?.message.toString())
                    }
                }

                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val message = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(message)
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

    private fun apiProfileRequest() {
        viewModule.userProfile()
    }

    private fun apiProfileResponse() {
        viewModule.userProfile.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    profileResponse = Gson().fromJson(it.data, ProfileResponse::class.java)
                    if (profileResponse?.code == 1) {
                        with(binding) {
                            val data = profileResponse?.data
                            emailAddress.text = data?.businessEmail
                            address.setText(data?.address)
                            designation.setText(data?.designation)
                            contactPersonName.setText(data?.contactPerson)
                            phoneNumber.text = data?.businessPhoneNo.toString()
                            companyNameEt.setText(data?.companyName)
                            gstEt.setText(data?.gstNumber)
                            contactMobileNumber.setText(data?.contactNo ?: "")
                            zipCodeTV.setText(data?.zipCode ?: "")
                            secondaryMobileNumber.setText(data?.secondayPhoneNo ?: "")
                            sharedPrefManager.saveString("phoneNumber", data?.businessPhoneNo.toString())
                            if (data?.state != null) {
                                apiCityListRequest(data.state)
                            }
                            selectState = data?.state ?: ""
                            selectedCity = data?.city ?: ""
                            citySpinner.text = data?.city
                            stateSpinner.text = data?.state

                            Glide.with(requireContext()).load(data?.profileImage).placeholder(R.drawable.placeholder).into(profile)
                            upload.visibility = View.GONE
                            imageLayout.visibility = View.VISIBLE

                            Glide.with(requireContext()).load(data?.gstFile).placeholder(R.drawable.placeholder).into(binding.imageView)

                        }

                    } else {
                        requireActivity().toast(profileResponse?.message.toString())
                    }

                }

                is NetworkResult.Error -> {
                    hideLoader()
                    try {
                        val message = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(message)
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
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }
            topBar.title.text = getString(R.string.edit_profile)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.notification.visibility = View.GONE
            topBar.cart.visibility = View.GONE
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            saveBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiEditProfileRequest()
                }
            }
            phoneNumber.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("type", "phoneNumber")
                    putString("phone", phoneNumber.text.toString())
                }
                val changePhoneNumberScreen = ChangePhoneAndEmailBottomSheetFragment().apply {
                    arguments = bundle
                }
                changePhoneNumberScreen.show(childFragmentManager, "ChangePhoneAndEmailBottomSheetFragment")
            }
            emailAddress.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("type", "emailAddress")
                    putString("email", emailAddress.text.toString())
                }
                val changePhoneNumberScreen = ChangePhoneAndEmailBottomSheetFragment().apply {
                    arguments = bundle
                }
                changePhoneNumberScreen.show(childFragmentManager, "ChangePhoneAndEmailBottomSheetFragment")
            }
            close.setOnAnimatedClickListener {
                imageLayout.visibility = View.GONE
                upload.visibility = View.VISIBLE
            }
            camera.setOnAnimatedClickListener {
                showPopup(requireContext(), message = getString(R.string.choose_you_source), positiveButtonText = getString(R.string.camera), negativeButtonText = getString(R.string.gallery), positiveAction = {
                    openCamera()
                }, negativeAction = {
                    openGallery()
                })
            }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            upload.setOnAnimatedClickListener {
                type = "gst"
                showPopup(requireContext(), message = getString(R.string.choose_you_source), positiveButtonText = getString(R.string.document), negativeButtonText = getString(R.string.camera), positiveAction = {
                    openDocumentPicker()
                }, negativeAction = {
                    openCamera()
                })
            }
            handleOnPressBackButton {
                navigateBack()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
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

    private fun openDocumentPicker() {
        documentLauncher.launch("*/*")
    }

    private fun openGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        galleryLauncher.launch(pickVisualMediaRequest)
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, contentResolver.getFileName(uri))
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return file
    }

    private fun validate(): Boolean {
        with(binding) {
            if (emailAddress.text.isBlank()) {
                emailAddress.error = getString(R.string.please_enter_email_address)
                return false
            } else if (!emailAddress.text.toString().trim().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                emailAddress.error = getString(R.string.please_enter_valid_email)
                return false
            } else if (companyNameEt.text.toString().isBlank()) {
                companyNameEt.error = getString(R.string.please_enter_company_name)
                return false
            } else if (gstEt.text.toString().isBlank()) {
                gstEt.error = getString(R.string.please_enter_gst_number)
                return false
            } else if (phoneNumber.text.isNullOrEmpty()) {
                phoneNumber.error = getString(R.string.please_enter_phone_number)
                return false
            } else if (phoneNumber.text.toString().length <= 4) {
                phoneNumber.error = getString(R.string.please_enter_valid_phone_number)
                return false
            } else if (secondaryMobileNumber.text.toString().isBlank()) {
                secondaryMobileNumber.error = getString(R.string.please_enter_secondary_phone_number)
                return false
            } else if (secondaryMobileNumber.text.toString().length <= 4) {
                secondaryMobileNumber.error = getString(R.string.please_enter_valid_secondary_phone_number)
                return false
            } else if (contactMobileNumber.text.isNullOrEmpty()) {
                contactMobileNumber.error = getString(R.string.please_enter_phone_number)
                return false
            } else if (contactMobileNumber.text.toString().length <= 4) {
                contactMobileNumber.error = getString(R.string.please_enter_valid_contact_phone_number)
                return false
            } else if (address.text.toString().isBlank()) {
                address.error = getString(R.string.please_enter_address)
                return false
            } else if (zipCodeTV.text.toString().isBlank()) {
                zipCodeTV.error = getString(R.string.please_enter_zip_code)
                return false
            } else if (contactPersonName.text.toString().isBlank()) {
                contactPersonName.error = getString(R.string.please_enter_contact_person_name)
                return false
            } else if (designation.text.toString().isBlank()) {
                designation.error = getString(R.string.please_enter_designation)
                return false
            }
        }
        return true
    }

    companion object {
        private var businessEditProfileResponse: BusinessEditProfileResponse? = null
        private var profileResponse: ProfileResponse? = null
    }

}