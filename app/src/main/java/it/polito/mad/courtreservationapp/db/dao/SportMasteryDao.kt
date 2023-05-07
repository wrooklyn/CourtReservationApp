package it.polito.mad.courtreservationapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import it.polito.mad.courtreservationapp.models.SportMastery

@Dao
interface SportMasteryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(sportMastery: SportMastery): Long

    @Delete
    suspend fun delete(sportMastery: SportMastery)
}