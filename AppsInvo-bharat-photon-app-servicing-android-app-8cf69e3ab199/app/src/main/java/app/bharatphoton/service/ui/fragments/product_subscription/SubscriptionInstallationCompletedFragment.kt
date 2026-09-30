package app.bharatphoton.service.ui.fragments.product_subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.SubscriptionInstallationListResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentSubscriptionInstallationCompletedBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.HomeActivity
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionInstallationCompletedAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_SUBSCRIPTION_COMPLETED_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class SubscriptionInstallationCompletedFragment : Fragment() {
    private lateinit var binding : FragmentSubscriptionInstallationCompletedBinding
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
        getNewSubscriptionInstallationListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSubscriptionInstallationCompletedBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_SUBSCRIPTION_COMPLETED_FRAGMENT,"SubscriptionInstallationCompletedFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        (requireActivity() as HomeActivity).profileDetails()

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvCompletedSubscriptionInstallation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            getNewSubscriptionInstallationListApi()
        }
        getNewSubscriptionInstallationListApi()
        
        return binding.root
    }

    private fun getNewSubscriptionInstallationListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.subInstallationList(token,currentPage,limit,installationStatus,"")
        call.enqueue(object : Callback<SubscriptionInstallationListResponse> {
            override fun onResponse(call: Call<SubscriptionInstallationListResponse>, response: Response<SubscriptionInstallationListResponse>)
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
                        (binding.rvCompletedSubscriptionInstallation.adapter as SubscriptionInstallationCompletedAdapter).clearAll()
                        (binding.rvCompletedSubscriptionInstallation.adapter as SubscriptionInstallationCompletedAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                }
            }
            override fun onFailure(call: Call<SubscriptionInstallationListResponse>, t: Throwable) {
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e:Exception){}
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvCompletedSubscriptionInstallation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvCompletedSubscriptionInstallation.adapter = SubscriptionInstallationCompletedAdapter(requireContext(), mutableListOf())
    }

    private fun loadMoreData() {

        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.subInstallationList(token,currentPage,limit,installationStatus,"")

        call.enqueue(object : Callback<SubscriptionInstallationListResponse> {
            override fun onResponse(call: Call<SubscriptionInstallationListResponse>, response: Response<SubscriptionInstallationListResponse>
            ) {
                if (response.isSuccessful) {
                    val arrAstrologerResponse = response.body()
                    val arrAstrologerList=arrAstrologerResponse?.data
                    (binding.rvCompletedSubscriptionInstallation.adapter as? SubscriptionInstallationCompletedAdapter)?.addAll(arrAstrologerList)
                }
                else{
                    val httpException = HttpException(response)
                    ApiErrorHandler.handleException(requireContext(), httpException)
                }
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
            }
            override fun onFailure(call: Call<SubscriptionInstallationListResponse>, t: Throwable
            ) {
                ApiErrorHandler.handleException(requireContext(), t)
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
            }
        })
    }
}