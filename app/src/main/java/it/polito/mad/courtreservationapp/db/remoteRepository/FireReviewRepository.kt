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
    val db: FirebaseFirestore = RemoteDataSource.instance

    suspend fun insertReview(sportCenterId:String, courtId : String, username:String, reservationId :String, reviewText:String, selectedRating:Int, dateStr:String){

        val updates = hashMapOf<String, Any>()
        updates["rating"] = selectedRating
        updates["review_date"] = dateStr
        if(reviewText.isNotEmpty())
            updates["review_content"] = reviewText

        val reviewDocQuery = db.collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(courtId).collection("reservations")
            .whereEqualTo("reservationId", reservationId).get().await()
        val docId = reviewDocQuery.documents.first().id
        val reviewDocRef = db.collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(courtId).collection("reservations")
            .document(docId)

        val reviewDocRef2 = db.collection("reservations").document(reservationId)


        reviewDocRef.update(updates).await()
        reviewDocRef2.update(updates).await()
    }



    fun getAll(): LiveData<List<Review>> {
        return MutableLiveData<List<Review>>();
    }

    fun getById(id: Long): LiveData<Review>{
        return MutableLiveData<Review>();
    }


}