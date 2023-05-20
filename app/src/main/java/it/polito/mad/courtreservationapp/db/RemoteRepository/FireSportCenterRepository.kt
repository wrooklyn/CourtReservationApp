package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
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

    fun getAllWithCourtsAndServices(): Task<List<SportCenterWithCourtsAndServices>> {
        val db: FirebaseFirestore = RemoteDataSource.instance
        val dataList = mutableListOf<SportCenterWithCourtsAndServices>()
        // Reference to your Firestore collection
        val sportCenterRef = db.collection("sport-centers")
        val innerTasks = mutableListOf<Task<*>>()
        return sportCenterRef.get().continueWithTask { task ->
            val sportCenterSnapshot = task.result
            for (document in sportCenterSnapshot?.documents.orEmpty()) {
                // Map Firestore document to YourDataModel and add it to dataList
                println("help")
                println(document.id)
                println(document.data)
                val scName: String = document.data?.get("name") as String
                val scAddress = document.data?.get("address") as String
                val scDescription = document.data?.get("description") as String
                val scId = document.data?.get("id") as Long
                val sportCenter = SportCenter(scName, scAddress, scDescription, scId)
                val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
                val courtWithServices = mutableListOf<CourtWithServices>()

                val innerTask =courtsOfCenterRef.get().continueWithTask { task ->
                    val courtSnapshot = task.result
                    for (courtDocument in courtSnapshot?.documents.orEmpty()) {
                        println("xp ${courtDocument.data}")
                        val cSportName = courtDocument.data?.get("sport_name") as String
                        val cCourtId = 19L
                        val cServices = courtDocument.data?.get("services") as List<Long>?
                        val s = cServices?.map { e -> Service("desct temporary", e) } ?: listOf()
                        val c = Court(scId, cSportName, 0, cCourtId)
                        courtWithServices.add(CourtWithServices(c, s))
                    }
                    dataList.add(SportCenterWithCourtsAndServices(sportCenter, courtWithServices))
                    Tasks.forResult(dataList)
                }
                innerTasks.add(innerTask)
            }
            Tasks.whenAllComplete(innerTasks).continueWith { _ ->
                dataList
            }
        }
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