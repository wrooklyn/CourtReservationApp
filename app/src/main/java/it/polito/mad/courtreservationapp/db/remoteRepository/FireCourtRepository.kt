package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.CourtWithSportCenter
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.utils.DbUtils
//import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.utils.ServiceUtils
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
//        val sportName = courtDoc.data?.get("sport_name") as String
//        val image: String? = courtDoc.data?.get("image_name") as String?
//        val cost: Double = courtDoc.data?.get("cost") as Double
//        val courtItem = Court(centerId, sportName, 0, courtId, cost, image)
        val courtItem = DbUtils.getCourt(courtDoc, centerId)

        val cServices = courtDoc.data?.get("services") as List<*>?
        val s = cServices?.let { ServiceUtils.getServices(it) } ?: emptyList()

        return CourtWithServices(courtItem, s)
    }

    fun observeCourtWithReservations(centerId: String, courtId: String): MutableLiveData<CourtWithReservations> {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val courtRef =
            db.collection("sport-centers").document(centerId).collection("courts").document(courtId)
        return object : MutableLiveData<CourtWithReservations>() {
            private var listenerRegistration: ListenerRegistration? = null

            override fun onActive() {
                super.onActive()
                listenerRegistration = courtRef.addSnapshotListener { courtSnapshot, error ->
                    if (error != null) {
                        // Handle the error
                        return@addSnapshotListener
                    }

                    courtSnapshot?.let { snapshot ->
//                        val sportName = snapshot.data?.get("sport_name") as String
//                        val image: String? = snapshot.data?.get("image_name") as String?
//                        val cost: Double = snapshot.data?.get("cost") as Double
//                        val courtItem = Court(centerId, sportName, 0, courtId, cost, image)
                        val courtItem = DbUtils.getCourt(snapshot, centerId)
                        val reservationsRef = snapshot.reference.collection("reservations")
                        reservationsRef.get().addOnSuccessListener { reservationsSnapshot ->
                            val reservations = reservationsSnapshot.documents.mapNotNull {
                                val reservDate: String = it.data?.get("date") as String
                                val request: String? = it.data?.get("request") as String?
                                val timeslotId: Long = it.data?.get("timeslot") as Long
                                Reservation(reservDate, timeslotId, null, centerId, request, it.id)
                            }
                            val courtWithReservations = courtItem?.let { CourtWithReservations(it, reservations) }
                            value = courtWithReservations
                        }.addOnFailureListener { exception ->
                            // Handle the exception while fetching reservations
                        }
                    }
                }
            }

            override fun onInactive() {
                super.onInactive()
                listenerRegistration?.remove()
            }
        }
    }

    fun getByIdWithSportCenter(
        courtId: String,
        sportCenterId: String,
        callback: (CourtWithSportCenter?) -> Unit
    ) {
        val db = RemoteDataSource.instance
        db.collection("sport-centers").document(sportCenterId).get()
            .addOnSuccessListener { centerSnapshot ->
                if (centerSnapshot.exists()) {
                    val centerName = centerSnapshot.data?.get("name") as String
                    val address = centerSnapshot.data?.get("address") as String
                    val description = centerSnapshot.data?.get("description") as String
                    val centerImage = centerSnapshot.data?.get("image_name") as String?
                    val centerItem = SportCenter(centerName, address, description, sportCenterId, centerImage)
                    db.collection("sport-centers")
                        .document(sportCenterId)
                        .collection("courts")
                        .document(courtId)
                        .get()
                        .addOnSuccessListener { courtSnapshot ->
                            if (courtSnapshot.exists()) {
//                                val sportName = courtSnapshot.data?.get("sport_name") as String
//                                val courtImage = courtSnapshot.data?.get("image_name") as String
//                                val cost: Double = courtSnapshot.data?.get("cost") as Double
//                                val courtItem = Court(sportCenterId, sportName, 0, courtId, cost, courtImage)
                                val courtItem = DbUtils.getCourt(courtSnapshot, sportCenterId)
                                val result = CourtWithSportCenter(courtItem, centerItem)
                                callback(result)
                            } else {
                                println("Could not find court with ID: $courtId in center with ID: $sportCenterId")
                                callback(null)
                            }
                        }
                } else {
                    println("Could not find center with ID: $sportCenterId")
                    callback(null)
                }
            }
    }


    suspend fun getByIdWithReservations(centerId: String, courtId: String): CourtWithReservations {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val reservList: MutableList<Reservation> = mutableListOf()
        val courtDoc =
            db.collection("sport-centers").document(centerId).collection("courts").document(courtId)
                .get().await()
//        val sportName = courtDoc.data?.get("sport_name") as String
//        val image: String? = courtDoc.data?.get("image_name") as String?
//        val cost: Double = courtDoc.data?.get("cost") as Double
//        val courtItem = Court(centerId, sportName, 0, courtId, cost, image)
        val courtItem = DbUtils.getCourt(courtDoc, centerId)
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