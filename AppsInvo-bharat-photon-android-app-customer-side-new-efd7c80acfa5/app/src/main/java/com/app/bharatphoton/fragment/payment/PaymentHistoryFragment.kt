package com.app.bharatphoton.fragment.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.app.bharatphoton.R
import com.app.bharatphoton.baseUtils.BaseFragment
import com.app.bharatphoton.common.NotificationManage.notificationCount
import com.app.bharatphoton.common.RecyclerAnimationHelper.Companion.animation
import com.app.bharatphoton.common.SharedData
import com.app.bharatphoton.common.Utils.downloadFile
import com.app.bharatphoton.common.Utils.formatPrice
import com.app.bharatphoton.common.Utils.navigateWithAnimation
import com.app.bharatphoton.common.Utils.setOnAnimatedClickListener
import com.app.bharatphoton.common.mlog
import com.app.bharatphoton.common.toast
import com.app.bharatphoton.databinding.FragmentPaymentHistoryBinding
import com.app.bharatphoton.databinding.LayoutAccountHistoryItemBinding
import com.app.bharatphoton.module.paymentHistory.PaymentHistoryResponse.PaymentHistoryData
import com.app.bharatphoton.retrofit.NetworkResult
import com.app.bharatphoton.viewModule.PaymentHistoryViewModule
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class PaymentHistoryFragment : BaseFragment<FragmentPaymentHistoryBinding>() {

    private var isLoading = false

    private val navOptions by lazy {
        navigateWithAnimation(requireContext())
    }

    private val viewModule by viewModels<PaymentHistoryViewModule>()

    override fun onResume() {
        super.onResume()
        setupScrollListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        onClick()
        observePaymentHistory()
        apiPaymentHistoryRequest()
        setupScrollListener()
    }

    override fun initBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): FragmentPaymentHistoryBinding = FragmentPaymentHistoryBinding.inflate(layoutInflater)

    fun formatReadableDate(dateString: String): String {
        val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        originalFormat.timeZone = TimeZone.getTimeZone("UTC")

        val desiredFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date: Date = originalFormat.parse(dateString) ?: return ""

        return desiredFormat.format(date)
    }

    private fun addRowIntoTable(data: List<PaymentHistoryData?>?) {
        data?.forEachIndexed { index, module ->
            val rowBinding = LayoutAccountHistoryItemBinding.inflate(layoutInflater, binding.tableLayout, false)
            if (index % 2 == 0) {
                rowBinding.root.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.Light_SkyBlue)
                )
            }

            rowBinding.apply {
                orderIdTv.text = module?.uuid
                logTv.text = module?.log?.filter { it.isLetterOrDigit() }?.take(8)
                amtTv.text = formatPrice(price = module?.finalTotalAmount ?: 0.0, currencyType = "INR")
                dateTv.text = formatReadableDate(module?.createdAt ?: "")
                statusTv.text = module?.paymentStatus

                if (module?.invoiceUrl != null) {
                    Column6.visibility = View.VISIBLE
                    notTv.visibility = View.GONE

                    Column6.setOnClickListener {
                        val isSubscription = module.log == "subscription"
                        val isPurchase = module.log == "purchase"
                        val isPending = module.paymentStatus == "pending"
                        val isCompleted = module.paymentStatus == "completed"

                        val (url, fileName) = when {
                            isSubscription && isPending -> {
                                val url = module.receiptUrl
                                url to url?.substringAfterLast("/")
                            }

                            isSubscription && isCompleted -> {
                                val url = module.invoiceUrl
                                url to url.substringAfterLast("/")
                            }

                            isPurchase && isPending -> {
                                val url = module.receiptUrl ?: ""
                                url to url.substringAfterLast("/")
                            }

                            isPurchase && isCompleted -> {
                                val url = module.invoiceUrl ?: ""
                                url to url.substringAfterLast("/")
                            }

                            else -> {
                                null to null
                            }
                        }

                        if (!url.isNullOrBlank() && !fileName.isNullOrBlank()) {
                            downloadFile(context = requireContext(), url = url, fileName = fileName)
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.no_file_available_to_download), Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    notTv.visibility = View.VISIBLE
                    Column6.visibility = View.GONE
                }
            }
            binding.tableLayout.addView(rowBinding.root)
        }
    }

    private fun apiPaymentHistoryRequest(loadNextPage: Boolean = false) {
        viewModule.paymentHistoryRequest(loadNextPage)
    }

    private fun hideFooterLoader() {
        binding.tableLayout.findViewWithTag<View>("FOOTER_LOADER")?.let {
            binding.tableLayout.removeView(it)
        }
    }

    private fun initData() {
        with(binding) {
            topBar.apply {
                cartItemCount.text = SharedData.cartItemCount.toString()
                notificationCountTv.text = SharedData.notificationCount.toString()
                topBarFunctionality.visibility = View.VISIBLE
                title.text = getString(R.string.payment_history)
            }
            notificationCount.observe(viewLifecycleOwner) { count ->
                apiNotificationCountRequest()
                apiNotificationCountResponse {
                    topBar.notificationCountTv.text = it.count.toString()
                }
            }
            animation(context = requireContext(), views = root)
            handleOnPressBackButton { navigateBack() }
        }
    }

    private fun observePaymentHistory() {
        viewModule.paymentHistory.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    showLoader()
                }

                is NetworkResult.Error -> {
                    hideLoader()
                    hideFooterLoader()
                    requireActivity().toast(result.errorMessage ?: "Error fetching data")
                }

                is NetworkResult.Success -> {
                    hideLoader()
                    hideFooterLoader()
                    val list = result.data?.data?.filterNotNull()
                    list?.size.mlog()
                    if (!list.isNullOrEmpty()) {
                        addRowIntoTable(list)
                    } else {
                        "list is empty".mlog()
                    }
                }
                else -> {
                    //
                }
            }
        }
    }

    private fun onClick() {
        with(binding) {
            topBar.apply {
                back.setOnAnimatedClickListener { navigateBack() }
                notification.setOnAnimatedClickListener { navigateToScreen(R.id.notificationFragment, bundle = null, navOptions = navOptions) }
                call.setOnAnimatedClickListener { openDialer() }
                cart.setOnAnimatedClickListener { navigateToScreen(R.id.cartFragment, bundle = null, navOptions = navOptions) }
            }
        }
    }

    private fun setupScrollListener() {
        binding.scrollLayout.viewTreeObserver.addOnScrollChangedListener {
            val scr = binding.scrollLayout
            val view = scr.getChildAt(scr.childCount - 1)
            val diff = view.bottom - (scr.height + scr.scrollY)
            if (diff <= 50 && !isLoading && !viewModule.isLastPage) {
                isLoading = true
                showFooterLoader()
                viewModule.paymentHistoryRequest(loadNextPage = true)
            }
        }
    }

    private fun showFooterLoader() {
        val loaderView = LayoutInflater.from(requireContext()).inflate(R.layout._layout_loader, binding.tableLayout, false)
        loaderView.tag = "FOOTER_LOADER"
        binding.tableLayout.addView(loaderView)
    }

}