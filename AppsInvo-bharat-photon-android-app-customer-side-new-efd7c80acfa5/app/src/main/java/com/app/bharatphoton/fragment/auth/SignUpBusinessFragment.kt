package com.app.bharatphoton.fragment.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSignUpBusinessBinding
import com.app.bharatphoton.module.authResponse.RegisterResponse
import com.app.bharatphoton.retrofit.Constants
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import kotlin.properties.Delegates

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignUpBusinessFragment : BaseFragment<FragmentSignUpBusinessBinding>() {

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { url ->
        if (url.resultCode == Activity.RESULT_OK) {
            val imageBitmap = url.data?.extras?.get("data")
            val file = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
            SharedData.region.mlog()
            if (file != null && SharedData.region != "") {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    binding.progressBar.visibility = View.GONE
                    binding.gstCertificate.visibility = View.VISIBLE
                    imageUrl = url
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.gstCertificate)
                }, onError = {

                }, onProgress = {

                })
            }
        }
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        if (it != null) {
            val file = imageBitmapToFile(context = requireContext(), bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it))
            if (file != null && SharedData.region != "") {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    binding.progressBar.visibility = View.GONE
                    binding.gstCertificate.visibility = View.VISIBLE
                    imageUrl = url
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.camera_icon).into(binding.gstCertificate)
                }, onError = {

                }, onProgress = {

                })
            }
        }
    }

    private var imageUrl = ""
    private var status = "0"
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private val viewModule by viewModels<AuthViewModule>()

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.register.postValue(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        apiRegisterResponse()
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSignUpBusinessBinding = FragmentSignUpBusinessBinding.inflate(layoutInflater)

    private fun apiRegisterRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("companyName", businessName.text.toString().trim())
                addProperty("businessEmail", businessEmailAddress.text.toString().trim())
                addProperty("businessPhoneNo", businessContactNumber.text.toString().trim())
                addProperty("gstNumber", gstNumber.text.toString().trim())
                addProperty("gstFile", imageUrl)
                addProperty("userType", "2")
                addProperty("password", password.text.toString().trim())
                addProperty("contactPerson", personName.text.toString().trim())
                addProperty("designation", desgination.text.toString().trim())
                addProperty("contactNo", phoneNumber.text.toString().trim())
                addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                addProperty("contactPersoncountryCode", ccp.selectedCountryCodeWithPlus)
            }
            viewModule.register(request)
        }
    }

    private fun apiRegisterResponse() {
        viewModule.register.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.submitBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.submitBtn.hideLoading()
                    try {
                        registerResponse = Gson().fromJson(it.data, RegisterResponse::class.java)
                        sharedPrefManager.saveString(Constants.SharedPref.AUTH_TOKEN, registerResponse?.data?.authToken ?: "")
                        if (registerResponse?.code == 1) {
                            val bundle = Bundle().apply {
                                putString("notificationType", "register")
                            }
                            navigateToScreen(id = R.id.homeFragment, bundle = bundle, navOptions = navOptions)
                        } else {
                            requireActivity().toast(registerResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    binding.submitBtn.hideLoading()
                    val errorMessage = JSONObject(it.errorMessage.toString()).getString("message")
                    requireActivity().toast(errorMessage)
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.GONE
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
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        galleryLauncher.launch(pickVisualMediaRequest)
    }

    private fun setClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.title.text = getString(R.string.signup_for_business)
            viewLifecycleOwner.handleOnPressBackButton { navigateBack() }
            chooseFile.setOnAnimatedClickListener { showPopup(requireContext()) }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            termsAndCondition.setOnAnimatedClickListener { navigateToScreen(R.id.termsAndConditionsFragment, bundle = null, navOptions = navOptions) }
            privacyPolicy.setOnAnimatedClickListener { navigateToScreen(R.id.privacyPolicyFragment, bundle = null, navOptions = navOptions) }
            submitBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiRegisterRequest()
                }
            }
            close.setOnAnimatedClickListener {
                gstCertificate.visibility = View.GONE
                imageUrl = ""
            }
            check.setOnAnimatedClickListener {
                if (status == "1") {
                    check.setImageResource(R.drawable.check)
                    status = "0"
                } else {
                    check.setImageResource(R.drawable.uncheck)
                    status = "1"
                }
            }
        }
    }

    private fun showPopup(context: Context) {
        var alertDialog: AlertDialog by Delegates.notNull()

        val alertDialogBuilder = AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.choose_you_source))
            setCancelable(true)
            setPositiveButton(getString(R.string.camera)) { _, _ ->
                openCamera()
                alertDialog.dismiss()
            }
            setNegativeButton(getString(R.string.gallery)) { _, _ ->
                openGallery()
                alertDialog.dismiss()
            }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun validate(): Boolean {
        with(binding) {
            if (businessName.text.toString().isBlank()) {
                businessName.apply {
                    error = context.getString(R.string.please_enter_business_name)
                    requestFocus()
                    return false
                }
            } else if (businessContactNumber.text.toString().isBlank()) {
                businessContactNumber.apply {
                    error = context.getString(R.string.please_enter_business_contact_number)
                    requestFocus()
                    return false
                }
            } else if (businessContactNumber.text.toString().length < 10) {
                businessContactNumber.apply {
                    error = context.getString(R.string.please_enter_valid_business_contact_number)
                    requestFocus()
                    return false
                }
            } else if (businessEmailAddress.text.toString().isBlank()) {
                businessEmailAddress.apply {
                    error = context.getString(R.string.please_enter_business_email_address)
                    requestFocus()
                    return false
                }
            } else if (!businessEmailAddress.text.toString().trim().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                businessEmailAddress.apply {
                    error = getString(R.string.please_enter_valid_email)
                    requestFocus()
                    return false
                }
            } else if (password.text.toString().isBlank()) {
                password.apply {
                    error = getString(R.string.please_enter_password)
                    requestFocus()
                    return false
                }
            } else if (gstNumber.text.toString().isBlank()) {
                gstNumber.apply {
                    error = getString(R.string.please_enter_gst_number)
                    requestFocus()
                    return false
                }
            } else if (gstNumber.text.toString().length < 15) {
                gstNumber.apply {
                    error = context.getString(R.string.please_enter_valid_gst_number)
                    requestFocus()
                    return false
                }
            } else if (imageUrl == "") {
                requireActivity().toast(getString(R.string.please_upload_gst_certificate))
                return false
            } else if (personName.text.toString().isBlank()) {
                personName.apply {
                    error = getString(R.string.please_enter_contact_person_name)
                    requestFocus()
                    return false
                }
            } else if (desgination.text.toString().isBlank()) {
                desgination.apply {
                    error = getString(R.string.please_enter_designation)
                    requestFocus()
                    return false
                }
            } else if (phoneNumber.text.toString().isBlank()) {
                phoneNumber.apply {
                    error = context.getString(R.string.please_enter_contact_person_mobile_number)
                    requestFocus()
                    return false
                }
            } else if (phoneNumber.text.toString().length < 10) {
                phoneNumber.apply {
                    error = context.getString(R.string.please_enter_valid_contact_person_mobile_number)
                    requestFocus()
                    return false
                }
            } else if (status == "1") {
                requireActivity().toast(getString(R.string.please_accept_terms_condition_and_privacy_policy))
                return false
            } else {

            }
        }
        return true
    }

    companion object {
        private var registerResponse: RegisterResponse? = null
    }

}