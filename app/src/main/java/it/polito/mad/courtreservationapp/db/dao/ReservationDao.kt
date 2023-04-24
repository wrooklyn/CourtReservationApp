package it.polito.mad.courtreservationapp.db.dao
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.User

@Dao
interface ReservationDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(reservation: Reservation): Long
    @Insert(onConflict = REPLACE)
    suspend fun save(vararg reservations: Reservation): Array<Long>

    @Delete
    suspend fun delete(reservation: Reservation)

    @Query("SELECT * FROM reservations")
    fun getAll(): LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE reservationId = :reservationId")
    fun getById(reservationId: Long): LiveData<Reservation>

    @Query("SELECT * FROM reservations WHERE reservationUserId = :userId")
    fun getByUser(userId: Long): LiveData<List<Reservation>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE reservationUserId = :userId")
    fun getLocationsByUserId(userId: Long): LiveData<List<ReservationWithSportCenter>>
}