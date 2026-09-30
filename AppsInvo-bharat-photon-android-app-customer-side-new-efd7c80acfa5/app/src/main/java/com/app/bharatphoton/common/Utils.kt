@file:Suppress("DEPRECATION")

package com.app.bharatphoton.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavOptions
import com.app.bharatphoton.BuildConfig
import com.app.bharatphoton.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.razorpay.Checkout
import org.json.JSONObject
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    fun navigateWithAnimation(context: Context, clearBackStack: Boolean = false): NavOptions {
        val isRtl = context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
        return NavOptions.Builder().apply {
            if (clearBackStack) {
                setPopUpTo(R.id.navigation, true)
            }
            setEnterAnim(if (isRtl) R.anim.nav_slide_in_left else R.anim.nav_slide_in_right)
//            setExitAnim(if (isRtl) R.anim.nav_slide_out_right else R.anim.nav_slide_out_left)
//            setPopEnterAnim(if (isRtl) R.anim.nav_slide_in_right else R.anim.nav_slide_in_left)
            setPopExitAnim(if (isRtl) R.anim.nav_slide_out_left else R.anim.nav_slide_out_right)
        }.build()
    }

    fun Activity.openSettingPopup(message: String) {
        var alertDialog: AlertDialog? = null

        val alertDialogBuilder = AlertDialog.Builder(this).apply {
            setTitle("Permission Required")
            setMessage(message)
            setCancelable(false)
        }

        alertDialogBuilder.setPositiveButton("Setting") { _, _ -> redirectToAppSettings(this) }
        alertDialogBuilder.setNegativeButton("Cancel") { _, _ -> alertDialog?.dismiss() }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    fun formatPrice(price: Double, currencyType: String): String {
        val currencySymbol = Currency.getInstance(currencyType).symbol
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        numberFormat.maximumFractionDigits = 2

        return "$currencySymbol ${numberFormat.format(price)}"
    }


    private fun redirectToAppSettings(activity: Activity) {
        val uri = Uri.fromParts("package", activity.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = uri }
        activity.startActivity(intent)
    }

    fun zoomInOutAnimation(view: List<View>) {
        view.forEach { view ->
            val zoomInX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f)
            val zoomInY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)
            val zoomOutX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.5f)
            val zoomOutY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.5f)

            val zoomInSet = AnimatorSet().apply {
                playTogether(zoomInX, zoomInY)
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            val zoomOutSet = AnimatorSet().apply {
                playTogether(zoomOutX, zoomOutY)
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
            }

            zoomInSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.postDelayed({ zoomOutSet.start() }, 50)
                }
            })

            zoomOutSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.postDelayed({ zoomInSet.start() }, 50)
                }
            })

            zoomInSet.start()
        }
    }


    fun downloadFile(context: Context, url: String, fileName: String) {
        val progressDialog = ProgressDialog(context).apply {
            setMessage("Downloading $fileName...")
            setCancelable(true)
            show()
        }

        try {
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setTitle("Downloading File")
                setDescription("Downloading $fileName")
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                    if (id == downloadId) {
                        if (progressDialog.isShowing) progressDialog.dismiss()

                        val query = DownloadManager.Query().setFilterById(downloadId)
                        val cursor = downloadManager.query(query)

                        if (cursor.moveToFirst()) {
                            val localUri = cursor.getString(
                                cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)
                            ) ?: return

                            // remove file://
                            val filePath = localUri.replace("file://", "")
                            val file = File(filePath)

                            openDownloadedFile(context, file)
                        }

                        cursor.close()
                        context.unregisterReceiver(this)
                    }
                }
            }

            context.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_EXPORTED
            )

        } catch (e: Exception) {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    fun openDownloadedFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",   // MUST MATCH manifest
                file
            )

            val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            }

            context.startActivity(openPdfIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show()
        }
    }


    fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "*/*"
    }


    fun showDialog(
        context: Context, messageText: String
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.subsctiption_not_available_pop_up)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        val messageTextView: TextView = dialog.findViewById(R.id.messageTv)
        val okayButton: CardView = dialog.findViewById(R.id.okay)
        val cancelButton: ImageView = dialog.findViewById(R.id.cross)
        val logoImg: ImageView = dialog.findViewById(R.id.logo)
        val oops: TextView = dialog.findViewById(R.id.oopsTv)

        zoomInOutAnimation(view = listOf(logoImg))
        messageTextView.text = messageText


        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        okayButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // Get Device Id
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun showPopUpDialog(context: Context, message: String, onOk: () -> Unit) {
        val ok: LinearLayout?
        val cancel: LinearLayout?
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.logout_pop_up)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        ok = dialog.findViewById(R.id.ok)
        cancel = dialog.findViewById(R.id.cancel)
        val title = dialog.findViewById<TextView>(R.id.title)
        title.text = message
        cancel.setOnAnimatedClickListener {
            dialog.cancel()
        }
        ok.setOnAnimatedClickListener {
            dialog.cancel()
            onOk()
        }
        dialog.show()
    }

    // Show Camera popup
    fun showPopup(
        context: Context, message: String, positiveButtonText: String, negativeButtonText: String, positiveAction: () -> Unit, negativeAction: () -> Unit
    ) {
        val alertDialog = AlertDialog.Builder(context).apply {
            setMessage(message)
            setCancelable(true)
            setPositiveButton(positiveButtonText) { dialog, _ ->
                positiveAction()
                dialog.dismiss()
            }
            setNegativeButton(negativeButtonText) { dialog, _ ->
                negativeAction()
                dialog.dismiss()
            }

        }.create()

        alertDialog.show()
    }


    // Animated Click-Listener
    fun View.setOnAnimatedClickListener(duration: Long = 100, scaleFactor: Float = 0.9f, onClickAction: () -> Unit) {
        this.setOnClickListener {
            this.isEnabled = false

            this.animate().scaleX(scaleFactor).scaleY(scaleFactor).setDuration(duration).withEndAction {
                this.animate().scaleX(1f).scaleY(1f).setDuration(duration).withEndAction {
                    this.isEnabled = true
                    onClickAction()
                }.start()
            }.start()
        }
    }

    // Update Rating Starts
    fun updateRatingStars(rating: Int, view: LinearLayout, context: Context) {
        view.removeAllViews()
        for (i in 1..rating) {
            val starImageView = ImageView(context).apply {
                setImageResource(R.drawable.star)
                setPadding(
                    resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp),
                    (resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)),
                    resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp),
                    0
                )
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                ).apply {
                    gravity = Gravity.CENTER
                }
            }
            view.addView(starImageView)
        }
    }

    // Hide Keyboard
    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Show keyboard
    fun showKeyBoard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInputFromInputMethod(view.windowToken, 0)
    }

    // Show description with html with expandable view
    fun setupExpandableTextView(
        textView: TextView, viewMore: TextView, description: String, initialMaxLines: Int
    ) {
        val cleanedDescription = description.replace("<br>", "").replace("<p>", "")
        textView.text = Html.fromHtml(cleanedDescription, Html.FROM_HTML_MODE_COMPACT)

        textView.post {
            if (textView.lineCount > initialMaxLines) {
                textView.maxLines = initialMaxLines
                viewMore.visibility = View.VISIBLE
                textView.ellipsize = TextUtils.TruncateAt.END

                viewMore.apply {
                    text = "View More"
                    setTextColor(Color.BLUE)
                    setOnClickListener {
                        if (textView.maxLines == initialMaxLines) {
                            textView.maxLines = Int.MAX_VALUE
                            this.text = "View Less"
                        } else {
                            textView.maxLines = initialMaxLines
                            this.text = "View More"
                        }
                    }
                }
            } else {
                viewMore.visibility = View.GONE
            }
        }
    }

