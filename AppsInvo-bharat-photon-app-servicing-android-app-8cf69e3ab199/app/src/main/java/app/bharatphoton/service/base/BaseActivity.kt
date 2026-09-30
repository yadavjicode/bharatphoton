package app.bharatphoton.service.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import app.bharatphoton.service.R
import app.bharatphoton.service.api_model_classes.AwsCredentialResponse
import app.bharatphoton.service.api_model_classes.ProfileDetailsResponse
import app.bharatphoton.service.interfaces.MyApi
import app.bharatphoton.service.utils.ApiErrorHandler
import app.bharatphoton.service.utils.Constants.FULL_NAME
import app.bharatphoton.service.utils.Constants.NAME
import app.bharatphoton.service.utils.Constants.PHONE_NO
import app.bharatphoton.service.utils.Constants.PROFILE_PIC
import app.bharatphoton.service.utils.Constants.S3_BUCKET_KEY
import app.bharatphoton.service.utils.Constants.S3_BUCKET_NAME
import app.bharatphoton.service.utils.Constants.S3_BUCKET_REGION
import app.bharatphoton.service.utils.Constants.S3_BUCKET_SECRET
import app.bharatphoton.service.utils.CrashlyticsHelper
import app.bharatphoton.service.utils.RetrofitBuilder
import app.bharatphoton.service.utils.SharedPreferencesHelper
import app.bharatphoton.service.utils.mLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

abstract class BaseActivity : AppCompatActivity() {

    //////////////////////////////////////////////////////////////////////////////////

