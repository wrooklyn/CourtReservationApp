package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservations
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.SportCenter

@Dao
interface SportCenterDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(center: SportCenter): Long
    @Insert(onConflict = REPLACE)
    suspend fun save(vararg centers: SportCenter): Array<Long>

    @Delete
    fun delete(vararg centers: SportCenter)

    @Query("SELECT * FROM sportCenters")
    fun getAll(): LiveData<List<SportCenter>>

    @Query("SELECT * FROM sportCenters WHERE centerId = :centerId")
    fun getById(centerId: Long): LiveData<SportCenter>

    @Transaction
    @Query("SELECT * FROM sportCenters")
    fun getAllWithCourts(): LiveData<List<SportCenterWithCourts>>

    @Transaction
    @Query("SELECT * FROM sportCenters WHERE centerId = :centerId")
    fun getByIdWithCourts(centerId: Long): LiveData<SportCenterWithCourts>

    @Transaction
    @Query("SELECT * FROM sportCenters")
    fun getAllWithCourtsAndServices(): LiveData<List<SportCenterWithCourtsAndServices>>

    @Transaction
    @Query("SELECT * FROM sportCenters WHERE centerId = :centerId")
    fun getByIdWithCourtsAndServices(centerId: Long): LiveData<SportCenterWithCourtsAndServices>

    @Transaction
    @Query("SELECT * FROM sportCenters")
    fun getAllWithCourtsAndReservations(): LiveData<List<SportCenterWithCourtsAndReservations>>

    @Transaction
    @Query("SELECT * FROM sportCenters WHERE centerId = :centerId")
    fun getByIdWithCourtsAndReservations(centerId: Long): LiveData<SportCenterWithCourtsAndReservations>

    @Transaction
    @Query("SELECT * FROM sportCenters")
    fun getAllWithCourtsAndReservationsAndServices(): LiveData<List<SportCenterWithCourtsAndReservationsAndServices>>

    @Transaction
    @Query("SELECT * FROM sportCenters WHERE centerId = :centerId")
    fun getByIdWithCourtsAndReservationsAndServices(centerId: Long): LiveData<SportCenterWithCourtsAndReservationsAndServices>

}