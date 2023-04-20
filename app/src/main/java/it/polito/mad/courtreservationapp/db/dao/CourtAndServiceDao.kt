package it.polito.mad.courtreservationapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import it.polito.mad.courtreservationapp.db.crossref.CourtServiceCrossRef

@Dao
interface CourtAndServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(courtServiceCrossRef: CourtServiceCrossRef)

    @Delete
    suspend fun delete(courtServiceCrossRef: CourtServiceCrossRef)


}