    fun isVideo(url: String): Boolean {
        return url.endsWith(".mp4", true) || url.endsWith(".mov", true) || url.endsWith(".avi", true) || url.endsWith(".mkv", true) || url.endsWith(".flv", true) || url.endsWith(".wmv", true)
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun animateViews(
        vararg views: View, startDelayMs: Long = 0, staggerDelayMs: Long = 100, durationMs: Long = 300,
        initialAlpha: Float = 0f, targetAlpha: Float = 1f, onAnimationEnd: (() -> Unit)? = null) {
        if (views.isEmpty()) {
            onAnimationEnd?.invoke()
            return
        }
        var completedAnimations = 0
        val totalAnimations = views.size
        views.forEachIndexed { index, view ->
            view.alpha = initialAlpha
            view.visibility = View.VISIBLE
            view.animate()
                .alpha(targetAlpha)
                .setStartDelay(startDelayMs + index * staggerDelayMs)
                .setDuration(durationMs)
                .withEndAction {
                    completedAnimations++
                    if (completedAnimations == totalAnimations) {
                        onAnimationEnd?.invoke()
                    }
                }
                .start()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun setEditTextFilterForAlphaNumericText(maxLength: Int, vararg editTexts: EditText) {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val filter = InputFilter { source, _, _, _, _, _ ->
            if (source.all { it in allowedChars }) source else ""
        }
        for (editText in editTexts) {
            editText.filters = arrayOf(filter, InputFilter.LengthFilter(maxLength))
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun applySlideInAnimation(view: View) {
        view.post {
            view.translationX = view.width.toFloat()
            view.animate()
                .translationX(0f)
                .setDuration(300)
                .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                .start()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun setupEditTextAutoShift(currentEditText: EditText, nextEditText: EditText?, shiftAfter: Int, previousEditText: EditText?) {
        currentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    val uppercaseText = s.toString().uppercase(Locale.getDefault())

                    if (uppercaseText != s.toString()) {
                        currentEditText.removeTextChangedListener(this)
                        currentEditText.setText(uppercaseText)
                        currentEditText.setSelection(uppercaseText.length)
                        currentEditText.addTextChangedListener(this)
                    }
                }
                if (s?.length == shiftAfter) {
                    if (nextEditText != null) {
                        nextEditText.requestFocus()
                    } else {
                        hideKeyboard(currentEditText)
                    }
                } else if (s?.isEmpty() == true && previousEditText != null) {
                    previousEditText.requestFocus()
                }
            }
        })

        currentEditText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && currentEditText.text.isEmpty()) {
                previousEditText?.requestFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    //////////////////////////////////////////////////////////////////////////////////

    fun setCursorToEndOnFocus(editTexts : List<EditText>) {
        editTexts.forEach { editText ->
            editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    editText.setSelection(editText.text?.length ?: 0)
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun formatDateInStringFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) //Aug 20, 2024
        val date: Date? = inputFormat.parse(inputDate)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "Invalid Date"
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("InflateParams")
    fun showAnimatedCustomToast(message: String) {
        val inflater = LayoutInflater.from(this)
        val customView: View = inflater.inflate(R.layout.custom_toast, null)
        val toastMessage: TextView = customView.findViewById(R.id.toast_message)
        val toastIcon: ImageView = customView.findViewById(R.id.toast_icon)
        toastMessage.text = message
        toastIcon.setImageResource(R.drawable.logo_icon_small)
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        customView.startAnimation(animation)
        val toast = Toast(this)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = customView
        toast.show()
    }

    //////////////////////////////////////////////////////////////////////////////////

    fun formatDateInYearFormat(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Desired format
        val date: Date? = inputFormat.parse(inputDate)
        return if (date != null) {
            outputFormat.format(date) // Output in dd/MM/yyyy
        } else {
            "Invalid Date"
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun formatDateInYearFormatForApi(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Desired format
        val date: Date? = inputFormat.parse(inputDate)
        "date-> $date".mLog()
        return if (date != null) {
            outputFormat.format(date) // Output in yyyy-MM-dd
        } else {
            "Invalid Date"
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun showPopupMenuList(itemView: TextView, popupItems: List<String>){
        val contextThemeWrapper = ContextThemeWrapper(this, R.style.CustomPopupMenuTextStyle)
        val popupMenu = PopupMenu(contextThemeWrapper, itemView)
        popupMenu.menu.clear()
        popupItems.forEachIndexed { index, item ->
            popupMenu.menu.add(0, index, index, item)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            itemView.text = menuItem.title
            true
        }
        popupMenu.show()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getStorageDetailsApiCall(context: Context) {
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.awsCredential()
        call.enqueue(object : Callback<AwsCredentialResponse> {
            override fun onResponse(call: Call<AwsCredentialResponse>, response: Response<AwsCredentialResponse>)
            {
                try {
                    if (response.isSuccessful){
                        val responseData=response.body()?.data
                        val fullRegion = responseData?.region.toString()

                        S3_BUCKET_KEY = responseData?.accessKeyId.toString()
                        S3_BUCKET_REGION = fullRegion.substringAfterLast(" ").trim()
                        S3_BUCKET_SECRET = responseData?.secretAccessKey.toString()
                        S3_BUCKET_NAME = responseData?.bucketName.toString()
                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(context, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("getStorageDetailsApiCall-Exception in parsing response: ${e.message}.")
                }
            }
            override fun onFailure(call: Call<AwsCredentialResponse>, t: Throwable) {
                try {
                    ApiErrorHandler.handleException(context, t)
                }catch (e:Exception){
                    e.message.mLog()
                    CrashlyticsHelper.log("getStorageDetailsApiCall-API call failed: ${e.message}")
                }
            }
        })
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun profileDetailsApiCall(context: Context, token: String?, sharedPreferencesHelper: SharedPreferencesHelper) {
        val retrofitBuilder = RetrofitBuilder.buildRetrofit(MyApi::class.java)
        val call = retrofitBuilder.profileDetails(token.toString())
        call.enqueue(object : Callback<ProfileDetailsResponse> {
            override fun onResponse(call: Call<ProfileDetailsResponse>, response: Response<ProfileDetailsResponse>)
            {
                try {
                    if (response.isSuccessful){
                        val responseData=response.body()?.data
                        val firstName = responseData?.firstName ?: "NA"
                        val lastName = response.body()?.data?.lastName ?: "NA"
                        val profilePic = response.body()?.data?.profileImage ?: "NA"
                        val countryCode = response.body()?.data?.country_code ?: "NA"
                        val phone = response.body()?.data?.phoneNo ?: "NA"
                        val phoneNo = "$countryCode $phone"
                        sharedPreferencesHelper.saveString(NAME,firstName)
                        sharedPreferencesHelper.saveString(FULL_NAME, "$firstName $lastName")
                        sharedPreferencesHelper.saveString(PROFILE_PIC,profilePic)
                        sharedPreferencesHelper.saveString(PHONE_NO,phoneNo)

                        ProfileDataSingleton.profileData = responseData

                    }
                    else{
                        val httpException = HttpException(response)
                        ApiErrorHandler.handleException(context, httpException)
                    }
                }catch (e: Exception){
                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("profileDetailsApiCall-Exception in parsing response ${e.message}")
                }
            }
            override fun onFailure(call: Call<ProfileDetailsResponse>, t: Throwable) {
                try {
                    ApiErrorHandler.handleException(context, t)
                }catch (e:Exception){
//                    e.message.mLog()
//                    showAnimatedCustomToast(e.message.toString())
                    CrashlyticsHelper.log("profileDetailsApiCall-API call failed:  ${e.message}")
                }
            }
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun setProgressDialog(context: Context): AlertDialog {
        val inflater = LayoutInflater.from(context)
        val progressView = inflater.inflate(R.layout.progress_dialog_layout, null)
        val builder = AlertDialog.Builder(context, R.style.FullScreenDialogStyle)
        builder.setCancelable(false)
        builder.setView(progressView)
        val progressDialog = builder.create()
        progressDialog.show()
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        return progressDialog
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showAnimatedCustomToast("Camera Permission Granted.")
            } else {
                showAnimatedCustomToast("Camera Permission Denied.")
            }
        }

    fun requestCameraPermission() {
        if (isCameraPermissionGranted()) {
            showAnimatedCustomToast("Camera Permission Already Granted.")
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun isCameraPermissionGranted(): Boolean {
        return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}