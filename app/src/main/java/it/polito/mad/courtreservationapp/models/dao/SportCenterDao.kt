package it.polito.mad.courtreservationapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User

@Dao
interface SportCenterDao {
    @Query("SELECT * FROM sportCenters")
    fun getAll(): LiveData<List<SportCenter>>

    @Query("SELECT * FROM users WHERE centerId = :centerId")
    fun getById(centerId: Int): LiveData<SportCenter>

    @Insert(onConflict = REPLACE)
    fun save(center: SportCenter)

    @Delete
    fun delete(center: SportCenter)
}