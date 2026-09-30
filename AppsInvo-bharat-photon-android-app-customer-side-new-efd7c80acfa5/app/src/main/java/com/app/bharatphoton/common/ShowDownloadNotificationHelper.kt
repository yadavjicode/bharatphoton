package com.app.bharatphoton.common

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.github.mikephil.charting.charts.LineChart
import java.io.IOException

class ShowDownloadNotificationHelper {
    companion object {

        // For Download add graph
        @SuppressLint("UseKtx")
        fun downloadAllGraphsAsPDF(
            graphViews: List<LineChart>,
            fileName: String,
            topTexts: List<String>,
            bottomTexts: List<String>
        ) {
            val pdfDocument = PdfDocument()
            val context = graphViews[0].context
            val resolver = context.contentResolver

            try {
                graphViews.forEachIndexed { index, graphView ->
                    val topMargin = 100
                    val bottomMargin = 100
                    val bitmapHeight = graphView.height + topMargin + bottomMargin

                    val bitmap = Bitmap.createBitmap(graphView.width, bitmapHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)

                    val paint = Paint().apply {
                        color = Color.BLACK
                        textSize = 40f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        textAlign = Paint.Align.CENTER
                    }

                    val centerX = (graphView.width / 2).toFloat()
                    canvas.drawText(topTexts[index], centerX, 50f, paint)
                    canvas.translate(0f, topMargin.toFloat())
                    graphView.draw(canvas)
                    canvas.translate(0f, graphView.height.toFloat())
                    canvas.drawText(bottomTexts[index], centerX, 50f, paint)
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    pdfDocument.finishPage(page)
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Graphs")
                }

                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        Toast.makeText(context, "PDF saved to Downloads/Graphs", Toast.LENGTH_LONG).show()
                        showSaveNotification(context, it)
                    } ?: throw IOException("Failed to get output stream")
                } ?: throw IOException("Failed to create file")

            } catch (e: IOException) {
                Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                pdfDocument.close()
            }
        }

        // Download only specific graph
        @SuppressLint("UseKtx")
        fun downloadGraphAsPDF(graphView: View, fileName: String, topText: String, bottomText: String) {
            val extraHeight = 200
            val bitmap = Bitmap.createBitmap(graphView.width, graphView.height + extraHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }

            canvas.drawText(topText, 10f, 50f, textPaint)
            canvas.translate(0f, 100f)
            graphView.draw(canvas)
            canvas.translate(0f, graphView.height.toFloat() + 50f)
            canvas.drawText(bottomText, 10f, 50f, textPaint)

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val pdfCanvas = page.canvas
            pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)
            pdfDocument.finishPage(page)

            val fileNameWithExt = "$fileName.pdf"
            val resolver = graphView.context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameWithExt)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Graphs")
            }

            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            try {
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                        Toast.makeText(graphView.context, "PDF saved to Downloads/Graphs", Toast.LENGTH_LONG).show()
                        showSaveNotification(graphView.context, it)
                    } ?: throw IOException("Failed to get output stream")
                } ?: throw IOException("Failed to create file")

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(graphView.context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                pdfDocument.close()
            }
        }


        private fun showSaveNotification(context: Context, pdfUri: Uri) {
            val channelId = "pdf_save_channel"
            val channelName = "PDF Save Notifications"
            val notificationId = 1

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
                )
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val openPdfIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(pdfUri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            val pendingIntent = PendingIntent.getActivity(
                context, 0, openPdfIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification =
                NotificationCompat.Builder(context, channelId).setSmallIcon(android.R.drawable.ic_menu_save).setContentTitle("PDF Saved").setContentText("Tap to open your PDF")
                    .setContentIntent(pendingIntent).setPriority(NotificationCompat.PRIORITY_DEFAULT).build()

            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }


    }
}