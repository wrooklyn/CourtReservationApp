package it.polito.mad.courtreservationapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.User

@Dao
interface CourtDao {
    @Query("SELECT * FROM courts")
    fun getAll(): LiveData<List<Court>>

    @Query("SELECT * FROM users " +
            "WHERE centerId = :centerId " +
            "  AND courtId = :courtId ")
    fun getById(centerId: Int, courtId: Int): LiveData<Court>

    @Insert(onConflict = REPLACE)
    fun save(court: Court)

    @Delete
    fun delete(court: Court)
}