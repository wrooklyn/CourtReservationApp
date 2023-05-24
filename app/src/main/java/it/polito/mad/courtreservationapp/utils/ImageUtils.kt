package it.polito.mad.courtreservationapp.utils

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import java.io.File

object ImageUtils {
    fun setImage(path: String, imageSrc: String?, imageView: ImageView){
        val storage = RemoteDataSource.storageInstance

        if(imageSrc.isNullOrEmpty()){
            imageView.setImageResource(R.drawable.gesu)
        } else {
            val imageRef = storage.reference.child("centers/$imageSrc")
            val localFile = File.createTempFile("tempImage", "jpg")
            imageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                imageView.setImageBitmap(bitmap)
            }.addOnFailureListener{
                Log.i("BitMapUtil","$path/$imageSrc->$imageView | Error: $it")
            }
        }
    }
}