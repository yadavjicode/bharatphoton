package com.app.bharatphoton.fragment.order

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.AddMoreSitePhotosAdapter
import com.app.bharatphoton.awsUtils.UploadToAws
import com.app.bharatphoton.awsUtils.UploadToAws.Companion.imageBitmapToFile
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.openSettingPopup
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.Utils.showPopup
import com.app.bharatphoton.common.Utils.zoomInOutAnimation
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentUploadPhotoBinding
import com.app.bharatphoton.module.addressResponse.AddInstallationAddressResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.AddressViewModule
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.extension.send
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UploadPhotoFragment : BaseFragment<FragmentUploadPhotoBinding>() {
    private var orderId = ""
    private var id = ""
    private var orderNo = ""
    private val viewModel by viewModels<AddressViewModule>()

    private val addMoreSitePhotosAdapter: AddMoreSitePhotosAdapter by lazy {
        AddMoreSitePhotosAdapter { type, position ->
            when (type) {
                "Delete" -> {
                    if (position in 0 until sitePhotoImageList.size) {
                        sitePhotoImageList.removeAt(position)
                        addMoreSitePhotosAdapter.submitList(sitePhotoImageList.toList())
                        addMoreSitePhotosAdapter.notifyItemRemoved(position)
                        addMoreSitePhotosAdapter.notifyItemRangeChanged(position, sitePhotoImageList.size)

                        if (sitePhotoImageList.isEmpty()) {
                            binding.uploadPhotoLayout.visibility = View.VISIBLE
                            binding.sitePhotosRv.visibility = View.GONE
                        }
                    }
                }

            }
        }
    }

    private var sitePhotoImageList = mutableListOf<String>()

    private val uploadToAws by lazy {
        UploadToAws(
            awsAccessKey = SharedData.accessKey, awsBucketName = SharedData.bucketName, awsSecretKey = SharedData.secretKey, context = requireContext(), region = SharedData.region
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val imageBitmap = it.data?.extras?.get("data")
            val image = imageBitmapToFile(context = requireContext(), bitmap = imageBitmap as Bitmap)
            if (image != null) {
                showLoader()
                uploadToAws.uploadFile(file = image, onSuccess = { url ->
                    hideLoader()
                    binding.uploadPhotoLayout.visibility = View.GONE
                    binding.sitePhotosRv.visibility = View.VISIBLE
                    sitePhotoImageList.addAll(listOf(url))
                    addMoreSitePhotosAdapter.submitList(sitePhotoImageList)
                    addMoreSitePhotosAdapter.notifyDataSetChanged()
                }, onError = {
                    hideLoader()
                }, onProgress = {

                })
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            showLoader()
            val file = imageBitmapToFile(context = requireContext(), bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri))
            if (file != null) {
                uploadToAws.uploadFile(file = file, onSuccess = { url ->
                    hideLoader()
                    binding.uploadPhotoLayout.visibility = View.GONE
                    binding.sitePhotosRv.visibility = View.VISIBLE
                    sitePhotoImageList.addAll(listOf(url))
                    addMoreSitePhotosAdapter.submitList(sitePhotoImageList)
                    addMoreSitePhotosAdapter.notifyDataSetChanged()
                }, onError = {
                    hideLoader()
                }, onProgress = {

                })
            }
        }
    }

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiAddInstallationAddressResponse()
    }

    private fun setDataInAdapter() {
        with(binding) {
            sitePhotosRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = addMoreSitePhotosAdapter
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentUploadPhotoBinding = FragmentUploadPhotoBinding.inflate(layoutInflater)

    @SuppressLint("SetTextI18n")
    private fun initData() {
        with(binding) {
            setupTopBar()
            fetchArguments()
            orderNoTv.text = "Order No. : $orderNo"
            zoomInOutAnimation(listOf(thumbImg))
            animation(requireContext(), root)
            updateSitePhotosUI()
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun setupTopBar() {
        with(binding.topBar) {
            topBarFunctionality.visibility = View.VISIBLE
            cart.visibility = View.GONE
            notification.visibility = View.GONE
            title.visibility = View.GONE
            back.visibility = View.GONE
            notificationCountTv.text = SharedData.notificationCount.toString()
        }
    }

    private fun fetchArguments() {
        arguments?.let {
            orderId = it.getString("orderId") ?: ""
            id = it.getString("installationId") ?: ""
            orderNo = it.getString("orderNo") ?: ""
        }
    }

    private fun updateSitePhotosUI() {
        with(binding) {
            val isSitePhotosEmpty = sitePhotoImageList.isEmpty()
            uploadPhotoLayout.visibility = if (isSitePhotosEmpty) View.VISIBLE else View.GONE
            sitePhotosRv.visibility = if (isSitePhotosEmpty) View.GONE else View.VISIBLE
            addMoreTv.text = if (isSitePhotosEmpty) getString(R.string.add_site_photos) else getString(R.string.add_more)
        }
    }

    private fun apiAddInstallationAddressRequest() {
        with(binding) {
            val request = JsonObject().apply {
                if (id.isNotEmpty()) {
                    addProperty("id", id)
                }
                if (orderId.isNotEmpty()) {
                    addProperty("orderId", orderId)
                }
                add("image", JsonArray().apply {
                    sitePhotoImageList.forEach { url ->
                        add(JsonObject().apply {
                            addProperty("url", url)
                        })
                    }
                })
            }
            viewModel.addInstallationAddress(request)
        }
    }

    private fun apiAddInstallationAddressResponse() {
        viewModel.addInstallationAddress.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Error -> {
                    binding.submitBtn.hideLoading()
                    try {
                        val errorMessage = JSONObject(it.errorMessage ?: "").optString("message")
                        requireActivity().toast(errorMessage)
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Loading -> {
                    binding.submitBtn.showLoading()
                }

                is NetworkResult.Success -> {
                    binding.submitBtn.hideLoading()
                    try {
                        addInstallationAddress = Gson().fromJson(it.data, AddInstallationAddressResponse::class.java)
                        if (addInstallationAddress?.code == 1) {
                            val bundle = Bundle().apply {
                                putStringArrayList("imageList", ArrayList(sitePhotoImageList))
                            }
                            navigateToScreen(
                                id = R.id.sitePhotoSubmittedFragment, bundle = bundle, navOptions = navOptions
                            )
                        } else {
                            requireActivity().toast(addInstallationAddress?.message ?: "")
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

    override fun onDestroyView() {
        super.onDestroyView()
        sitePhotoImageList.clear()
        viewModel.addInstallationAddress.postValue(null)
        viewModel.updateDeliveryAddress.postValue(null)
        viewModel.addAddress.postValue(null)
        viewModel.addressList.postValue(null)
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener {
                navigateBack()
            }
            uploadLaterBtn.setOnAnimatedClickListener {
                navigateToScreen(R.id.homeFragment, bundle = null, navOptions = navOptions)
            }
            upload.setOnAnimatedClickListener {
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
            addMoreTv.setOnClickListener {
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
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            submitBtn.setOnAnimatedClickListener {
                if (sitePhotoImageList.isEmpty()) {
                    requireActivity().toast(getString(R.string.please_add_site_photos))
                    return@setOnAnimatedClickListener
                } else {
                    apiAddInstallationAddressRequest()
                }
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

    private fun openGallery() {
        val pickVisualMediaRequest = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        galleryLauncher.launch(pickVisualMediaRequest)
    }


    companion object {
        private var addInstallationAddress: AddInstallationAddressResponse? = null
    }

}