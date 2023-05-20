package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviews
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
//import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court

class FireCourtRepository() {
    private val db: FirebaseFirestore = RemoteDataSource.instance

/*
    fun getById(id: Long): LiveData<Court>{
        val liveData = MutableLiveData<Court>()

        // Reference to your Firestore collection
        val courtRef = db.collection("sport-centers")
            .document("courts")
            .collection("court12")
            .document("court12")
        collectionRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                // Handle any errors
                // e.g., liveData.value = emptyList() or liveData.value = null
                return@addSnapshotListener
            }

            val dataList = mutableListOf<Court>()
            for (document in snapshot?.documents.orEmpty()) {
                // Map Firestore document to YourDataModel and add it to dataList
                println("help")
                println(document.id)
                println(document.data)
            }

            liveData.value = dataList
        }

        return liveData
    }

    fun getByIdWithServices(id: Long): LiveData<CourtWithServices>{
        return courtDao.getByIdWithServices(id)
    }

    fun getByIdWithReservations(id: Long): LiveData<CourtWithReservations>{
        return courtDao.getByIdWithReservations(id)
    }


 */
/*
    suspend fun insertCourt(court: Court){
        courtDao.save(court)
    }

    suspend fun deleteCourt(court: Court){
        courtDao.delete(court)
    }



    fun getById(id: Long): LiveData<Court>{
        return courtDao.getById(id)
    }


    fun getByIdWithServices(id: Long): LiveData<CourtWithServices>{
        return courtDao.getByIdWithServices(id)
    }

    fun getAllWithReservations(): LiveData<List<CourtWithReservations>>{
        return courtDao.getAllWithReservations()
    }

    fun getByIdWithReservations(id: Long): LiveData<CourtWithReservations>{
        return courtDao.getByIdWithReservations(id)
    }

    fun getAllWithReviews(): LiveData<List<CourtWithReviews>>{
        return courtDao.getAllWithReviews()
    }

    fun getByIdWithReviews(id: Long): LiveData<CourtWithReviews>{
        return courtDao.getByIdWithReviews(id)
    }

*/

}