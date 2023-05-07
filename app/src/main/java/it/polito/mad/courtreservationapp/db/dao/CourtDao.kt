package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviews
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court

@Dao
interface CourtDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(court: Court): Long
    @Insert(onConflict = REPLACE)
    suspend fun save(vararg courts: Court): Array<Long>

    @Delete
    suspend fun delete(vararg courts: Court)

    @Query("SELECT * FROM courts")
    fun getAll(): LiveData<List<Court>>

    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getById(courtId: Long): LiveData<Court>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithServices(): LiveData<List<CourtWithServices>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithServices(courtId: Long): LiveData<CourtWithServices>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithReservations(): LiveData<List<CourtWithReservations>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithReservations(courtId: Long): LiveData<CourtWithReservations>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithReservationsAndServices(): LiveData<List<CourtWithReservationsAndServices>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithReservationsAndServices(courtId: Long): LiveData<CourtWithReservationsAndServices>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithReviews(): LiveData<List<CourtWithReviews>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithReviews(courtId: Long): LiveData<CourtWithReviews>

}