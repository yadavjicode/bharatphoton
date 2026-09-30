package app.bharatphoton.service.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.bharatphoton.service.api_model_classes.NotificationResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.FragmentNotificationBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.ui.adapter.AllNotificationAdapter
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.CrashlyticsConstants.NOTIFICATION
import app.bharatphoton.service.utils.CrashlyticsConstants.SUPPORT
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class NotificationFragment : Fragment() {
    private lateinit var binding : FragmentNotificationBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token = ""
    private var isLoading = false
    private var currentPage = 1
    private var totalPage = 1
    private var limit = 10
    private var readStatus: String? = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentNotificationBinding.inflate(layoutInflater)

        CrashlyticsHelper.setCustomKey(NOTIFICATION,"NotificationFragment")

        /*binding.cbAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                isLoading = true
                binding.clLoader.visibility = View.VISIBLE
                readStatus = "1"
                binding.cbAll.isClickable = false
                binding.cbUnread.isChecked = false
                notificationListApi(readStatus)
            }
            else{
                binding.cbAll.isClickable = true
            }
        }
        binding.cbUnread.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                isLoading = true
                binding.clLoader.visibility = View.VISIBLE
                readStatus = "0"
                binding.cbUnread.isClickable = false
                binding.cbAll.isChecked = false
                notificationListApi(readStatus)
            }
            else{
                binding.cbUnread.isClickable = true
            }
        }*/

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN,"")

        (activity as BaseActivity).applySlideInAnimation(binding.clNotification)

        isLoading = true
        binding.shimmerLayout.apply {
            startShimmer()
            binding.swipeRefreshLayout.isVisible = false
            visibility = View.VISIBLE
        }

        setupRecyclerView()

        binding.rvAllNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            notificationListApi()
        }
        notificationListApi()
        
        return binding.root
    }

    private fun notificationListApi() {
        currentPage = 1
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.notificationList(token,currentPage,limit,readStatus)
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>)
            {
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.swipeRefreshLayout.isVisible = true
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
                        (binding.rvAllNotification.adapter as AllNotificationAdapter).clearAll()
                        (binding.rvAllNotification.adapter as AllNotificationAdapter).addAll(arrAstrologerList)
                        val totalCount=(arrAstrologerResponse?.count ?: 1)
                        totalPage = (Math.ceil(totalCount.toDouble() / limit)).toInt()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("notificationListApi-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                isLoading = false
                binding.shimmerLayout.apply {
                    stopShimmer()
                    binding.swipeRefreshLayout.isVisible = true
                    visibility = View.GONE
                }
                binding.swipeRefreshLayout.isRefreshing = false
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e: Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("notificationListApi-API call failed ${e.message}")
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.rvAllNotification.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvAllNotification.adapter = AllNotificationAdapter(requireContext(), mutableListOf())
    }

    private fun loadMoreData() {
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.notificationList(token,currentPage,limit,readStatus)
        call.enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    if (response.isSuccessful) {
                        val arrAstrologerResponse = response.body()
                        val arrAstrologerList=arrAstrologerResponse?.data
                        (binding.rvAllNotification.adapter as? AllNotificationAdapter)?.addAll(arrAstrologerList)
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(requireContext(), httpException)
                    }
                }catch (e: Exception){
                    (activity as BaseActivity).showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("notificationListApi loadMoreData-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<NotificationResponse>, t: Throwable
            ) {
                isLoading = false
                binding.pbLoaderBottom.visibility = View.GONE
                try {
                    ApiErrorHandler.handleException(requireContext(), t)
                }catch (e: Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("notificationListApi loadMoreData-API call failed ${e.message}")
                }
            }
        })
    }
}