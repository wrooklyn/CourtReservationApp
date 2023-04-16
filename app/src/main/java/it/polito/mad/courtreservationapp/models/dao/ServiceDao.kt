package it.polito.mad.courtreservationapp.models.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Service

@Dao
interface ServiceDao {
    @Query("SELECT * FROM sportCenters")
    fun getAll(): LiveData<List<Service>>

    @Query("SELECT * FROM services " +
            "WHERE centerId = :centerId " +
            "  AND courtId = :courtId " +
            "  AND serviceId = :serviceId ")
    fun getById(centerId: Int, courtId: Int, serviceId: Int): LiveData<Service>

    @Insert(onConflict = REPLACE)
    fun save(service: Service)

    @Delete
    fun delete(service: Service)
}