package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.dao.ReviewDao
import it.polito.mad.courtreservationapp.db.relationships.ReviewWithUser
import it.polito.mad.courtreservationapp.models.Review
import kotlinx.coroutines.tasks.await

class FireReviewRepository(val application: Application) {


    suspend fun insertReview(sportCenterId:String, courtId : String, username:String, reservationId :String, reviewText:String, selectedRating:Int, dateStr:String){
        val db: FirebaseFirestore = RemoteDataSource.instance

        val reviewDocRef = db.collection("reservations")
            .document(reservationId)

        val updates = hashMapOf<String, Any>()
        updates["rating"] = selectedRating
        updates["review_date"] = dateStr
        if(!reviewText.isNullOrEmpty())
            updates["review_content"] = reviewText
        reviewDocRef.update(updates).await()
    }



    fun getAll(): LiveData<List<Review>> {
        return MutableLiveData<List<Review>>();
    }

    fun getById(id: Long): LiveData<Review>{
        return MutableLiveData<Review>();
    }


}