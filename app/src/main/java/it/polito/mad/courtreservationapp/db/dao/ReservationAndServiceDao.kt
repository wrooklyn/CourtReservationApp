package it.polito.mad.courtreservationapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef

@Dao
interface ReservationAndServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(reservationServiceCrossRef: ReservationServiceCrossRef)

    @Delete
    suspend fun delete(reservationServiceCrossRef: ReservationServiceCrossRef)
}