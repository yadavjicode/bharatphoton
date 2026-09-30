package com.app.bharatphoton.fragment.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.LoaderAdapter
import com.app.bharatphoton.adapter.ReviewsAllAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.databinding.FragmentReviewBinding
import com.app.bharatphoton.fragment.product.ZoomPhotosDialogFragment
import com.app.bharatphoton.viewModule.ReviewViewModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewFragment : BaseFragment<FragmentReviewBinding>() {
    private val viewModule by viewModels<ReviewViewModule>()
    private val reviewAdapter by lazy {
        ReviewsAllAdapter { type, data ->
            when (type) {
                "image" -> {
                    val allImages = data?.map { it?.url } ?: emptyList()
                    val bundle = Bundle().apply {
                        putStringArrayList("productImages", ArrayList(allImages))
                    }
                    val dialog = ZoomPhotosDialogFragment().apply {
                        arguments = bundle
                    }
                    dialog.show(childFragmentManager, "ZoomPhotosDialogFragment")

                }
            }
        }
    }
    private var productId = ""

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentReviewBinding = FragmentReviewBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        setDataInAdapter()
        apiReviewListRequest()
    }

    private fun setDataInAdapter() {
        with(binding) {
            reviewRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = reviewAdapter.withLoadStateFooter(LoaderAdapter())

                reviewAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            hideLoader()
                        }

                        LoadState.Loading -> {
                            showLoader()
                        }

                        is LoadState.NotLoading -> {
                            hideLoader()

                        }
                        else -> {
                            //
                        }
                    }
                }
            }
        }
    }

    private fun apiReviewListRequest() {
        lifecycleScope.launch {
            viewModule.reviewLIst(productId = productId).collectLatest { list ->
                reviewAdapter.submitData(list)
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.back.setOnAnimatedClickListener { navigateBack() }
            topBar.call.setOnAnimatedClickListener {
                openDialer()
            }
        }
    }

    private fun initData() {
        with(binding) {
            topBar.topBarFunctionality.visibility = View.VISIBLE
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            handleOnPressBackButton { navigateBack() }
            topBar.title.text = getString(R.string.review)
            productId = arguments?.getString("productId").toString()
        }
    }
}