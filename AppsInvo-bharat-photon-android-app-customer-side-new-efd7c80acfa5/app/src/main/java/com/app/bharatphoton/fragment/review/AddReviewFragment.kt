package com.app.bharatphoton.fragment.review

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentAddReviewBinding
import com.app.bharatphoton.module.MultipleImageModule
import com.app.bharatphoton.module.reviewResponse.AddReviewResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.ReviewViewModule
import com.bumptech.glide.Glide
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddReviewFragment : BottomSheetDialogFragment() {


    private var clickedView = 0
    private var imageList = MutableList(3) { MultipleImageModule() }
    private var productId = ""
    private val binding by lazy { FragmentAddReviewBinding.inflate(layoutInflater) }

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    private val viewModule by viewModels<ReviewViewModule>()

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap ?: return@registerForActivityResult
            handleImageUpload(bitmap)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            handleImageUpload(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setClick()
        onSelectedView()
        apiAddReviewResponse()

    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    private fun apiAddReviewRequest() {
        with(binding) {
            val request = JsonObject().apply {
                addProperty("productId", productId)
                addProperty("rating", ratingBar.rating)
                addProperty("review", reviewEt.text.toString().trim())
                add("image", JsonArray().apply {
                    imageList.forEach { image ->
                        if (image.imageUrl.isNotEmpty()) {
                            add(JsonObject().apply {
                                addProperty("url", image.imageUrl)
                            })
                        }
                    }
                })
            }
            viewModule.addReview(request)
        }
    }

    private fun apiAddReviewResponse() {
        viewModule.addReview.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.submitBtn.hideLoading()
                    try {
                        it.errorMessage.mlog()
                        val message = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(message)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> binding.submitBtn.showLoading()
                is NetworkResult.Success -> {
                    binding.submitBtn.hideLoading()
                    try {
                        addReviewResponse = Gson().fromJson(it.data, AddReviewResponse::class.java)
                        if (addReviewResponse?.code == 1) {
                            dismiss()
                        } else {
                            dismiss()
                            requireActivity().toast(addReviewResponse?.message.toString())
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

    private fun handleImageUpload(bitmap: Bitmap) {
        showProgress(clickedView)

        imageBitmapToFile(requireContext(), bitmap)?.let { file ->
            uploadToAws.uploadFile(file = file, onSuccess = { url ->
                updateImageList(url)
                updateUIWithImage(url)
            }, onError = {
                hideProgress(clickedView)
            }, onProgress = {
                // Handle progress update if needed
            })
        }
    }

    private fun hideProgress(index: Int) {
        when (index) {
            0 -> binding.progressBar1.visibility = View.GONE
            1 -> binding.progressBar2.visibility = View.GONE
            2 -> binding.progressBar3.visibility = View.GONE
            else -> {
                //
            }
        }
    }

    private fun initData() {
        with(binding) {
            productId = arguments?.getString("productId") ?: ""

        }
    }

    private fun onSelectedView() {
        binding.reviewImage.forEachIndexed { index, view ->
            view.setOnAnimatedClickListener {
                clickedView = index
                index.mlog()
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
        try {
            val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            galleryLauncher.launch(pickVisualMediaRequest)
        } catch (e: Exception) {
            e.message.mlog()
        }
    }

    private fun setClick() {
        with(binding) {
            listOf(frame1, frame2, frame3).forEach { frame ->
                frame.setOnAnimatedClickListener { showImageSourcePopup() }
            }
            submitBtn.setOnAnimatedClickListener {
                if (validation()) {
                    apiAddReviewRequest()
                }
            }
        }
    }

    private fun validation(): Boolean {
        with(binding) {
            if (ratingBar.rating == 0f) {
                requireActivity().toast(getString(R.string.please_select_rating))
                return false
            } else if (reviewEt.text.toString().trim().isEmpty()) {
                requireActivity().toast(getString(R.string.please_enter_review))
                return false
            }
        }
        return true
    }

    private fun showImageSourcePopup() {
        showPopup(
            requireContext(),
            message = getString(R.string.choose_you_source),
            positiveButtonText = getString(R.string.camera),
            negativeButtonText = getString(R.string.gallery),
            positiveAction = { openCamera() },
            negativeAction = { openGallery() })
    }

    private fun showProgress(index: Int) {
        when (index) {
            0 -> binding.progressBar1.visibility = View.VISIBLE
            1 -> binding.progressBar2.visibility = View.VISIBLE
            2 -> binding.progressBar3.visibility = View.VISIBLE
            else -> {
                //
            }
        }
    }

    private fun updateImageList(url: String) {
        imageList[clickedView] = MultipleImageModule(
            imageUrl = url, imageId = imageList[clickedView].imageId
        )
    }

    private fun updateUIWithImage(url: String) {
        when (clickedView) {
            0 -> {
                Glide.with(requireContext()).load(url).into(binding.imageView1)
                binding.progressBar1.visibility = View.GONE
                binding.imageView1.visibility = View.VISIBLE
            }

            1 -> {
                Glide.with(requireContext()).load(url).into(binding.imageView2)
                binding.progressBar2.visibility = View.GONE
                binding.imageView2.visibility = View.VISIBLE
            }

            2 -> {
                Glide.with(requireContext()).load(url).into(binding.imageView3)
                binding.progressBar3.visibility = View.GONE
                binding.imageView3.visibility = View.VISIBLE
            }
            else -> {
                //
            }
        }
    }

    companion object {
        private var addReviewResponse: AddReviewResponse? = null
    }

}