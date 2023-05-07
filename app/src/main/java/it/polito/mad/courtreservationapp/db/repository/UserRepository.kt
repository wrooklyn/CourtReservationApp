package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.db.dao.UserDao
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteries

//import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations

class UserRepository(private val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val userDao = db.userDao()

    suspend fun insertUser(user: User){
        userDao.save(user)
    }

    suspend fun deleteUser(user: User){
        userDao.delete(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    fun getAll(): LiveData<List<User>>{
        return userDao.getAll()
    }

    fun getById(userId: Long): LiveData<User>{
        return userDao.getById(userId)
    }

    fun getUserReservations(userId: Long): LiveData<UserWithReservations>{
        return userDao.getByIdWithReservations(userId)
    }

    fun getUserWithMasteries(userId: Long): LiveData<UserWithSportMasteries>{
        return userDao.getByIdWithSportMasteries(userId)
    }
}