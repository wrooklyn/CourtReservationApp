package it.polito.mad.courtreservationapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import it.polito.mad.courtreservationapp.models.Sport

@Dao
interface SportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(sport: Sport): Long

    @Delete
    suspend fun delete(sport: Sport)

}