package it.polito.mad.courtreservationapp.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.util.*

object DiskUtil {
    private const val dir: String = "CourtFolder"
    private const val tag: String = "DiskUtil"

    fun createFolderInt(context: Context){
        val root = File(context.getExternalFilesDir(null), dir)
        if(!root.exists()){
            val creationRes = root.mkdirs()
            if(creationRes){
                Log.i(tag, "Folder created in $root")
            } else {
                Log.e(tag, "Folder not created in $root")
            }
        } else {
            Log.i(tag, "Folder already present: $root")
        }
    }

    private fun getInternalFolder(context: Context): String {
        return File(context.getExternalFilesDir(null), dir).toString()
    }

    fun getUriFromPath(path: String): Uri? {
        try {
            val file = File(path)
            if(file.isFile){
                return Uri.fromFile(file)
            }
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        return null
    }

    fun getImagePathFromUri(imageUri: Uri, context: Context): String{
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(imageUri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val imagePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        return imagePath ?: ""
    }

    fun getFileFromPath(path: String): File? {
        try {
            val file = File(path)
            if(file.isFile){
                return file
            }
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        return null
    }

    fun getSaveFile(context: Context): File{
        val date = Calendar.getInstance().time
        val name = "$date.jpg"

        return File(getInternalFolder(context), name)

    }

    fun getDefaultImage(context: Context): File {
        return File(getInternalFolder(context))
    }

    fun deleteFile(path: String) {
        val file = File(path)
        file.delete()
    }
}