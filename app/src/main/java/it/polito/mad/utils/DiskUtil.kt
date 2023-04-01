package it.polito.mad.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File

object DiskUtil {
    private const val dir: String = "CourtFolder"
    private const val tag: String = "DiskUtil"

    fun createRootFolderExt(){
        val root = File(Environment.getExternalStorageDirectory(), dir)
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(!root.exists()){
                val creationRes = root.mkdirs()
                if(creationRes){
                    Log.i(tag, "Folder created in $root")
                } else {
                    Log.e(tag, "Folder not created in $root")
                }
            } else {
                Log.i(tag, "Folder already present")
            }
        }
    }

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
            Log.i(tag, "Folder already present")
        }
    }

    fun getExternalFolder(): String {
        return File(Environment.getExternalStorageDirectory(), dir).toString()
    }

    fun getInternalFolder(context: Context): String {
        return File(context.getExternalFilesDir(null), dir).toString()
    }

}