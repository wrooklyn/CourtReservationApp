package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.SportMasteryWithName
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.db.relationships.UserWithReservations
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteriesAndName
import it.polito.mad.courtreservationapp.models.Sport
import it.polito.mad.courtreservationapp.models.SportMastery
import kotlinx.coroutines.tasks.await

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
        return userDao.getById(1)
    }

    fun getUserReservations(userId: Long): LiveData<UserWithReservations>{
        return userDao.getByIdWithReservations(userId)
    }

    suspend fun getUserWithMasteries(email: String): UserWithSportMasteriesAndName{
//        return userDao.getByIdWithSportMasteries(1)
        val db: FirebaseFirestore = RemoteDataSource.instance
        val userDoc = db.collection("users").document(email).get().await()
        val username = userDoc.data?.get("username") as String
        val first_name = userDoc.data?.get("first_name") as String
        val last_name = userDoc.data?.get("last_name") as String
        val address = userDoc.data?.get("address") as String

        val user = User(username, first_name, last_name, email, address, 0, 0, 0, "", 0L)
        val masterySnap = db.collection("users").document(email).collection("mastery").get().await()
        val masteries = mutableListOf<SportMasteryWithName>()
        for(mastery in masterySnap){
            val sportMastery = SportMastery(0L, 0L, mastery.data?.get("level") as Int, mastery.data?.get("achievement") as String)
            val sport = Sport(mastery.id, 0L)
            val sportMasteryWithName = SportMasteryWithName(sportMastery, sport)
            masteries.add(sportMasteryWithName)
        }
        return UserWithSportMasteriesAndName(user, masteries)
    }
}