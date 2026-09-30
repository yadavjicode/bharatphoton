package com.app.bharatphoton.fragment.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.SitePhotoSubmittedAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentSitePhotoSubmittedBinding
import com.app.bharatphoton.fragment.product.ZoomPhotosDialogFragment

class SitePhotoSubmittedFragment : BaseFragment<FragmentSitePhotoSubmittedBinding>() {

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }
    private var imageList = ArrayList<String>()

    private val sitePhotoSubmittedAdapter by lazy {
        SitePhotoSubmittedAdapter { type ->
            when (type) {
                "Image" -> {
                    val bundle = Bundle().apply {
                        putStringArrayList("productImages", imageList)
                    }
                    val dialog = ZoomPhotosDialogFragment().apply {
                        arguments = bundle
                    }
                    dialog.show(childFragmentManager, "ZoomPhotosDialogFragment")
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
    }

    private fun setDataInAdapter() {
        with(binding) {
            imageRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                adapter = sitePhotoSubmittedAdapter
            }
        }
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentSitePhotoSubmittedBinding = FragmentSitePhotoSubmittedBinding.inflate(layoutInflater)

    private fun initData() {
        with(binding) {
            topBar.apply {
                notificationCountTv.text = SharedData.notificationCount.toString()
                topBarFunctionality.visibility = View.VISIBLE
                cart.visibility = View.GONE
                back.visibility = View.GONE
                notification.visibility = View.GONE
                title.visibility = View.GONE
            }
            animation(requireContext(), root)
            handleOnPressBackButton { navigateBack() }
            imageList = arguments?.getStringArrayList("imageList")!!
            sitePhotoSubmittedAdapter.submitList(imageList)
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
            backToHomeBtn.setOnAnimatedClickListener {
                navigateToScreen(id = R.id.homeFragment, bundle = null, navOptions = navOptions)
            }
            topBar.notification.setOnAnimatedClickListener {
                navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions)
            }
            topBar.cart.setOnAnimatedClickListener {
                navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions)
            }
        }
    }
}