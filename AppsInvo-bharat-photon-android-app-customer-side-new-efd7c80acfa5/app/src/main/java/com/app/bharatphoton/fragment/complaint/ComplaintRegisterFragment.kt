package com.app.bharatphoton.fragment.complaint

import SpinnerDataModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentComplaintRegisterBinding
import com.app.bharatphoton.viewModule.AddressViewModule
import com.app.bharatphoton.viewModule.ComplaintViewModule
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import dagger.hilt.android.AndroidEntryPoint
import dropDown
import java.io.File

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ComplaintRegisterFragment : BaseFragment<FragmentComplaintRegisterBinding>() {
    val navOptions by lazy { navigateWithAnimation(requireContext()) }
    private var productId = ""
    private var productName = ""
    private var categoryId = ""
    private var addressId = ""
    private var orderId = ""
    private var categoryName = ""
    private var purchasedSubscriptionId = ""
    private var complaintType = -1
    private var imageUrl = ""
    private var sin = ""
    private var complaintReason = ""
    private var sinList = ArrayList<String>()
    private val viewModule by viewModels<ComplaintViewModule>()
    private val ViewModule by viewModels<AddressViewModule>()


    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val mimeType = requireActivity().contentResolver.getType(uri)
            val file: File? = when {
                mimeType?.startsWith("image/") == true -> {
                    val bitmap = getBitmap(requireActivity().contentResolver, uri)
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


    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentComplaintRegisterBinding = FragmentComplaintRegisterBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        selectReason()
        apiComplaintRegisterResponse { registerComplaintResponse ->
            binding.submitBtn.hideLoading()
            val data = registerComplaintResponse.data
            val bundle = Bundle().apply {
                putString("complaintNo", data?.uuid?:"")
                putString("complaintDate", data?.createdAt?:"")
            }
            navigateToScreen(id = R.id.complaintRegisteredFragment, bundle = bundle, navOptions = navOptions)
        }
        apiAddressListRequest("")
        apiAddressListResponse { addressListResponse ->
            if (addressListResponse.data?.isNotEmpty() == true) {
                binding.addAddressTv.visibility = View.GONE
            } else {
                binding.addAddressTv.visibility = View.VISIBLE
            }
            addressId = addressListResponse.data?.get(0)?.id ?: ""
            addressListResponse.data?.let {
                binding.addressTv.dropDown(list = it.map {
                    SpinnerDataModel(id = it?.id, title = it?.address + ", " + it?.city + ", " + it?.state + ", " + it?.zipCode)
                }, isShowIndexValue = 0, onClickItem = {
                    binding.addressTv.text = it.title.toString()?:""
                    addressId = it.id.toString()
                })
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
            } else if (imageUrl == "") {
                requireActivity().toast(getString(R.string.please_upload_an_image))
                return false
            }
        }
        return true
    }


    private fun onClick() {
        with(binding) {
            submitBtn.setOnAnimatedClickListener {
                if (validation()) {
                    submitBtn.showLoading()
                    apiComplaintRegisterRequest(
                        categoryId = categoryId,
                        productId = productId,
                        sin = sin,
                        complaintReason = complaintReason,
                        description = complaintEt.text.toString(),
                        imageUrl = imageUrl,
                        addressId = addressId,
                        type = complaintType,
                        orderId = orderId,
                        purchasedSubscriptionId = purchasedSubscriptionId
                    )
                }
            }
            topBar.notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
            topBar.cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            addAddressTv.setOnAnimatedClickListener {
                navigateToScreen(R.id.addAddressFragment, bundle = null, navOptions = navOptions)
            }
            frameLayout.setOnAnimatedClickListener {
                showPopup(requireContext(), message = getString(R.string.choose_you_source), positiveButtonText = getString(R.string.camera), negativeButtonText = getString(R.string.gallery), positiveAction = {
                    openCamera()
                }, negativeAction = {
                    openGallery()
                })
            }
            topBar.back.setOnAnimatedClickListener { navigateBack() }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            topBar.title.text = getString(R.string.register_complaint)
            topBar.title.textSize = 18f
            binding.topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            NotificationManage.notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    binding.topBar.notificationCountTv.text = it.count.toString()
                }
            }

            sinList = arguments?.getStringArrayList("sinList") ?: arrayListOf()

            handleOnPressBackButton { navigateBack() }
            productId = arguments?.getString("productId", "") ?: ""
            productName = arguments?.getString("productName", "") ?: ""
            categoryName = arguments?.getString("categoryName", "") ?: ""
            orderId = arguments?.getString("orderId", "") ?: ""
            categoryId = arguments?.getString("categoryId", "") ?: ""
            purchasedSubscriptionId = arguments?.getString("subscriptionId", "") ?: ""
            complaintType = arguments?.getInt("complaintType", -1) ?: -1
            productNameSubscriptionTv.text = productName
            categorySubscriptionTv.text = categoryName
            if (sinList.isNotEmpty()) {
                sin = sinList[0]
                sinList.map {
                    SpinnerDataModel(id = it, title = it)
                }.let {
                    sinSubscriptionTv.dropDown(list = it, isShowIndexValue = 0, onClickItem = {
                        sinSubscriptionTv.text = it.title.toString()
                        sin = it.title.toString()
                    })
                }
            } else {
                sin = arguments?.getString("sin", "") ?: ""
                sinSubscriptionTv.text = sin
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
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        galleryLauncher.launch(pickVisualMediaRequest)
    }

    private fun selectReason() {
        binding.complaintOption.setOnCheckedChangeListener { radioGroup, id ->
            radioGroup.forEachIndexed { index, view ->
                val radioBtn = view as RadioButton
                if (id == radioBtn.id) {
                    radioBtn.setTextColor(resources.getColor(R.color.Deep_Saffron, null))
                    complaintReason = radioBtn.text.toString()
                } else {
                    radioBtn.setTextColor(resources.getColor(R.color.black, null))
                }

                if (id == radioGroup.getChildAt(3).id) {
                    binding.TextViewYourComplain.visibility = View.VISIBLE
                    binding.LayoutYourComplain.visibility = View.VISIBLE
                } else {
                    binding.TextViewYourComplain.visibility = View.GONE
                    binding.LayoutYourComplain.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModule.registerComplaint.postValue(null)
        ViewModule.addressList.postValue(null)
    }
}