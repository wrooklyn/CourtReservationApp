package it.polito.mad.courtreservationapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import java.io.File
import java.io.IOException

object BitmapUtil {
    fun uriToBitmap(selectedFileUri: Uri, context: Context): Bitmap? {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}