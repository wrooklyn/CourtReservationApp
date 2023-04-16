package it.polito.mad.courtreservationapp.models.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.User

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations")
    fun getAll(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE userId = :userId")
    fun getAllByUser(userId: Int): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations " +
            "WHERE centerId = :centerId" +
            "  AND courtId = :courtId")
    fun getAllByCourt(centerId: Int, courtId: Int)

    @Query("SELECT * FROM reservations " +
            "WHERE centerId = :centerId" +
            "  AND courtId = :courtId" +
            "  AND userId = :userId")
    fun getAllByCourtUser(centerId: Int, courtId: Int, userId: Int)

    @Insert(onConflict = REPLACE)
    fun save(center: Reservation)

    @Delete
    fun delete(center: Reservation)
}