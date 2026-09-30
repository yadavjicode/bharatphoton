package app.bharatphoton.service.ui.fragments.product_installation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.ProductInstallationList
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentProductInstallationPendingBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.product_installation.ProductInstallationPendingAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_INSTALLATION_PENDING_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProductInstallationPendingFragment : Fragment() {
    private lateinit var binding : FragmentProductInstallationPendingBinding
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
        getPendingInstallationProductListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductInstallationPendingBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_INSTALLATION_PENDING_FRAGMENT,"ProductInstallationPendingFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvPendingInstallation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            getPendingInstallationProductListApi()
        }
        getPendingInstallationProductListApi()

        return binding.root
    }

    private fun getPendingInstallationProductListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.productInstallationListing(token,currentPage,limit,installationStatus,"")
        call.enqueue(object : Callback<ProductInstallationList> {
            override fun onResponse(call: Call<ProductInstallationList>, response: Response<ProductInstallationList>)
            {
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
                try {
                    if (response.isSuccessful){
                        val arrResponse = response.body()
                        val arrResponseList=arrResponse?.data
                        if (arrResponseList != null) {
                            if (arrResponseList.isEmpty()){
                                binding.tvNoData.isVisible = true
                            }else{
                                binding.tvNoData.isVisible = false
                            }
                        }
                        (binding.rvPendingInstallation.adapter as ProductInstallationPendingAdapter).clearAll()
                        (binding.rvPendingInstallation.adapter as ProductInstallationPendingAdapter).addAll(arrResponseList)
                        val totalCount=(arrResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getPendingInstallationProductListApi-Exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductInstallationList>, t: Throwable) {
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
                    CrashlyticsHelper.log("getPendingInstallationProductListApi-Failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvPendingInstallation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPendingInstallation.adapter = ProductInstallationPendingAdapter(requireContext(), mutableListOf())
    }

    private fun loadMoreData() {

        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.productInstallationListing(token,currentPage,limit,installationStatus,"")

        call.enqueue(object : Callback<ProductInstallationList> {
            override fun onResponse(call: Call<ProductInstallationList>, response: Response<ProductInstallationList>
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val arrResponse = response.body()
                        val arrResponseList=arrResponse?.data
                        (binding.rvPendingInstallation.adapter as? ProductInstallationPendingAdapter)?.addAll(arrResponseList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getPendingInstallationProductListApi loadMoreData-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductInstallationList>, t: Throwable
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e: Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getPendingInstallationProductListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }
}