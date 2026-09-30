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
import app.bharatphoton.service.databinding.FragmentSubscriptionInstallationNewBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.HomeActivity
import app.bharatphoton.service.ui.adapter.product_subscritption.SubscriptionInstallationNewAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_SUBSCRIPTION_NEW_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class SubscriptionInstallationNewFragment : Fragment() {
    private lateinit var binding : FragmentSubscriptionInstallationNewBinding
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
        getNewSubscriptionInstallationListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentSubscriptionInstallationNewBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_SUBSCRIPTION_NEW_FRAGMENT,"SubscriptionInstallationNewFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        (requireActivity() as HomeActivity).profileDetails()

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvNewSubscriptionInstallation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        (binding.rvNewSubscriptionInstallation.adapter as SubscriptionInstallationNewAdapter).clearAll()
                        (binding.rvNewSubscriptionInstallation.adapter as SubscriptionInstallationNewAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewSubscriptionInstallationListApi-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<SubscriptionInstallationListResponse>, t: Throwable) {
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
                    CrashlyticsHelper.log("getNewSubscriptionInstallationListApi-Failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvNewSubscriptionInstallation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNewSubscriptionInstallation.adapter = SubscriptionInstallationNewAdapter(requireContext(), mutableListOf())
    }

    private fun loadMoreData() {

        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.subInstallationList(token,currentPage,limit,installationStatus,"")

        call.enqueue(object : Callback<SubscriptionInstallationListResponse> {
            override fun onResponse(call: Call<SubscriptionInstallationListResponse>, response: Response<SubscriptionInstallationListResponse>
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList=arrAstrologerResponse?.data
                        (binding.rvNewSubscriptionInstallation.adapter as? SubscriptionInstallationNewAdapter)?.addAll(arrAstrologerList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewSubscriptionInstallationListApi loadMoreData-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<SubscriptionInstallationListResponse>, t: Throwable
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e: Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getNewSubscriptionInstallationListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }
}