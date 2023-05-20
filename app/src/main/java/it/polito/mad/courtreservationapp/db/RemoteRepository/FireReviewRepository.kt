package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.ReviewDao
import it.polito.mad.courtreservationapp.db.relationships.ReviewWithUser
import it.polito.mad.courtreservationapp.models.Review

class FireReviewRepository(val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val reviewDao: ReviewDao = db.reviewDao()

    suspend fun insertReview(review: Review){
        reviewDao.save(review)
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