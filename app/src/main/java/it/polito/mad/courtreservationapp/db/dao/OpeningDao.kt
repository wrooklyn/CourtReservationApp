package it.polito.mad.courtreservationapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Opening

@Dao
interface OpeningDao {
    @Query("SELECT * FROM sportCenters")
    fun getAll(): LiveData<List<Opening>>

    @Insert(onConflict = REPLACE)
    suspend fun save(center: Opening)

    @Delete
    suspend fun delete(center: Opening)
}