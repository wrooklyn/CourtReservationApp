package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.dao.ReviewDao
import it.polito.mad.courtreservationapp.db.relationships.ReviewWithUser
import it.polito.mad.courtreservationapp.models.Review
import kotlinx.coroutines.tasks.await

class FireReviewRepository(val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val reviewDao: ReviewDao = db.reviewDao()

    suspend fun insertReview(sportCenterId:String, courtId : String, username:String, reservationId :String, reviewText:String, selectedRating:Int, dateStr:String){
        val db: FirebaseFirestore = RemoteDataSource.instance

        val reviewDocRef = db.collection("sport-centers")
            .document(sportCenterId)
            .collection("courts")
            .document(courtId)
            .collection("reservations")
            .document(reservationId)

        val updates = hashMapOf<String, Any>()
        updates["rating"] = selectedRating
        updates["review_date"] = dateStr
        if(!reviewText.isNullOrEmpty())
            updates["review_content"] = reviewText
        reviewDocRef.update(updates).await()
    }

    suspend fun deleteReview(review: Review){
        reviewDao.delete(review)
    }

    fun getAll(): LiveData<List<Review>> {
        return reviewDao.getAll()
    }

    fun getById(id: Long): LiveData<Review>{
        return reviewDao.getById(id)
    }

    fun getByUserId(id: Long): LiveData<List<Review>>{
        return  reviewDao.getByUserId(id)
    }

    fun getByCourtId(id: Long): LiveData<List<Review>>{
        return reviewDao.getByCourtId(id)
    }

    fun getAllWithUser(): LiveData<List<ReviewWithUser>>{
        return  reviewDao.getAllWithUser()
    }
}