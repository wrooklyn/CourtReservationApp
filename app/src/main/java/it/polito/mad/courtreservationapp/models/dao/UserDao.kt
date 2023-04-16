package it.polito.mad.courtreservationapp.models.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.polito.mad.courtreservationapp.models.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getById(userId: Int): LiveData<User>

    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Delete
    fun delete(user: User)
}