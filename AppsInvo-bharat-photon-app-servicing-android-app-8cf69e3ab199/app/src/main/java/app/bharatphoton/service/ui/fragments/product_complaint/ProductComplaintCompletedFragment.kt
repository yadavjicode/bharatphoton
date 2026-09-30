package app.bharatphoton.service.ui.fragments.product_complaint

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.ProductComplaintListingResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentProductComplaintCompletedBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.LoginActivity
import app.bharatphoton.service.ui.adapter.product_complaint.ProductComplaintCompletedAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_COMPLETED_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_COMPLAINT_NEW_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.math.ceil

class ProductComplaintCompletedFragment : Fragment() {
    private lateinit var binding : FragmentProductComplaintCompletedBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var isLoading = false
    private var currentPage = 1
    private var totalPage = 1
    private var limit = 10
    private var installationStatus = "2"

    override fun onResume() {
        super.onResume()
        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }
        getCompletedProductComplaintListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductComplaintCompletedBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_COMPLAINT_COMPLETED_FRAGMENT,"ProductComplaintCompletedFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvCompletedComplaint.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            getCompletedProductComplaintListApi()
        }
        getCompletedProductComplaintListApi()

        return binding.root
    }

    private fun getCompletedProductComplaintListApi() {
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
                        (binding.rvCompletedComplaint.adapter as ProductComplaintCompletedAdapter).clearAll()
                        (binding.rvCompletedComplaint.adapter as ProductComplaintCompletedAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getCompletedProductComplaintListApi-Exception ${e.message}")
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
                    CrashlyticsHelper.log("getCompletedProductComplaintListApi-Failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvCompletedComplaint.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCompletedComplaint.adapter = ProductComplaintCompletedAdapter(requireContext(), mutableListOf())
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
                        (binding.rvCompletedComplaint.adapter as? ProductComplaintCompletedAdapter)?.addAll(arrAstrologerList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getCompletedProductComplaintListApi loadMoreData-Exception ${e.message}")
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
                    CrashlyticsHelper.log("getCompletedProductComplaintListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }
}