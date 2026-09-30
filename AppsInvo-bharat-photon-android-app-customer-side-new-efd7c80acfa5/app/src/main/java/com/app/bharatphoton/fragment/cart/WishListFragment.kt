package com.app.bharatphoton.fragment.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.bharatphoton.R
import com.app.bharatphoton.adapter.LoaderAdapter
import com.app.bharatphoton.adapter.WishListAdapter
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.applyRecyclerAnimation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentWishListBinding
import com.app.bharatphoton.module.wishlistResponse.DeleteWishListResponse
import com.app.bharatphoton.retrofit.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class WishListFragment : BaseFragment<FragmentWishListBinding>() {
    private var isLoaded = true
    private var shouldScrollToTopAfterRefresh = false

    private val navOptions by lazy { Utils.navigateWithAnimation(requireContext()) }
    private val wishListAdapter by lazy {
        WishListAdapter { type, data ->
            when (type) {
                "like" -> {
                    apiDeleteWishListRequest(data.id ?: "")
                }

                "root" -> {
                    val bundle = Bundle().apply {
                        putString("productId", data.productId)
                    }
                    navigateToScreen(R.id.productDetailsFragment, bundle, navOptions = navOptions)
                }
            }
        }
    }

    companion object {
        private var deleteWishListResponse: DeleteWishListResponse? = null
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWishListBinding = FragmentWishListBinding.inflate(layoutInflater)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intiData()
        onClick()
        setDataInAdapter()
        apiWishListRequest()
        apiDeleteWishListResponse()
    }

    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, null, navOptions) }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
                call.setOnAnimatedClickListener { openDialer() }
            }
        }
    }

    private fun apiWishListRequest() {
        wishListViewModule.wishList().observe(viewLifecycleOwner) { list ->
            lifecycleScope.launch {
                wishListAdapter.submitData(list)
            }
        }
    }

    private fun apiDeleteWishListRequest(id: String) {
        wishListViewModule.deleteWishList(id)
    }

    private fun apiDeleteWishListResponse() {
        wishListViewModule.deleteWishList.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    try {
                        deleteWishListResponse = Gson().fromJson(it.data, DeleteWishListResponse::class.java)
                        if (deleteWishListResponse?.code == 1) {
                            val updatedList = wishListAdapter.snapshot().items.filter { item ->
                                item.id != deleteWishListResponse?.data?.id
                            }
                            lifecycleScope.launch {
                                wishListAdapter.submitData(PagingData.from(updatedList))
                            }
                        } else {
                            requireActivity().toast(deleteWishListResponse?.message.toString())
                        }
                    } catch (e: Exception) {
                        e.message.mlog()
                    }
                }

                is NetworkResult.Error -> {
                    hideLoader()
                    val errorBody = it.errorMessage
                    try {
                        val errorMessage = JSONObject(errorBody ?: "").optString("message")
                        requireActivity().toast(errorMessage)
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

    private fun setDataInAdapter() {
        with(binding) {
            wishListRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = wishListAdapter.withLoadStateFooter(LoaderAdapter())

                wishListAdapter.addLoadStateListener { loadState ->
                    when (loadState.refresh) {
                        is LoadState.Error -> {
                            hideLoader()
                        }

                        LoadState.Loading -> {
                            showLoader()
                        }

                        is LoadState.NotLoading -> {
                            hideLoader()
                            if (shouldScrollToTopAfterRefresh) {
                                binding.wishListRv.scrollToPosition(0)
                                shouldScrollToTopAfterRefresh = false
                            }
                            if (isLoaded) {
                                applyRecyclerAnimation(requireContext(), wishListRv)
                                isLoaded = false
                                noFound.visibility = View.GONE
                            }
                            if (wishListAdapter.snapshot().items.isEmpty()) {
                                noFound.visibility = View.VISIBLE
                                wishListRv.visibility = View.GONE
                                val animation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                                noFound.startAnimation(animation)
                            } else {
                                noFound.visibility = View.GONE
                                wishListRv.visibility = View.VISIBLE
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

    @SuppressLint("SetTextI18n")
    private fun intiData() {
        with(binding) {
            topBar.cartItemCount.text = SharedData.cartItemCount.toString()
            topBar.title.text = getString(R.string.wishlist)
            topBar.notificationCountTv.text = SharedData.notificationCount.toString()
            handleOnPressBackButton { navigateBack() }

            swipeRefreshLayout.setOnRefreshListener {
                shouldScrollToTopAfterRefresh = true
                apiWishListRequest()
                swipeRefreshLayout.isRefreshing = false

            }
        }
    }
}