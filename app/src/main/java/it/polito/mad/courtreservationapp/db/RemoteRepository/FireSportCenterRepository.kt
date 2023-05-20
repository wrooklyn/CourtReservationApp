package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.models.SportCenter

class FireSportCenterRepository(val application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val sportCenterDao: SportCenterDao = db.sportCenterDao()

    suspend fun insertCenter(sportCenter: SportCenter) {
        sportCenterDao.save(sportCenter)
    }

    suspend fun deleteCenter(sportCenter: SportCenter) {
        sportCenterDao.delete(sportCenter)
    }

    fun getAll(): LiveData<List<SportCenter>> {
        return sportCenterDao.getAll()
    }

    fun getById(id: Long): LiveData<SportCenter> {
        return sportCenterDao.getById(id)
    }

    fun getAllWithCourts(): LiveData<List<SportCenterWithCourts>> {
        return sportCenterDao.getAllWithCourts()
    }

    fun getCenterWithCourts(id: Long): LiveData<SportCenterWithCourts> {
        return sportCenterDao.getByIdWithCourts(id)
    }

    fun getAllWithCourtsAndReservations(): LiveData<List<SportCenterWithCourtsAndReservations>> {
        return sportCenterDao.getAllWithCourtsAndReservations()
    }

    fun getCenterWithCourtsAndReservations(id: Long): LiveData<SportCenterWithCourtsAndReservations> {
        return sportCenterDao.getByIdWithCourtsAndReservations(id)
    }

    fun getAllWithCourtsAndServices(): LiveData<List<SportCenterWithCourtsAndServices>> {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val liveData = MutableLiveData<List<SportCenterWithCourtsAndServices>>()

        // Reference to your Firestore collection
        val sportCenterRef = db.collection("sport-centers")
        sportCenterRef.addSnapshotListener { sportCenterSnapshot, exception ->
            if (exception != null) {
                // Handle any errors
                // e.g., liveData.value = emptyList() or liveData.value = null
                return@addSnapshotListener
            }

            val dataList = mutableListOf<SportCenterWithCourtsAndServices>()
            for (document in sportCenterSnapshot?.documents.orEmpty()) {
                // Map Firestore document to YourDataModel and add it to dataList
                println("help")
                println(document.id)
                println(document.data)
                val sc_name: String = document.data?.get("name") as String
                val sc_address = document.data?.get("address") as String
                val sc_description = document.data?.get("description") as String
                val sc_id =  document.data?.get("id") as Long
                val sportCenter = SportCenter(sc_name, sc_address, sc_description, sc_id)
                val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
                val courtWithServices = mutableListOf<CourtWithServices>()
                courtsOfCenterRef.addSnapshotListener { courtSnapshot, exception ->
                    if (exception != null) {
                        // Handle any errors
                        // e.g., liveData.value = emptyList() or liveData.value = null
                        return@addSnapshotListener
                    }
                    for (courtDocument in courtSnapshot?.documents.orEmpty()) {
                        println("xp ${courtDocument.data}")
                        val c_sport_name = courtDocument.data?.get("sport_name") as String
                        val c_court_id=19L
                        val c_services = courtDocument.data?.get("services") as List<Long>?
                        val s = c_services?.map { e -> Service("desct temporary", e) } ?: listOf()
                        val c = Court(sc_id,c_sport_name,0,  c_court_id)
                        courtWithServices.add(CourtWithServices(c, s))
                    }
                    dataList.add(SportCenterWithCourtsAndServices(sportCenter, courtWithServices))
                }


            }

            liveData.value = dataList
        }

        //return liveData
        return liveData
    }

    fun getCenterWithCourtsAndServices(id: Long): LiveData<SportCenterWithCourtsAndServices> {
        return sportCenterDao.getByIdWithCourtsAndServices(id)
    }

    fun getAllWithCourtsAndReservationsAndServices(): LiveData<List<SportCenterWithCourtsAndReservationsAndServices>> {
        return sportCenterDao.getAllWithCourtsAndReservationsAndServices()
    }

    fun getCenterWithCourtsAndReservationsAndServices(id: Long): LiveData<SportCenterWithCourtsAndReservationsAndServices> {
        return sportCenterDao.getByIdWithCourtsAndReservationsAndServices(id)
    }

    fun getAllWithCourtsAndReviews(): LiveData<List<SportCenterWithCourtsAndReviews>> {
        return sportCenterDao.getAllWithCourtsAndReviews()
    }

    fun getAllWithCourtsAndReviewsAndUsers(): LiveData<List<SportCenterWIthCourtsAndReviewsAndUsers>> {
        return sportCenterDao.getAllWithCourtsAndReviewsAndUsers()
    }

    fun getCenterWithCourtsAndReviews(id: Long): LiveData<SportCenterWithCourtsAndReviews> {
        return sportCenterDao.getByIdWithCourtsAndReviews(id)
    }

}