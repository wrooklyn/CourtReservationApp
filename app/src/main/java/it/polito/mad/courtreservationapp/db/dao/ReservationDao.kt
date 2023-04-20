package it.polito.mad.courtreservationapp.db.dao
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

    @Insert(onConflict = REPLACE)
    suspend fun save(vararg reservations: Reservation)

    @Delete
    suspend fun delete(vararg reservations: Reservation)

    @Query("SELECT * FROM reservations")
    fun getAll(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE reservationId = :reservationId")
    fun getById(reservationId: Int): LiveData<Reservation>
}