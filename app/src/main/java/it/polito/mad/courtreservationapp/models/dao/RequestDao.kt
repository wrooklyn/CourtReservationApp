package it.polito.mad.courtreservationapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.Request

@Dao
interface RequestDao {
    @Query("SELECT * FROM requests")
    fun getAll(): LiveData<List<Request>>

    @Query("SELECT * FROM requests " +
            "WHERE requestId = :requestId " +
            "  AND userId = :userId" +
            "  AND centerId = :centerId" +
            "  AND courtId = :courtId" )
    fun getAllByCourtUser(requestId: Int, userId: Int, centerId: Int, courtId: Int)

    @Insert(onConflict = REPLACE)
    fun save(center: Request)

    @Delete
    fun delete(center: Request)
}