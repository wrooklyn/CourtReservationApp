package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.db.dao.UserDao
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

    fun getAll(): LiveData<List<User>>{
        return userDao.getAll()
    }

    fun getById(userId: Long): LiveData<User>{
        return userDao.getById(userId)
    }

//    suspend fun getAllWithReservations(): LiveData<List<UserWithReservations>>{
//        return userDao.getAllWithReservations()
//    }
//
//    suspend fun getByIdWithReservations(user: User): LiveData<UserWithReservations>{
//        return userDao.getByIdWithReservations(user.userId)
//    }
}