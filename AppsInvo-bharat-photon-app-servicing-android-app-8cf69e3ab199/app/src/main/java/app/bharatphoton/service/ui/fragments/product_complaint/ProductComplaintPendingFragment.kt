package app.bharatphoton.service.ui.fragments.product_complaint

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
import app.bharatphoton.service.databinding.FragmentProductComplaintPendingBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.product_complaint.ProductComplaintPendingAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_COMPLETED_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_PENDING_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProductComplaintPendingFragment : Fragment() {
    private lateinit var binding: FragmentProductComplaintPendingBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var isLoading = false
    private var currentPage = 1
    private var totalPage = 1
    private var limit = 10
    private var installationStatus = "1"

    override fun onResume() {
        super.onResume()
        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        getPendingProductComplaintListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductComplaintPendingBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_COMPLAINT_PENDING_FRAGMENT,"ProductComplaintPendingFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvPendingComplaint.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            getPendingProductComplaintListApi()
        }
        getPendingProductComplaintListApi()

        return binding.root
    }


    private fun getPendingProductComplaintListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.complaintList(token,currentPage,limit,installationStatus)
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
                    if (response.isSuccessful){
                        val arrAstrologerResponse=response.body()
                        val arrAstrologerList=arrAstrologerResponse?.data
                        if (arrAstrologerList != null) {
                            if (arrAstrologerList.isEmpty()){
                                binding.tvNoData.isVisible = true
                            }else{
                                binding.tvNoData.isVisible = false
                            }
                        }
                        (binding.rvPendingComplaint.adapter as ProductComplaintPendingAdapter).clearAll()
                        (binding.rvPendingComplaint.adapter as ProductComplaintPendingAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getPendingProductComplaintListApi-exception ${e.message}")
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
                    CrashlyticsHelper.log("getPendingProductComplaintListApi-Failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvPendingComplaint.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPendingComplaint.adapter = ProductComplaintPendingAdapter(requireContext(), mutableListOf())
    }

    private fun loadMoreData() {

        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.complaintList(token,currentPage,limit,installationStatus)

        call.enqueue(object : Callback<ProductComplaintListingResponse> {
            override fun onResponse(call: Call<ProductComplaintListingResponse>, response: Response<ProductComplaintListingResponse>
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList=arrAstrologerResponse?.data
                        (binding.rvPendingComplaint.adapter as? ProductComplaintPendingAdapter)?.addAll(arrAstrologerList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getPendingProductComplaintListApi loadMoreData-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductComplaintListingResponse>, t: Throwable
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getPendingProductComplaintListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }
}