package app.bharatphoton.service.ui.fragments.product_complaint

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.ProductComplaintListingResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentProductComplaintNewBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.product_complaint.ComplaintDetailsActivity
import app.bharatphoton.service.ui.adapter.product_complaint.ProductComplaintNewAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COMPLAINT_STATUS
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_NEW_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProductComplaintNewFragment : Fragment(), ProductComplaintNewAdapter.OnViewButtonClick {
    private lateinit var binding: FragmentProductComplaintNewBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var isLoading = false
    private var currentPage = 1
    private var totalPage = 1
    private var limit = 10
    private var installationStatus = "0"

    override fun onResume() {
        super.onResume()
        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        getNewProductComplaintListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductComplaintNewBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_COMPLAINT_NEW_FRAGMENT,"ProductComplaintNewFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvNewComplaint.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isLoading && currentPage < totalPage) {
                        isLoading = true
                        currentPage++
                        binding.pbLoaderBottom.visibility = View.VISIBLE
                        loadMoreData()
                    }
                }
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            getNewProductComplaintListApi()
        }
        getNewProductComplaintListApi()

        return binding.root
    }


    private fun getNewProductComplaintListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.complaintList(token, currentPage, limit, installationStatus)
        call.enqueue(object : Callback<ProductComplaintListingResponse> {
            override fun onResponse(call: Call<ProductComplaintListingResponse>, response: Response<ProductComplaintListingResponse>)
            {
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
                try {
                    if (response.isSuccessful) {
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList = arrAstrologerResponse?.data
                        if (arrAstrologerList != null) {
                            if (arrAstrologerList.isEmpty()) {
                                binding.tvNoData.isVisible = true
                            } else {
                                binding.tvNoData.isVisible = false
                            }
                        }
                        (binding.rvNewComplaint.adapter as ProductComplaintNewAdapter).clearAll()
                        (binding.rvNewComplaint.adapter as ProductComplaintNewAdapter).addAll(arrAstrologerList)
                        val totalCount = (arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewProductComplaintListApi-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductComplaintListingResponse>, t: Throwable) {
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getNewProductComplaintListApi-failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvNewComplaint.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNewComplaint.adapter = ProductComplaintNewAdapter(requireContext(), mutableListOf(), this)
    }

    private fun loadMoreData() {

        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.complaintList(token, currentPage, limit, installationStatus)

        call.enqueue(object : Callback<ProductComplaintListingResponse> {
            override fun onResponse(
                call: Call<ProductComplaintListingResponse>, response: Response<ProductComplaintListingResponse>
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList = arrAstrologerResponse?.data
                        (binding.rvNewComplaint.adapter as? ProductComplaintNewAdapter)?.addAll(arrAstrologerList)
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewProductComplaintListApi loadMoreData-exception ${e.message}")
                }
            }

            override fun onFailure(call: Call<ProductComplaintListingResponse>, t: Throwable)
            {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getNewProductComplaintListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }

    override fun onViewButtonClick(complaintId: String, view: View) {
        val intent = Intent(requireContext(), ComplaintDetailsActivity::class.java).apply {
            putExtra(COMPLAINT_ID, complaintId)
            putExtra(COMPLAINT_STATUS, "0")
            putExtra(FROM_ADAPTER, "ProductComplaintNewAdapter")
        }
        startActivity(intent)
    }
}