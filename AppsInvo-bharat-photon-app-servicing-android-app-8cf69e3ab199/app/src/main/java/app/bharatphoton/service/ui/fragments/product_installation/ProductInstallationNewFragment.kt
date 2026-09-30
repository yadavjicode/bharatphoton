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
import app.bharatphoton.service.databinding.FragmentProductInstallationNewBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.activities.HomeActivity
import app.bharatphoton.service.ui.adapter.product_installation.ProductInstallationNewAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.PRODUCT_INSTALLATION_NEW_FRAGMENT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ProductInstallationNewFragment : Fragment() {
    private lateinit var binding : FragmentProductInstallationNewBinding
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
        getNewInstallationProductListApi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentProductInstallationNewBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(PRODUCT_INSTALLATION_NEW_FRAGMENT,"ProductInstallationNewFragment")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        (requireActivity() as HomeActivity).profileDetails()

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvNewInstallation.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            getNewInstallationProductListApi()
        }
        getNewInstallationProductListApi()

        return binding.root
    }

    private fun getNewInstallationProductListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.productInstallationListing(token,currentPage,limit,installationStatus,"")
        call.enqueue(object : Callback<ProductInstallationList> {
            override fun onResponse(call: Call<ProductInstallationList>, response: Response<ProductInstallationList>)
            {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
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
                        (binding.rvNewInstallation.adapter as ProductInstallationNewAdapter).clearAll()
                        (binding.rvNewInstallation.adapter as ProductInstallationNewAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewInstallationProductListApi-exception ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProductInstallationList>, t: Throwable) {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    visibility = View.GONE
                }
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getNewInstallationProductListApi-Failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvNewInstallation.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNewInstallation.adapter = ProductInstallationNewAdapter(requireContext(), mutableListOf())
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
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList=arrAstrologerResponse?.data
                        (binding.rvNewInstallation.adapter as? ProductInstallationNewAdapter)?.addAll(arrAstrologerList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getNewInstallationProductListApi loadMoreData-exception ${e.message}")
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
                    CrashlyticsHelper.log("getNewInstallationProductListApi loadMoreData-Failed ${e.message}")
                }
            }
        })
    }

}