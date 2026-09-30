package com.app.bharatphoton.fragment.profile

import SpinnerDataModel
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.Constant
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.RecyclerAnimationHelper
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.SharedViewModule
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentEditProfileBinding
import com.app.bharatphoton.fragment.auth.ChangePhoneAndEmailBottomSheetFragment
import com.app.bharatphoton.module.profileResponse.PersonalEditProfileResponse
import com.app.bharatphoton.module.profileResponse.PersonalProfileResponse
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

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>() {

    private lateinit var imageFile: File
    private lateinit var sharedViewModule: SharedViewModule

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val imageBitmap = it.data?.extras?.get("data")
        val image = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
        if (image != null) {
            binding.progressBar.visibility = View.VISIBLE
            uploadToAws.uploadFile(file = image, onSuccess = { url ->
                binding.progressBar.visibility = View.GONE
                Glide.with(requireContext()).load(url).placeholder(R.drawable.placeholder).into(binding.profile)
                imageUrl = url
            }, onError = {
                binding.progressBar.visibility = View.GONE
            }, onProgress = {

            })
        }
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val file = imageBitmapToFile(context = requireContext(), bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    imageUrl = url
                    binding.progressBar.visibility = View.GONE
                    Glide.with(requireContext()).load(url).placeholder(R.drawable.placeholder).into(binding.profile)
                }, onError = {
                    binding.progressBar.visibility = View.GONE
                }, onProgress = {

                })
            }
        }
    }

    private var imageUrl = ""
    private var selectState = ""
    private var selectedCity = ""
    private val navOptions by lazy { navigateWithAnimation(requireContext()) }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private val viewModule by viewModels<ProfileViewModule>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModule = ViewModelProvider(requireActivity())[SharedViewModule::class.java]

        initData()
        onClick()
        apiPersonalEditProfileResponse()
        apiProfileRequest()
        apiProfileResponse()
        apiStateRequest()
        apiStateResponse { stateListResponse ->
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
    ): FragmentEditProfileBinding = FragmentEditProfileBinding.inflate(layoutInflater)

    private fun apiPersonalEditProfileRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("address", address.text.toString())
                addProperty("firstName", firstName.text.toString())
                addProperty("lastName", lastName.text.toString())
                addProperty("country_code", ccp.selectedCountryCodeWithPlus)
                addProperty("phoneNo", phoneNumberEt.text.toString())
                if (imageUrl.isNotEmpty()) {
                    addProperty("profileImage", imageUrl)
                }
                addProperty("city", selectedCity)
                addProperty("state", selectState)
                addProperty("zipCode", zipCode.text.toString())
            }
            viewModule.personalEditProfile(request)
        }
    }

    private fun apiPersonalEditProfileResponse() {
        viewModule.personalEditProfile.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    binding.saveBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.saveBtn.hideLoading()
                    personalEditProfileResponse = Gson().fromJson(it.data, PersonalEditProfileResponse::class.java)
                    if (personalEditProfileResponse?.code == 1) {
                        sharedPrefManager.saveString(Constant.SharedPref.PROFILE_PIC, personalEditProfileResponse?.data?.profileImage ?: "")
                        sharedPrefManager.saveString(Constant.SharedPref.PROFILE_NAME, personalEditProfileResponse?.data?.firstName ?: "")
                        sharedViewModule._profilePicUrl.value = personalEditProfileResponse?.data?.profileImage ?: ""
                        sharedPrefManager.saveString("phoneNumber", personalEditProfileResponse?.data?.phoneNo.toString())
                        navigateBack()
                    } else {
                        requireActivity().toast(personalEditProfileResponse?.message.toString())
                    }
                }

                is NetworkResult.Error -> {
                    binding.saveBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage.toString()).optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        //
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
                    try {
                        profileResponse = Gson().fromJson(it.data, PersonalProfileResponse::class.java)
                        if (profileResponse?.code == 1) {
                            with(binding) {
                                val data = profileResponse?.data
                                editProfileLayout.visibility = View.VISIBLE
                                emailAddress.text = data?.email
                                Glide.with(requireContext()).load(data?.profileImage).placeholder(R.drawable.placeholder).into(binding.profile)
                                firstName.setText(data?.firstName)
                                lastName.setText(data?.lastName)
                                address.setText(data?.address)
                                zipCode.setText(data?.zipCode)
                                data?.phoneNo.mlog()
                                phoneNumberEt.text = data?.phoneNo
                                ccp.setCountryForPhoneCode(data?.countryCode?.toInt() ?: 0)
                                stateSpinner.text = data?.state
                                citySpinner.text = data?.city
                                selectState = data?.state ?: ""
                                selectedCity = data?.city ?: ""
                                "state ${data?.state}".mlog()
                                if (data?.state != null) {
                                    apiCityListRequest(stateName = data.state.toString())
                                }
                            }

                        } else {
                            requireActivity().toast(profileResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        //
                    }
                }

                is NetworkResult.Error -> {
                    hideLoader()

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
            RecyclerAnimationHelper.animation(context = requireContext(), views = root)
            topBar.title.text = getString(R.string.edit_profile)
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.notification.visibility = View.GONE
            topBar.cart.visibility = View.GONE
            handleOnPressBackButton {
                navigateBack()
            }
        }
    }

    private fun onClick() {
        with(binding) {
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

            phoneNumberEt.setOnAnimatedClickListener {
                val bundle = Bundle().apply {
                    putString("type", "phoneNumber")
                    putString("phone", phoneNumberEt.text.toString())
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
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            addNewAddress.setOnAnimatedClickListener {
                navigateToScreen(R.id.addAddressFragment, null, navOptions)
            }
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
            saveBtn.setOnAnimatedClickListener {
                if (validate()) {
                    apiPersonalEditProfileRequest()
                }
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

    private fun validate(): Boolean {
        with(binding) {
            if (firstName.text.toString().trim().isBlank()) {
                firstName.error = getString(R.string.please_enter_first_name)
                return false
            } else if (lastName.text.toString().isBlank()) {
                lastName.error = getString(R.string.please_enter_last_name)
                return false
            } else if (address.text.toString().isBlank()) {
                address.error = getString(R.string.please_enter_address)
                return false
            } else if (zipCode.text.toString().isBlank()) {
                zipCode.error = getString(R.string.please_enter_zip_code)
                return false
            }
        }
        return true
    }

    companion object {
        private var personalEditProfileResponse: PersonalEditProfileResponse? = null
        private var profileResponse: PersonalProfileResponse? = null
    }
}