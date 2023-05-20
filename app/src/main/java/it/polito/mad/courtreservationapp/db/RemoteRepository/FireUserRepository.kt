package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName

//import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations

class FireUserRepository(private val application: Application) {
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

    fun getById(email: String): LiveData<User>{
//        return userDao.getById(email)
        val db: FirebaseFirestore = RemoteDataSource.instance
        val userLiveData = MutableLiveData<User>()
        val userRef = db.collection("users")
            .document(email).get().addOnSuccessListener {
//                println("user: ${it.data}")
                val username = it.data?.get("username") as String
                val first_name = it.data?.get("first_name") as String
                val last_name = it.data?.get("last_name") as String
                val address = it.data?.get("address") as String
                val user = User(username,first_name, last_name, email, address, 0, 0, 0, "", 0L)
                userLiveData.value = user
            }



        return userLiveData
    }

    fun getUserReservations(userId: Long): LiveData<UserWithReservations>{
        return userDao.getByIdWithReservations(userId)
    }

    fun getUserWithMasteries(email: String): LiveData<UserWithSportMasteriesAndName>{
        return userDao.getByIdWithSportMasteries(1)

    }
}