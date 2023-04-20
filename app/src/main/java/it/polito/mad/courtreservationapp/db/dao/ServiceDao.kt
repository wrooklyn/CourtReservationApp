package it.polito.mad.courtreservationapp.db.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Service

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services")
    fun getAll(): LiveData<List<Service>>

    @Query("SELECT * FROM services WHERE serviceId = :serviceId ")
    fun getById(serviceId: Int): LiveData<Service>

    @Insert(onConflict = REPLACE)
    suspend fun save(service: Service)

    @Delete
    suspend fun delete(service: Service)
}