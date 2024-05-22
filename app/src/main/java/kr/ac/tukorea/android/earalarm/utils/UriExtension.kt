package kr.ac.tukorea.android.earalarm.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

private fun Uri.getFileName(context: Context): String? {
    val contentResolver = context.contentResolver
    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(nameIndex)
    }
    return null
}

fun Uri.toPath(context: Context, deleteFile: File? = null): String? {
    val file = this.getFileName(context)?.let { File(context.filesDir, it) }
    val inputStream = context.contentResolver.openInputStream(this)
    val outputStream = FileOutputStream(file)

    inputStream.use { input ->
        outputStream.use { output ->
            input?.copyTo(output)
        }
    }

    file?.let {
        deleteFile?.delete()
    }

    return file?.absolutePath
}