package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import it.polito.mad.courtreservationapp.models.Review

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(review: Review): Long

    @Delete
    suspend fun delete(review: Review)

    @Query("SELECT * FROM reviews")
    fun getAll(): LiveData<List<Review>>

    @Query("SELECT * FROM reviews WHERE reviewId = :reviewId ")
    fun getById(reviewId: Long): LiveData<Review>

    @Query("SELECT * FROM reviews WHERE reviewUserId = :userId ")
    fun getByUserId(userId: Long): LiveData<List<Review>>

    @Query("SELECT * FROM reviews WHERE reviewCourtId = :courtId ")
    fun getByCourtId(courtId: Long): LiveData<List<Review>>

    @Query("SELECT * FROM reviews WHERE reviewReservationId = :reservationId")
    fun getByReservationId(reservationId: Long): LiveData<Review>
}