//    SimpleDateFormat("MMMM dd, yyyy hh:mm a", Locale.getDefault())

    // Format Readable Date
    fun formatReadableDate(dateString: String, simpleDateFormat: SimpleDateFormat): String {
        return try {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
            originalFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date: Date = originalFormat.parse(dateString) ?: return ""
            simpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            e.message.mlog()
            ""
        }
    }

    // Get Image From Html Text
    class UrlDrawable : BitmapDrawable() {
        var drawable: Drawable? = null
        override fun draw(canvas: Canvas) {
            drawable?.draw(canvas)
        }
    }

    class ImageGetter(val textView: TextView) : Html.ImageGetter {
        @SuppressLint("UseCompatLoadingForDrawables")
        override fun getDrawable(source: String?): Drawable {
            val urlDrawable = UrlDrawable()

            source?.let { url ->
                Glide.with(textView.context).load(url).placeholder(R.drawable.placeholder).into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        urlDrawable.drawable = resource
                        resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                        urlDrawable.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)

                        textView.text = textView.text
                        textView.invalidate()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        urlDrawable.drawable = placeholder
                    }
                })
            }
            return urlDrawable
        }
    }

    // Format Plain Text
    fun formatText(prefix: String, plan: String, color: Int): SpannableString {
        val text = "$prefix$plan"
        val spannableString = SpannableString(text)

        // For Style
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD), prefix.length, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // For Color
        spannableString.setSpan(
            ForegroundColorSpan(color), prefix.length, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    // Tap to copy
    fun setupTapToCopy(textView: TextView, context: Context) {
        textView.setOnAnimatedClickListener {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", "yGg741")
            clipboardManager.setPrimaryClip(clip)
//            Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    // Razor Pay Start Payment

    fun startPayment(
        razorpayId: String,
        activity: Activity,
        amount: String,
    ) {
        val checkout = Checkout().apply {
            setKeyID(BuildConfig.RAZORPAY_KEY)
        }

        try {
            // Validate amount
            if (amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                throw IllegalArgumentException("Invalid payment amount")
            }

            val amountInPaise = (amount.toDouble() * 100).toInt()
            val options = JSONObject().apply {
                put("name", "Bharat Photon Pvt. Ltd.")
                put("description", "Product Description")
                put("theme.color", "#3399cc")
                put("currency", "INR")
                put("order_id", razorpayId)
                put("amount", amountInPaise)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            e.message.mlog()
            e.printStackTrace()
        }
    }

    // hideKeyBoard with textView
    fun hideKeyBoardWithEditTextLength(editText: EditText, context: Context, view: View, length: Int) {
        editText.addTextChangedListener {
            if (it.toString().length == length) {
                hideKeyboard(context = context, view = view)
            }
        }
    }

//    chaneColoOfOneTextView

    fun chaneColoOfOneTextView(string: String, wordToColor: String, context: Context, color: Int, finalWord: TextView) {
        val fullText = string
        val spannableString = SpannableString(fullText)
        val wordToColor = wordToColor
        val startIndex = fullText.indexOf(wordToColor)
        val endIndex = startIndex + wordToColor.length
        if (startIndex >= 0) {
            spannableString.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, color)), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        finalWord.text = spannableString
    }

}
