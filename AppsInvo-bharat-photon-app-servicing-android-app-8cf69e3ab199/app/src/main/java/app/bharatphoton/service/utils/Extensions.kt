package app.bharatphoton.service.utils


import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream

fun Context.convertContentFilePath_To_FilePath(uri: Uri): File? {
    try {
        val mimetype = this.contentResolver.getType(uri) ?: run {
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimetype)
        val tmpFile = if (mimetype?.startsWith("image/") == true) {
            File.createTempFile("tmp_image_file_${System.currentTimeMillis()}", ".$extension", this.cacheDir).apply {
                createNewFile()
            }
        } else {
            File.createTempFile("tmp_video_file_${System.currentTimeMillis()}", ".$extension", this.cacheDir).apply {
                createNewFile()
            }
        }
        //Writing to content file uri stream to new File.
        val op = FileOutputStream(tmpFile)
        op.write(this.contentResolver.openInputStream(uri)?.readBytes())
        op.close()
        return tmpFile
    }catch (e:Exception){
        e.message.mLog()
        return null
    }
}
