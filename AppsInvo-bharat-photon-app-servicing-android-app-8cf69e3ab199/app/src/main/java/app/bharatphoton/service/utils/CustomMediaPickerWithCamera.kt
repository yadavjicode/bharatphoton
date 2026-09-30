package app.bharatphoton.service.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import app.bharatphoton.service.R
import app.bharatphoton.service.base.BaseActivity
import app.bharatphoton.service.databinding.PopupMediaPickerBinding
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.VideoResizer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class CustomMediaPickerWithCamera(
    private val activity: ComponentActivity,
    private val callback: MediaPickerCallback
) {
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var progressDialog: AlertDialog? = null
    private var progressTextView: TextView? = null

    interface MediaPickerCallback {
        fun onMediaSelected(mediaUrl: String)
    }

    private val imageCaptureLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let { uri ->
                    handleImageCapture(uri)
                }
            }
        }

    private val videoCaptureLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                videoUri?.let { uri ->
                    handleVideoCapture(uri)
                }
            }
        }

    fun showMediaPickerDialog(context: Context, background: View?) {
        val imageFile = File.createTempFile("tmp_image_${System.currentTimeMillis()}", ".jpg", activity.cacheDir)
        val videoFile = File.createTempFile("tmp_video_${System.currentTimeMillis()}", ".mp4", activity.cacheDir)

        imageUri = FileProvider.getUriForFile(context, "app.bharatphoton.service.fileProvider", imageFile)
        videoUri = FileProvider.getUriForFile(context, "app.bharatphoton.service.fileProvider", videoFile)

        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = PopupMediaPickerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)
        val window: Window? = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.inset_choose_language_back)
        dialog.setCanceledOnTouchOutside(true)
        background?.alpha = 0.7f
        dialog.setOnDismissListener {
            background?.alpha = 1.0f
        }
        with(dialogBinding) {
            tvCamera.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                }
                imageCaptureLauncher.launch(takePictureIntent)
                dialog.dismiss()
                background?.alpha = 1.0f
            }
            tvVideo.setOnClickWithAnimation {
                if (!ClickDebounceUtil.canClick()) return@setOnClickWithAnimation
                val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
                    putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)
                }
                videoCaptureLauncher.launch(takeVideoIntent)
                dialog.dismiss()
                background?.alpha = 1.0f
            }
        }
        dialog.show()
    }

    private fun handleImageCapture(uri: Uri) {
        val progressDialog = (activity as BaseActivity).setProgressDialog(activity)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val file = activity.convertContentFilePath_To_FilePath(uri)
                if (file != null) {
                    val compressedImage = compressImage(file)
                    val url = AmazonS3Uploader.uploadFile(compressedImage)
                    withContext(Dispatchers.Main) {
                        callback.onMediaSelected(url ?: "")
                    }
                } else {
                    "Failed to get file from URI" + mLog()
                }
                progressDialog.dismiss()
            } catch (e: Exception) {
                progressDialog.dismiss()
                "Error handling image capture $e" + mLog()
            }
        }
    }

    private fun compressImage(file: File): File {
        val originalSize = file.length() / 1024
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val compressedFile = File(file.parent, "compressed_${file.name}")
        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)
        }
//        val compressedSize = compressedFile.length() / 1024
//        "Original Size: ${originalSize}KB, Compressed Size: ${compressedSize}KB".mLog()
        return compressedFile
    }

    private fun handleVideoCapture(uri: Uri) {
        showProgressDialog(activity)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateProgressMessage("Compressing... 0%")
                val file = compressVideo(uri)
                if (file != null) {
                    withContext(Dispatchers.Main) {
                        updateProgressMessage("Uploading...")
                    }
                    val url = AmazonS3Uploader.uploadFile(file)
                    withContext(Dispatchers.Main) {
                        callback.onMediaSelected(url ?: "")
                    }
                } else {
                    "Failed to get compressed file".mLog()
                }
            } catch (e: Exception) {
                "Error handling media capture: $e".mLog()
            } finally {
                withContext(Dispatchers.Main) {
                    dismissProgressDialog()
                }
            }
        }
    }

    private suspend fun compressVideo(uri: Uri): File? = withContext(Dispatchers.IO) {
        val inputStream = activity.contentResolver.openInputStream(uri)
        val originalSize = inputStream?.available()?.toLong() ?: 0L
        inputStream?.close()
        val outputDir = File(activity.cacheDir, "compressed_videos").apply { mkdirs() }
        val outputFile = File(outputDir, "compressed_${System.currentTimeMillis()}.mp4")
        val result = CompletableDeferred<File?>()
        VideoCompressor.start(
            context = activity,
            uris = listOf(uri),
            isStreamable = false,
            storageConfiguration = AppSpecificStorageConfiguration(),
            configureWith = Configuration(
                videoNames = listOf(outputFile.name),
                quality = VideoQuality.HIGH,
                isMinBitrateCheckEnabled = true,
                videoBitrateInMbps = 3,
                disableAudio = false,
                resizer = VideoResizer.matchSize(720.0, 1280.0, false)
            ),
            listener = object : CompressionListener {
                @SuppressLint("DefaultLocale")
                override fun onProgress(index: Int, percent: Float) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val formattedPercent = String.format("%.2f", percent)
                        withContext(Dispatchers.Main) {
                            updateProgressMessage("Compressing... $formattedPercent%")
                        }
                    }
                }
                override fun onStart(index: Int) {
                    "Compression Started".mLog()
                }
                override fun onSuccess(index: Int, size: Long, path: String?) {
                    val compressedFile = File(path ?: "")
                    val compressedSize = compressedFile.length()
//                    "Original Size: ${formatSize(originalSize)}".mLog()
//                    "Compressed Size: ${formatSize(compressedSize)}".mLog()
                    result.complete(File(path ?: ""))
                }
                override fun onFailure(index: Int, failureMessage: String) {
                    "Compression Failed: $failureMessage".mLog()
                    result.complete(null)
                }
                override fun onCancelled(index: Int) {
                    "Compression Cancelled".mLog()
                    result.complete(null)
                }
            }
        )
        return@withContext result.await()
    }

    private fun showProgressDialog(context: Context) {
        val inflater = LayoutInflater.from(context)
        val progressView = inflater.inflate(R.layout.progress_dialog_layout, null)
        progressTextView = progressView.findViewById(R.id.tvUploading)

        val builder = AlertDialog.Builder(context, R.style.FullScreenDialogStyle)
            .setCancelable(false)
            .setView(progressView)

        progressDialog = builder.create()
        progressDialog?.show()
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
    fun updateProgressMessage(message: String) {
        progressTextView?.text = message
    }
    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }



}
