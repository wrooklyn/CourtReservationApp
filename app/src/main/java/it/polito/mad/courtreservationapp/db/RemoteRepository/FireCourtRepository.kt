package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
//import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service
import kotlinx.coroutines.tasks.await

class FireCourtRepository(val application: Application) {

    private val serviceMap: Map<Long, Service> = mapOf(
        Pair(0, Service("Safety shower", 0)),
        Pair(1, Service("Equipment", 1)),
        Pair(2, Service("Coach", 2)),
        Pair(3, Service("Refreshment", 3))
    )

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
     */

    suspend fun getByIdWithServices(centerId: String, courtId: String): CourtWithServices {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val courtDoc = db.collection("sport-centers").document(centerId).collection("courts").document(courtId).get().await()
        val sportName = courtDoc.data?.get("sport_name") as String
        val image: String? = courtDoc.data?.get("image_name") as String?
        val courtItem = Court(centerId, sportName, 0, courtId, image)
        val servicesIds = (courtDoc.data?.get("services") as ArrayList<*>?) ?: emptyList()
        val serviceList : MutableList<Service> = mutableListOf()
        for(id in servicesIds){
            val service = serviceMap[id]
            Log.i("FireCourtRepo", "ServId: $id, service: $service")
            if(service!=null)
                serviceList.add(service)
        }
        Log.i("FireCourtRepo", "Court: $courtId, services: $servicesIds to $serviceList")
        return CourtWithServices(courtItem, serviceList)
    }

    suspend fun getByIdWithReservations(centerId: String, courtId: String): CourtWithReservations {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val reservList: MutableList<Reservation> = mutableListOf()
        val courtDoc =
            db.collection("sport-centers").document(centerId).collection("courts").document(courtId)
                .get().await()
        val sportName = courtDoc.data?.get("sport_name") as String
        val image: String? = courtDoc.data?.get("image_name") as String?
        val courtItem = Court(centerId, sportName, 0, courtId, image)
        val reservsSnapshot =
            db.collection("sport-centers").document(centerId).collection("courts").document(courtId)
                .collection("reservations").get().await()
        if (reservsSnapshot != null) {
            for (reservation in reservsSnapshot.documents) {
                //val reservReference: DocumentReference =
                   // reservation.data?.get("ref") as DocumentReference? ?: continue
                //val reservItem = reservReference.get().await()
                val reservDate: String = reservation.data?.get("date") as String
                val request: String? = reservation.data?.get("request") as String?
                val timeslotId: Long = reservation.data?.get("timeslot") as Long
                val reservationItem = Reservation(reservDate, timeslotId, null, centerId, request, reservation.id)
                reservList.add(reservationItem)
            }
        }
        return CourtWithReservations(courtItem, reservList)
    }
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