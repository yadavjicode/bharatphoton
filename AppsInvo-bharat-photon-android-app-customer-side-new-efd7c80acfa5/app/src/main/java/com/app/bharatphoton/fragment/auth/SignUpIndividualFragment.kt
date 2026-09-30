package com.app.bharatphoton.fragment.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.SpannedString.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import coil.load
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentSignUpIndividualBinding
import com.app.bharatphoton.module.authResponse.RegisterResponse
import com.app.bharatphoton.retrofit.Constants.SharedPref.AUTH_TOKEN
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AuthViewModule
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class SignUpIndividualFragment : BaseFragment<FragmentSignUpIndividualBinding>() {

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data")
            val image = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
            if (image != null) {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = image, onSuccess = { url ->
                    binding.profile.load(url) {
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.camera_icon)
                    }
                    binding.progressBar.visibility = View.GONE
                    imageUrl = url
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            }
        }
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val file = imageBitmapToFile(context = requireContext(), bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    imageUrl = url
                    binding.profile.load(url) {
                        placeholder(R.drawable.placeholder)
                        error(R.drawable.camera_icon)
                    }
                    binding.progressBar.visibility = View.GONE
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            }
        }
    }

    private var imageUrl = ""
    private var status = "0"
    private var userType = "individual"
    private val authViewModule by viewModels<AuthViewModule>()
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    var image = ""

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModule.register.postValue(null)
        status = "0"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        termsAndCondition()
        apiRegisterResponse()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSignUpIndividualBinding = FragmentSignUpIndividualBinding.inflate(layoutInflater)

    private fun apiRegisterResponse() {
        authViewModule.register.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.submitBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.submitBtn.hideLoading()
                    try {
                        registerResponse = Gson().fromJson(it.data, RegisterResponse::class.java)
                        if (registerResponse?.code == 1) {
                            requireActivity().toast(registerResponse?.message ?: "")
                            sharedPrefManager.saveString(AUTH_TOKEN, registerResponse?.data?.authToken ?: "")
                            val bundle = Bundle().apply {
                                putString("notificationType", "register")
                            }
                            navigateToScreen(R.id.homeFragment, bundle = bundle, navOptions = navOptions)
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
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
            userType = sharedPrefManager.getString("type", "")
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.topBarFunctionality.visibility = View.GONE
            topBar.title.text = getString(R.string.signup_for_individual)

            viewLifecycleOwner.handleOnPressBackButton {
                navigateBack()
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

    private fun openGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        galleryLauncher.launch(pickVisualMediaRequest)
    }

    private fun requestSignUp() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("firstName", firstName.text.toString().trim())
                addProperty("lastName", lastName.text.toString().trim())
                addProperty("profileImage", imageUrl)
                addProperty("email", emailAddress.text.toString().trim())
                addProperty("phoneNo", phoneNumber.text.toString().trim())
                addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                addProperty("address", address.text.toString().trim())
                addProperty("userType", "1")
            }
            authViewModule.register(request)
        }
    }

    private fun setClick() {
        with(binding) {
            submitBtn.setOnAnimatedClickListener {
                if (validate()) {
                    requestSignUp()
                }
            }
            camera.setOnAnimatedClickListener {
                showPopup(
                    requireContext(),
                    message = getString(R.string.choose_you_source),
                    positiveButtonText = getString(R.string.camera),
                    negativeButtonText = getString(R.string.gallery),
                    positiveAction = {
                        openCamera()
                    },
                    negativeAction = {
                        openGallery()
                    })
            }

            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
            checkLayout.setOnAnimatedClickListener {
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

    private fun termsAndCondition() {
        val text1 = "Accept all the"
        val text2 = " Terms of use"
        val text3 = "&"
        val text4 = "Privacy Policy"
        val finalText = "$text1 $text2 $text3 $text4"
        val spannableString = SpannableString(finalText)
        spannableString.apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black, null)), text1.length, text1.length + text2.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        navigateToScreen(R.id.termsAndConditionsFragment, null, navOptions)

                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.color = resources.getColor(R.color.Deep_Saffron, null)
                        ds.isUnderlineText = false
                    }

                }, finalText.indexOf(text2), finalText.indexOf(text2) + text2.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(ForegroundColorSpan(resources.getColor(R.color.black, null)), finalText.indexOf(text3), finalText.indexOf(text3) + text3.length, SPAN_EXCLUSIVE_EXCLUSIVE)

            setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        navigateToScreen(R.id.privacyPolicyFragment, null, navOptions)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)

                        ds.color = resources.getColor(R.color.Deep_Saffron, null)
                        ds.isUnderlineText = false

                    }
                }, finalText.indexOf(text4), finalText.indexOf(text4) + text4.length, SPAN_EXCLUSIVE_EXCLUSIVE
            )

        }
        binding.TextViewAcceptAll.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun validate(): Boolean {
        with(binding) {
            if (firstName.text.toString().isBlank()) {
                firstName.apply {
                    error = getString(R.string.please_enter_first_name)
                    requestFocus()
                    return false
                }
            } else if (lastName.text.toString().isBlank()) {
                lastName.apply {
                    error = getString(R.string.please_enter_last_name)
                    requestFocus()
                    return false
                }
            } else if (phoneNumber.text.toString().isBlank()) {
                phoneNumber.apply {
                    error = getString(R.string.please_enter_phone_number)
                    requestFocus()
                    return false
                }
            } else if (phoneNumber.text.toString().length < 10) {
                phoneNumber.apply {
                    error = getString(R.string.please_enter_valid_phone_number)
                    requestFocus()
                    return false
                }
            } else if (emailAddress.text.toString().isBlank()) {
                emailAddress.apply {
                    error = "empty"
                    requestFocus()
                    return false
                }
            } else if (!emailAddress.text.toString().matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                emailAddress.apply {
                    error = getString(R.string.please_enter_valid_email)
                    requestFocus()
                    return false
                }
            } else if (address.text.toString().isBlank()) {
                address.apply {
                    error = "empty"
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
        var registerResponse: RegisterResponse? = null
    }
}