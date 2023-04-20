package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court

@Dao
interface CourtDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(vararg courts: Court)

    @Delete
    suspend fun delete(vararg courts: Court)

    @Query("SELECT * FROM courts")
    fun getAll(): LiveData<List<Court>>

    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getById(courtId: Int): LiveData<Court>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithServices(): LiveData<List<CourtWithServices>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithServices(courtId: Int): LiveData<CourtWithServices>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithReservations(): LiveData<List<CourtWithReservations>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithReservations(courtId: Int): LiveData<CourtWithReservations>

    @Transaction
    @Query("SELECT * FROM courts")
    fun getAllWithReservationsAndServices(): LiveData<List<CourtWithReservationsAndServices>>

    @Transaction
    @Query("SELECT * FROM courts WHERE courtId = :courtId ")
    fun getByIdWithReservationsAndServices(courtId: Int): LiveData<CourtWithReservationsAndServices>

}