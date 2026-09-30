package com.app.bharatphoton.common

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Suppress("DEPRECATION")
class ScreenShotHelper {
    companion object {

        @RequiresApi(Build.VERSION_CODES.O)
        fun generateFullPDFAsync(
            context: Context,
            parentView: View,
            recyclerView: RecyclerView,
            fileName: String,
            callback: (File?) -> Unit
        ) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Generating PDF...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            CoroutineScope(Dispatchers.IO).launch {
                val file = generateFullPDF(context, parentView, recyclerView, fileName)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    callback(file)
                    // ✅ Open PDF automatically if generated
                    file?.let { openPDF(context, it) }
                }
            }
        }

        @SuppressLint("UseKtx")
        @RequiresApi(Build.VERSION_CODES.O)
        fun generateFullPDF(
            context: Context,
            topView: View,
            recyclerView: RecyclerView,
            fileName: String
        ): File? {
            return try {
                // 1️⃣ Capture top layout
                val topBitmap = getBitmapFromView(context, topView)

                // 2️⃣ Capture RecyclerView fully
                val recyclerBitmap = getBitmapFromRecyclerViewFully(recyclerView)

                // 3️⃣ Merge them vertically
                val mergedWidth = maxOf(topBitmap.width, recyclerBitmap.width)
                val mergedHeight = topBitmap.height + recyclerBitmap.height

                val mergedBitmap = Bitmap.createBitmap(mergedWidth, mergedHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(mergedBitmap)
                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(topBitmap, 0f, 0f, null)
                canvas.drawBitmap(recyclerBitmap, 0f, topBitmap.height.toFloat(), null)

                // 4️⃣ Create PDF
                val pdf = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(mergedBitmap.width, mergedBitmap.height, 1).create()
                val page = pdf.startPage(pageInfo)
                page.canvas.drawBitmap(mergedBitmap, 0f, 0f, null)
                pdf.finishPage(page)

                val file = File(context.getExternalFilesDir(null), "$fileName.pdf")
                pdf.writeTo(FileOutputStream(file))
                pdf.close()

                // 5️⃣ Recycle bitmaps
                topBitmap.recycle()
                recyclerBitmap.recycle()
                mergedBitmap.recycle()

                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        @SuppressLint("UseKtx")
        fun getBitmapFromView(context: Context, view: View): Bitmap {
            val displayMetrics = context.resources.displayMetrics
            view.measure(
                View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        @SuppressLint("UseKtx")
        fun getBitmapFromRecyclerViewFully(recyclerView: RecyclerView): Bitmap {
            val adapter = recyclerView.adapter ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            val paint = Paint()
            var totalHeight = 0
            val bitmaps = mutableListOf<Bitmap>()

            // Capture all items using adapter.itemCount
            for (i in 0 until adapter.itemCount) {
                val holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(holder, i)  // position is passed here, so sNo will be correct
                holder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                holder.itemView.layout(0, 0, holder.itemView.measuredWidth, holder.itemView.measuredHeight)

                val bitmap = Bitmap.createBitmap(holder.itemView.measuredWidth, holder.itemView.measuredHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                holder.itemView.draw(canvas)
                bitmaps.add(bitmap)
                totalHeight += holder.itemView.measuredHeight
            }

            // Merge all bitmaps vertically
            val fullBitmap = Bitmap.createBitmap(recyclerView.width, totalHeight, Bitmap.Config.ARGB_8888)
            val fullCanvas = Canvas(fullBitmap)
            var yOffset = 0
            for (bmp in bitmaps) {
                fullCanvas.drawBitmap(bmp, 0f, yOffset.toFloat(), paint)
                yOffset += bmp.height
                bmp.recycle()
            }

            return fullBitmap
        }


        private fun openPDF(context: Context, file: File) {
            val pdfUri: Uri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No PDF viewer found", Toast.LENGTH_SHORT).show()
            }
        }

        fun showSaveNotification(context: Context, filePath: String) {
            val channelId = "pdf_save_channel"
            val channelName = "PDF Save Notifications"
            val notificationId = 1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val file = File(filePath)
            val pdfUri: Uri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val pendingIntent = PendingIntent.getActivity(
                context, 0, openPdfIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_menu_save)
                .setContentTitle("PDF Saved")
                .setContentText("Saved to: $filePath")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
