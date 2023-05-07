package it.polito.mad.courtreservationapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteries
//import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.models.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(user: User): Long

    @Insert(onConflict = REPLACE)
    suspend fun save(vararg users: User): Array<Long>

    @Delete
    suspend fun delete(vararg users: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users")
    fun getAll(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getById(userId: Long): LiveData<User>

    @Query("SELECT * FROM users")
    fun getAllWithReservations(): LiveData<List<UserWithReservations>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getByIdWithReservations(userId: Long): LiveData<UserWithReservations>

    @Query("SELECT * FROM users")
    fun getAllWithReservationsAndServices(): LiveData<List<UserWithReservationsAndServices>>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getByIdWithReservationsAndServices(userId: Long): LiveData<UserWithReservationsAndServices>

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getByIdWithSportMasteries(userId: Long): LiveData<UserWithSportMasteries>
}