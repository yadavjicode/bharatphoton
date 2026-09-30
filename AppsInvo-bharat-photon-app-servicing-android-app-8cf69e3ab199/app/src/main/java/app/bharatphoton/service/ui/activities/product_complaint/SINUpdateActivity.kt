package app.bharatphoton.service.ui.activities.product_complaint

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isVisible
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.ComplaintDetailsResponseData
import app.bharatphoton.service.api_model_classes.ComplaintSinDetailsResponse
import app.bharatphoton.service.api_model_classes.PendingSinDetailsResponse
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.ActivitySinupdateBinding
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.ClickDebounceUtil
import app.bharatphoton.service.utils.Constants.BATTERY_VOLTAGE
import app.bharatphoton.service.utils.Constants.COMPLAINT_DETAILS
import app.bharatphoton.service.utils.Constants.COMPLAINT_ID
import app.bharatphoton.service.utils.Constants.COST_SAVED
import app.bharatphoton.service.utils.Constants.CUSTOMER_ID
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
import app.bharatphoton.service.utils.Constants.PRE_FLUSH
import app.bharatphoton.service.utils.Constants.PRODUCT_ID
import app.bharatphoton.service.utils.Constants.RANGE
import app.bharatphoton.service.utils.Constants.REMARK
import app.bharatphoton.service.utils.Constants.RESOLVE_ACTION
import app.bharatphoton.service.utils.Constants.RESOLVE_DATE
import app.bharatphoton.service.utils.Constants.SIN_NO
import app.bharatphoton.service.utils.Constants.SIN_UPDATE_ID
import app.bharatphoton.service.utils.Constants.TOTAL_PAYMENT
import app.bharatphoton.service.utils.Constants.USER_COUNT
import app.bharatphoton.service.utils.Constants.WATER_SAVED
import app.bharatphoton.service.utils.CrashlyticsConstants.COMPLAINT_SIN_DETAILS
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import app.bharatphoton.service.utils.setOnClickWithAnimation
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class SINUpdateActivity : BaseActivity() {
    private lateinit var binding: ActivitySinupdateBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var token: String? = null
    private var remark: String? = null
    private var customerName: String? = null
    private var categoryName = ""
    private var updateId: String? = null
    private var installationImage: String? = null
    private var installationImageId: String? = null
    private var complaintId: String? = null
    private var customerId: String? = null
    private var productId: String? = null
    private var fromAdapter: String? = null
    private var isFieldAvailable: String? = null
    private var isSpecificForFlush: String? = null
    private var resolveDate: String? = null
    private val rangeSetting = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    private val preFlush = listOf("0", "2", "3")
    private val flushTime = listOf("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15")
    private val offDelay = listOf("0", "2", "3", "4")
    private val cutOffDelay = listOf("5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15")

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySinupdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CrashlyticsHelper.setCustomKey(COMPLAINT_SIN_DETAILS, "SINUpdateActivity")

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
        token = sharedPreferencesHelper.getString(LOGIN_TOKEN, "")
        fromAdapter = intent.getStringExtra(FROM_ADAPTER)
        isFieldAvailable = intent.getStringExtra(IS_FIELD_AVAILABLE)
        isSpecificForFlush = intent.getStringExtra(IS_SPECIFIC_CATEGORY_FOR_FLUSH)
        categoryName = intent.getStringExtra("categoryName") ?: ""


        val complaintDetails = intent.getParcelableExtra<ComplaintDetailsResponseData>(COMPLAINT_DETAILS)
        "complaintDetails -> $complaintDetails".mLog()
        if (complaintDetails != null) {
            complaintId = complaintDetails.complaintDetails?._id ?: ""
            customerId = complaintDetails.complaintDetails?.userId ?: ""
            productId = complaintDetails.complaintDetails?.productId ?: ""

            when (fromAdapter) {
                "ProductComplaintNewAdapter" -> {
                    complaintSinDetailsApiCall()
                }

                "ProductComplaintPendingAdapter" -> {
                    complaintPendingSinDetailsApiCall()
                }
            }

            if (!complaintDetails.complaintDetails?.productImage.isNullOrEmpty()) {
                Glide.with(this).load(complaintDetails.complaintDetails?.productImage!![0]?.url).placeholder(R.drawable.default_gray_image_img).error(R.drawable.default_gray_image_img).into(binding.ivProductImg)
            }
            binding.tvProductName.text = complaintDetails.complaintDetails?.productName.toString()
            binding.tvProductCategory.text = complaintDetails.complaintDetails?.categoryName.toString()
        }

        binding.apply {

            if (isFieldAvailable == "0" && isSpecificForFlush == "0") {
                llShowSinDetails.isVisible = false
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = false
                llDelayLayout.isVisible = false
                tvSinDetails.isVisible = false
            } else if (isFieldAvailable == "0" && isSpecificForFlush == "1") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = true
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = false
                tvSinDetails.isVisible = true
            } else if (isFieldAvailable == "1" && isSpecificForFlush == "0") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = true
                tvSinDetails.isVisible = true
            } else if (isFieldAvailable == "1" && isSpecificForFlush == "1") {
                llShowSinDetails.isVisible = true
                llFlushLayout.isVisible = true
                llBatteryVoltage.isVisible = true
                llDelayLayout.isVisible = true
                tvSinDetails.isVisible = true
            } else {
                llShowSinDetails.isVisible = false
                llFlushLayout.isVisible = false
                llBatteryVoltage.isVisible = false
                llDelayLayout.isVisible = false
                tvSinDetails.isVisible = false
            }

            applySlideInAnimation(clSinUpdate)

            setEditTextFilterForAlphaNumericText(2, etSin1, etSin2, etSin3)/*
            setEditTextFilterForAlphaNumericText(4, etFin1)
            setEditTextFilterForAlphaNumericText(5, etFin2)
            setEditTextFilterForAlphaNumericText(3, etFin3)

            setupEditTextAutoShift(etFin1, etFin2, 4, null)
            setupEditTextAutoShift(etFin2, etFin3, 5, etFin1)
            setupEditTextAutoShift(etFin3, null, 3, etFin2)*/

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

            ivBack.setOnClickWithAnimation {
                finish()
            }

            tvResolveDate.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                showDatePickerDialogWithFutureDates(tvResolveDate)
            }
        }

        binding.tvBtnResolveComplaint.setOnClickWithAnimation {
            if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation

            val sinPart1 = binding.etSin1.text.toString()
            val sinPart2 = binding.etSin2.text.toString()
            val sinPart3 = binding.etSin3.text.toString()
            val sin = "$sinPart1-$sinPart2-$sinPart3"

            val finPart1 = binding.etFin1.text.toString()
            val finPart2 = binding.etFin2.text.toString()
            val finPart3 = binding.etFin3.text.toString()
            val fin = "$finPart1-$finPart2-$finPart3"

            val userCount = binding.etUserCount.text.toString()
            val waterSaved = binding.etWaterSaved.text.toString()
            val costSaved = binding.etCostSaved.text.toString()
            val range = binding.tvRange.text.toString()
            val preFlush = binding.tvPreFlush.text.toString()
            val flushTime = binding.tvFlushTime.text.toString()
            val offDelay = binding.tvOffDelay.text.toString()
            val cutOffDelay = binding.tvCutOffDelay.text.toString()
            val batteryVoltage = binding.etBatteryVoltage.text.toString()
            binding.tvComplaintNo.text.toString()
            val resolveDateText = binding.tvResolveDate.text.toString()
            val resolveAction = binding.etResolveAction.text.toString()
            val paymentDone = binding.etPaymentDone.text.toString()

            val bundle = Bundle().apply {
                putString(COMPLAINT_ID, complaintId)
                putString(CUSTOMER_ID, customerId)
                putString(PRODUCT_ID, productId)
                if (fin != "--") {
                    putString(FIN_NO, fin)
                }
                putString(SIN_NO, sin)
                putString(USER_COUNT, userCount)
                putString(RANGE, range)
                putString(PRE_FLUSH, preFlush)
                putString(FLUSH_TIME, flushTime)
                putString(OFF_DELAY, offDelay)
                putString(CUT_OFF_DELAY, cutOffDelay)
                putString(BATTERY_VOLTAGE, batteryVoltage)
                putString(TOTAL_PAYMENT, paymentDone)
                putString(REMARK, remark)
                putString(CUSTOMER_NAME, customerName)
                putString(SIN_UPDATE_ID, updateId)
                putString(INSTALLATION_IMAGE, installationImage)
                putString(INSTALLATION_IMAGE_ID, installationImageId)
                putString(RESOLVE_DATE, resolveDate)
                putString(RESOLVE_ACTION, resolveAction)
                putString(WATER_SAVED, waterSaved)
                putString(COST_SAVED, costSaved)
                putString(IS_FIELD_AVAILABLE, isFieldAvailable)
                putString(IS_SPECIFIC_CATEGORY_FOR_FLUSH, isSpecificForFlush)
            }

            val requiredFields = mutableListOf(sinPart1, sinPart2, sinPart3, sin, resolveDateText, resolveAction, paymentDone)

            if (isFieldAvailable == "0") {
                if (isSpecificForFlush == "1") {
                    requiredFields.addAll(listOf(userCount, waterSaved, costSaved, range, preFlush, flushTime, batteryVoltage))
                }
            } else {
                requiredFields.addAll(listOf(userCount, waterSaved, costSaved, range, offDelay, cutOffDelay, batteryVoltage))
                if (isSpecificForFlush == "1") {
                    requiredFields.addAll(listOf(preFlush, flushTime))
                }
            }

            if (requiredFields.any { it.isBlank() }) {
                showAnimatedCustomToast("Please fill in all required fields!")
            } else if (fin.length <= 13) {
                showAnimatedCustomToast("Please enter valid FIN number!")
            } else if (sin.length <= 7) {
                showAnimatedCustomToast("Please enter valid SIN number!")
            } else {
                val intent = Intent(this@SINUpdateActivity, ComplaintCustomerRemarkActivity::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent)
//                finish()
            }
        }

        editEnableOnClick()

        val editTexts = listOf(
            binding.etSin1, binding.etSin2, binding.etSin3, binding.etUserCount, binding.etWaterSaved, binding.etCostSaved, binding.etBatteryVoltage, binding.etResolveAction, binding.etPaymentDone
        )
        setCursorToEndOnFocus(editTexts)
    }

    private fun complaintSinDetailsApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.complaintSinDetails(
            token.toString(), customerId, productId, complaintId, "0"
        )
        getDetail.enqueue(object : Callback<ComplaintSinDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ComplaintSinDetailsResponse>, response: Response<ComplaintSinDetailsResponse>) {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1) {
                        val responseData = response.body()?.data
                        val sinDataResponse = response.body()?.data?.sinDetails
                        val complaintDataResponse = response.body()?.data?.complaintDetails
                        if (responseData != null) {
//                        val fin = complaintDataResponse?.productFinNo
                            val fin = sinDataResponse?.finNo
                            val finParts = fin?.split("-")
                            if (finParts?.size == 3) {
                                binding.etFin1.setText(finParts[0])
                                binding.etFin2.setText(finParts[1])
                                binding.etFin3.setText(finParts[2])
                            } else {
                                binding.etFin1.setText("")
                                binding.etFin2.setText("")
                                binding.etFin3.setText("")
                            }
                            val sin = complaintDataResponse?.sin
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
                            binding.etUserCount.setText(sinDataResponse?.userCount ?: "")
                            binding.etWaterSaved.setText(sinDataResponse?.waterSaved ?: "")
                            binding.etCostSaved.setText(sinDataResponse?.costSaved ?: "")
                            binding.tvRange.text = sinDataResponse?.range ?: ""
                            binding.tvPreFlush.text = sinDataResponse?.preflush ?: ""
                            binding.tvFlushTime.text = sinDataResponse?.flushTime ?: ""
                            binding.tvOffDelay.text = sinDataResponse?.offDelay ?: ""
                            binding.tvCutOffDelay.text = sinDataResponse?.cutOffDelay ?: ""
                            binding.etBatteryVoltage.setText(sinDataResponse?.batteryVoltage ?: "")
                            binding.tvComplaintNo.text = complaintDataResponse?.uuid ?: ""
                            binding.etPaymentDone.setText(sinDataResponse?.totalPayment ?: "")
                            binding.tvResolveDate.text = complaintDataResponse?.resolveDate?.let { formatDateInYearFormatForApi(it) } ?: ""
                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@SINUpdateActivity, httpException)
                    }
                } catch (e: Exception) {
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("complaintSinDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }

            override fun onFailure(call: Call<ComplaintSinDetailsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@SINUpdateActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("complaintSinDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }

    private fun complaintPendingSinDetailsApiCall() {
        binding.clLoader.isVisible = true
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val getDetail = retrofitBuilder.complaintPendingSinDetails(token.toString(), complaintId, "1")
        getDetail.enqueue(object : Callback<PendingSinDetailsResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<PendingSinDetailsResponse>, response: Response<PendingSinDetailsResponse>) {
                binding.clLoader.isVisible = false
                try {
                    if (response.isSuccessful && response.body() != null && response.body()?.code == 1) {
                        val responseData = response.body()?.data?.installation
                        if (responseData != null) {
                            remark = responseData.remark
                            customerName = responseData.customerName
                            updateId = responseData._id
                            if (!responseData.image.isNullOrEmpty()) {
                                for (installationImg in responseData.image!!) {
                                    installationImage = installationImg?.url
                                    installationImageId = installationImg?._id
                                }
                            }
                            val fin = responseData.finNo
                            val finParts = fin?.split("-")
                            if (finParts?.size == 3) {
                                binding.etFin1.setText(finParts[0])
                                binding.etFin2.setText(finParts[1])
                                binding.etFin3.setText(finParts[2])
                            } else {
                                binding.etFin1.setText("")
                                binding.etFin2.setText("")
                                binding.etFin3.setText("")
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
                            binding.tvCutOffDelay.text = responseData.cutOffDelay ?: ""
                            binding.etBatteryVoltage.setText(responseData.batteryVoltage ?: "")
                            binding.tvComplaintNo.text = responseData.uuid ?: ""
                            binding.etResolveAction.setText(responseData.resolveAction ?: "")
                            binding.etPaymentDone.setText(responseData.totalPayment ?: "")

                        }
                    } else {
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(this@SINUpdateActivity, httpException)
                    }
                } catch (e: Exception) {
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("complaintPendingSinDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }

            override fun onFailure(call: Call<PendingSinDetailsResponse>, t: Throwable) {
                binding.clLoader.isVisible = false
                try {
                    ApiErrorHandler.handleException(this@SINUpdateActivity, t)
                } catch (e: Exception) {
                    e.message.mLog()
                    CrashlyticsHelper.log("complaintPendingSinDetailsApiCall-API call failed ${e.message}")
                }
            }
        })
    }

    @SuppressLint("DefaultLocale")
    fun showDatePickerDialogWithFutureDates(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this, R.style.CustomDatePickerDialogTheme, { _, selectedYear, selectedMonth, selectedDay ->
                // Format for TextView: dd/MM/yyyy
                val displayDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                textView.text = displayDate
                // Format for API: yyyy-MM-dd
                resolveDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            }, year, month, day
        )
        val minDate = Calendar.getInstance()
        datePickerDialog.datePicker.minDate = minDate.timeInMillis
        datePickerDialog.show()
    }

    private fun editEnableOnClick() {
        val editFields = mapOf(
            binding.ivEditFin to listOf(binding.etFin1, binding.etFin2, binding.etFin3),
            binding.ivEditSin to listOf(binding.etSin1, binding.etSin2, binding.etSin3),
            binding.ivEditUserCount to listOf(binding.etUserCount),
            binding.ivEditWaterSaved to listOf(binding.etWaterSaved),
            binding.ivEditCostSaved to listOf(binding.etCostSaved),
            binding.ivEditBatteryVoltage to listOf(binding.etBatteryVoltage),
            binding.ivEditResolveAction to listOf(binding.etResolveAction),
            binding.ivEditPaymentDone to listOf(binding.etPaymentDone),
        )

        editFields.values.flatten().forEach { it.disableEditing() }

        editFields.forEach { (button, fields) ->
            button.setOnClickWithAnimation {
                fields.forEach { it.enableEditing() }
                fields.first().requestFocus()
            }
        }

        val dropdownFields = mapOf(
            binding.ivEditRange to binding.tvRange,
            binding.ivEditPreFlush to binding.tvPreFlush,
            binding.ivEditFlushTime to binding.tvFlushTime,
            binding.ivEditOffDelay to binding.tvOffDelay,
            binding.ivEditCutOffDelay to binding.tvCutOffDelay,
        )

        dropdownFields.values.forEach { it.disableEditing() }

        dropdownFields.forEach { (button, field) ->
            button.setOnClickWithAnimation {
                field.enableEditing()
                showPopupMenuList(
                    field, when (field) {
                        binding.tvRange -> rangeSetting
                        binding.tvPreFlush -> preFlush
                        binding.tvFlushTime -> flushTime
                        binding.tvOffDelay -> offDelay
                        binding.tvCutOffDelay -> cutOffDelay
                        else -> emptyList()
                    }
                )
            }
        }
    }

    private fun View.disableEditing() {
        isFocusable = false
        isFocusableInTouchMode = false
        isClickable = false
    }

    private fun View.enableEditing() {
        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
        requestFocus()
        (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

}