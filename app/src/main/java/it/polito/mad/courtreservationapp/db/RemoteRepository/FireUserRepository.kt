package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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






    suspend fun updateUser(user: User) {
//        userDao.updateUser(user)
        val db: FirebaseFirestore = RemoteDataSource.instance
//        val content = hashMapOf(
//            "level" to level
//        )

        db.collection("users").document(user.email).set(user, SetOptions.merge())
    }





    suspend fun getUserWithMasteries(email: String): UserWithSportMasteriesAndName{
//        return userDao.getByIdWithSportMasteries(1)
        val db: FirebaseFirestore = RemoteDataSource.instance
        val userDoc = db.collection("users").document(email).get().await()
        val username = userDoc.data?.get("username") as String? ?: ""
        val firstName = userDoc.data?.get("firstName") as String? ?: ""
        val lastName = userDoc.data?.get("lastName") as String? ?: ""
        val address = userDoc.data?.get("address") as String? ?: ""
        val gender = userDoc.data?.get("gender") as Long? ?: 0L
        val height = userDoc.data?.get("height") as Long? ?: 0L
        val weight = userDoc.data?.get("weight") as Long? ?: 0L
        val phone = userDoc.data?.get("phone") as String? ?: ""

        val user = User(username, firstName, lastName, email, address, gender.toInt(), height.toInt(), weight.toInt(), phone, email)
        Log.i("getUser", "$user")
        val masterySnap = db.collection("users").document(email).collection("mastery").get().await()
        val masteries = mutableListOf<SportMasteryWithName>()
        for(mastery in masterySnap){
            Log.i("FireUserRepo", "i'm reading $mastery")
            val achievements = mastery.data?.get("achievements")  as ArrayList<String>
            Log.i("FireUserRepo", "achievements $achievements")
            val string =
                if(achievements.isNullOrEmpty()) ""
                else {
                    achievements.filter { s -> !s.isNullOrEmpty() }.toString().replace("[", "").replace("]", "")
                }
            Log.i("FireUserRepo", "str $string")
            val sportMastery = SportMastery(0L, email, (mastery.data?.get("level") as Long).toInt(), string)
            Log.i("FireUserRepo", "sportMastery created: $sportMastery")
            val sport = Sport(mastery.id, 0L)
            val sportMasteryWithName = SportMasteryWithName(sportMastery, sport)
            masteries.add(sportMasteryWithName)
        }
        val a = UserWithSportMasteriesAndName(user, masteries)
        Log.i("FireUserRepo", "final $a")
        return a
    }
}