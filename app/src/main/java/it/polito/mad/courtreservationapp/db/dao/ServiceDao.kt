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
    @Insert(onConflict = REPLACE)
    suspend fun save(service: Service): Long
    @Insert(onConflict = REPLACE)
    suspend fun save(vararg services: Service): Array<Long>

    @Delete
    suspend fun delete(vararg services: Service)
    @Query("SELECT * FROM services")
    fun getAll(): LiveData<List<Service>>

    @Query("SELECT * FROM services WHERE serviceId = :serviceId ")
    fun getById(serviceId: Long): LiveData<Service>


}