package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court

@Dao
interface CourtDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(court: Court)

    @Delete
    suspend fun delete(court: Court)

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

}