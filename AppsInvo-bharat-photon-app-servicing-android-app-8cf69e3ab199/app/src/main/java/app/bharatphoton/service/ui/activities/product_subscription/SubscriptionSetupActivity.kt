package app.bharatphoton.service.ui.activities.product_subscription

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import app.bharatphoton.service.api_model_classes.SinDetailsResponse
import app.bharatphoton.service.api_model_classes.SubscriptionDetailsResponseData
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivitySubscriptionSetupBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.BATTERY_VOLTAGE
import app.bharatphoton.service.utils.Constants.COST_SAVED
import app.bharatphoton.service.utils.Constants.CUSTOMER_NAME
import app.bharatphoton.service.utils.Constants.CUT_OFF_DELAY
import app.bharatphoton.service.utils.Constants.FIN_NO
import app.bharatphoton.service.utils.Constants.FLUSH_TIME
import app.bharatphoton.service.utils.Constants.FROM_ADAPTER
import app.bharatphoton.service.utils.Constants.INSTALLATION_IMAGE
import app.bharatphoton.service.utils.Constants.INSTALLATION_IMAGE_ID
import app.bharatphoton.service.utils.Constants.IS_FIELD_AVAILABLE
import app.bharatphoton.service.utils.Constants.IS_SPECIFIC_CATEGORY_FOR_FLUSH
import app.bharatphoton.service.utils.Constants.LOGIN_TOKEN
import app.bharatphoton.service.utils.Constants.OFF_DELAY
import app.bharatphoton.service.utils.Constants.ORDER_ID
import app.bharatphoton.service.utils.Constants.PRE_FLUSH
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.Constants.RANGE
import app.bharatphoton.service.utils.Constants.REMARK
import app.bharatphoton.service.utils.Constants.SIN_NO
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.SUBSCRIPTION_DETAILS
import app.bharatphoton.service.utils.Constants.SUBSCRIPTION_ID
import app.bharatphoton.service.utils.Constants.TOTAL_PAYMENT
import app.bharatphoton.service.utils.Constants.UPDATE_MONTH
import app.bharatphoton.service.utils.Constants.UPDATE_YEAR
import app.bharatphoton.service.utils.Constants.USER_COUNT
import app.bharatphoton.service.utils.Constants.WATER_SAVED
import app.bharatphoton.service.utils.CrashlyticsConstants.SUBSCRIPTION_SIN_DETAILS
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class SubscriptionSetupActivity : BaseActivity() {
    private lateinit var binding: ActivitySubscriptionSetupBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token: String? = null
    private val rangeSetting = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private val preFlush = listOf("0", "2", "3")
    private val flushTime = listOf("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15")
    private val offDelay = listOf("0", "2", "3", "4")
    private val cutOffDelay = listOf("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15")
    private val updateMonths = listOf("JAN", "FEB", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEPT", "OCT", "NOV", "DEC")
    private var orderIdGet: String? = null
    private var productIdGet: String? = null
    private var subscriptionIdGet: String? = null
    private var remark: String? = null
    private var customerName: String? = null
    private var installationImage: String? = null
    private var installationImageId: String? = null
    private var fromAdapter: String? = null
    private var sinUpdateId: String? = null
    private var productId: String? = null
    private var isFieldAvailable: String? = null
    private var isSpecificForFlush: String? = null
    private var finNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubscriptionSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(SUBSCRIPTION_SIN_DETAILS, "SubscriptionSetupActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")

        val subscriptionDetails = intent.getParcelableExtra<SubscriptionDetailsResponseData>(SUBSCRIPTION_DETAILS)
        fromAdapter = intent.getStringExtra(FROM_ADAPTER)
        productId = intent.getStringExtra(PRODUCT_ID)
        sinUpdateId = intent.getStringExtra(SIN_UPDATE_ID)
        isFieldAvailable = intent.getStringExtra(IS_FIELD_AVAILABLE)
        isSpecificForFlush = intent.getStringExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH)
        finNo = intent.getStringExtra(FIN_NO)

        when (fromAdapter) {
            "SubscriptionInstallationPendingAdapter" -> {
                sinDetailsApiCall()
            }

            "SubscriptionInstallationNewAdapter" -> {
                if (subscriptionDetails != null) {
                    orderIdGet = subscriptionDetails.subscriptionList?._id.toString()
                    subscriptionIdGet = subscriptionDetails.subscriptionList?.subscriptionId.toString()
                    val product = subscriptionDetails.subscriptionList
                    productIdGet = product?.productId.toString()
                    binding.tvModelName.text = product?.modelNo ?: "NA"
                    binding.tvFin.text = finNo ?: "NA"
                }
            }
        }

        binding.apply {

            if (isFieldAvailable == "0" && isSpecificForFlush == "0") {
                llShowSinDetails.isVisible = false
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = false
                llDelayLayout.isVisible = false
            } else if (isFieldAvailable == "0" && isSpecificForFlush == "1") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = true
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = false
            } else if (isFieldAvailable == "1" && isSpecificForFlush == "0") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = true
            } else if (isFieldAvailable == "1" && isSpecificForFlush == "1") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = true
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = true
            } else {
                llShowSinDetails.isVisible = false
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = false
                llDelayLayout.isVisible = false
            }

            applySlideInAnimation(clSubSetup)

            setEditTextFilterForAlphaNumericText(2, etSin1, etSin2, etSin3)

            setupEditTextAutoShift(etSin1, etSin2, 2, null)
            setupEditTextAutoShift(etSin2, etSin3, 2, etSin1)
            setupEditTextAutoShift(etSin3, null, 2, etSin2)

            tvRange.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvRange, rangeSetting)
            }

            tvFlushTime.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvFlushTime, flushTime)
            }

            tvPreFlush.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvPreFlush, preFlush)
            }

            tvOffDelay.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvOffDelay, offDelay)
            }

            tvCutOffDelay.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvCutOffDelay, cutOffDelay)
            }

            tvUpdateMonth.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showPopupMenuList(tvUpdateMonth, updateMonths)
            }

            ivBack.setOnClickWithAnimation {
                finish()
            }
        }

        binding.tvBtnUpdateSIN.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation

            val sinPart1 = binding.etSin1.text.toString()
            val sinPart2 = binding.etSin2.text.toString()
            val sinPart3 = binding.etSin3.text.toString()
            val sin = "$sinPart1-$sinPart2-$sinPart3"

            val userCount = binding.etUserCount.text.toString()
            val waterSaved = binding.etWaterSaved.text.toString()
            val costSaved = binding.etCostSaved.text.toString()
            val range = binding.tvRange.text.toString()
            val preFlush = binding.tvPreFlush.text.toString()
            val flushTime = binding.tvFlushTime.text.toString()
            val offDelay = binding.tvOffDelay.text.toString()
            val cutOffDelay = binding.tvCutOffDelay.text.toString()
            val updateMonth = binding.tvUpdateMonth.text.toString()
            val updateYear = binding.tvUpdateYear.text.toString()
            val batteryVoltage = binding.etBatteryVoltage.text.toString()
            val totalPayment = binding.etTotalPayment.text.toString()

            val bundle = Bundle().apply {
                putString(ORDER_ID, orderIdGet)
                putString(PRODUCT_ID, productIdGet)
                putString(SUBSCRIPTION_ID, subscriptionIdGet)
                putString(SIN_NO, sin)
                putString(FIN_NO, finNo)
                putString(USER_COUNT, userCount)
                putString(RANGE, range)
                putString(PRE_FLUSH, preFlush)
                putString(FLUSH_TIME, flushTime)
                putString(OFF_DELAY, offDelay)
                putString(CUT_OFF_DELAY, cutOffDelay)
                putString(UPDATE_MONTH, updateMonth)
                putString(UPDATE_YEAR, updateYear)
                putString(BATTERY_VOLTAGE, batteryVoltage)
                putString(TOTAL_PAYMENT, totalPayment)
                putString(REMARK, remark)
                putString(CUSTOMER_NAME, customerName)
                putString(INSTALLATION_IMAGE, installationImage)
                putString(INSTALLATION_IMAGE_ID, installationImageId)
                putString(SIN_UPDATE_ID, sinUpdateId)
                putString(WATER_SAVED, waterSaved)
                putString(COST_SAVED, costSaved)
                putString(IS_FIELD_AVAILABLE, isFieldAvailable)
                putString(IS_SPECIFIC_CATEGORY_FOR_FLUSH, isSpecificForFlush)
            }

            val requiredFields = mutableListOf(sinPart1, sinPart2, sinPart3, totalPayment, sin)

            if (isFieldAvailable == "0") {
                if (isSpecificForFlush == "1") {
                    requiredFields.addAll(listOf(userCount, waterSaved, costSaved, range, preFlush, flushTime, updateMonth, batteryVoltage))
                }
            } else {
                requiredFields.addAll(listOf(userCount, waterSaved, costSaved, range, offDelay, cutOffDelay, updateMonth, batteryVoltage))
                if (isSpecificForFlush == "1") {
                    requiredFields.addAll(listOf(preFlush, flushTime))
                }
            }

            if (requiredFields.any { it.isBlank() }) {
                showAnimatedCustomToast("Please fill in all required fields!")
            } else {
                val intent = Intent(this@SubscriptionSetupActivity, SubscriptionCustomerRemarkActivity::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent)
//                finish()
            }
        }

        val editTexts = listOf(
            binding.etSin1, binding.etSin2, binding.etSin3, binding.etUserCount, binding.etWaterSaved, binding.etCostSaved, binding.etBatteryVoltage, binding.etTotalPayment
        )
        setCursorToEndOnFocus(editTexts)
    }

    private fun sinDetailsApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.sinDetails(token.toString(), sinUpdateId)
        getDetail.enqueue(object : Callback<SinDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<SinDetailsResponse>, response: Response<SinDetailsResponse>) {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1) {
                        val responseData = response.body()?.data
                        if (responseData != null) {
                            remark = responseData.remark
                            customerName = responseData.customerName

                            binding.tvModelName.text = responseData.modelNo ?: "NA"
                            binding.tvFin.text = responseData.finNo ?: "NA"

                            orderIdGet = responseData.orderId
                            subscriptionIdGet = responseData.subscriptionId
                            productIdGet = responseData.productId
                            sinUpdateId = responseData._id

                            if (!responseData.image.isNullOrEmpty()) {
                                for (installationImg in responseData.image!!) {
                                    installationImage = installationImg?.url
                                    installationImageId = installationImg?._id
                                }
                            }
                            val sin = responseData.sin
                            val sinParts = sin?.split("-")
                            if (sinParts?.size == 3) {
                                binding.etSin1.setText(sinParts[0])
                                binding.etSin2.setText(sinParts[1])
                                binding.etSin3.setText(sinParts[2])
                            } else {
                                binding.etSin1.setText("")
                                binding.etSin2.setText("")
                                binding.etSin3.setText("")
                            }
                            binding.etUserCount.setText(responseData.userCount ?: "")
                            binding.etWaterSaved.setText(responseData.waterSaved ?: "")
                            binding.etCostSaved.setText(responseData.costSaved ?: "")
                            binding.tvRange.text = responseData.range ?: ""
                            binding.tvPreFlush.text = responseData.preflush ?: ""
                            binding.tvFlushTime.text = responseData.flushTime ?: ""
                            binding.tvOffDelay.text = responseData.offDelay ?: ""
                            binding.tvUpdateMonth.text = responseData.month ?: ""
                            binding.tvUpdateYear.setText(responseData.year.toString())
                            binding.tvCutOffDelay.text = responseData.cutOffDelay ?: ""
                            binding.etBatteryVoltage.setText(responseData.batteryVoltage ?: "")
                            binding.etTotalPayment.setText(responseData.totalPayment ?: "")
                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@SubscriptionSetupActivity, httpException)
                    }
                } catch (e: Exception) {
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("sinDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }

            override fun onFailure(call: Call<SinDetailsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@SubscriptionSetupActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("sinDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }
}