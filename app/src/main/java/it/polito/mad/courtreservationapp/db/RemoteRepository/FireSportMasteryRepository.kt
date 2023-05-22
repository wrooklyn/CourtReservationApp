package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.polito.mad.courtreservationapp.db.RemoteDataSource

class FireSportMasteryRepository(private val application: Application) {
//    private val db: AppDatabase = AppDatabase.getDatabase(application)
//    private val sportMasteryDao = db.sportMasteryDao()

    val db: FirebaseFirestore = RemoteDataSource.instance

    suspend fun insertSportMastery(email: String, sport: String, level: Int, achievement: String?){
        val content = hashMapOf(
            "level" to level
        )

        db.collection("users").document(email).collection("mastery").document(sport).set(content, SetOptions.merge())
        db.collection("users").document(email).collection("mastery").document(sport).update("achievements", FieldValue.arrayUnion(achievement ?: ""))
//        return sportMasteryDao.save(sportMastery)
    }

//    suspend fun deleteSportMastery(sportMastery: SportMastery){
//        return sportMasteryDao.delete(sportMastery)
//    }

}