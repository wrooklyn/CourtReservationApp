package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservations
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.SportCenter

class SportCenterRepository(private val application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val sportCenterDao = db.sportCenterDao()

    private lateinit var sportCenters: LiveData<List<SportCenter>>
    private lateinit var sportCentersWithCourts: LiveData<List<SportCenterWithCourts>>
    private lateinit var sportCentersWithCourtsAndReservations: LiveData<List<SportCenterWithCourtsAndReservations>>
    private lateinit var sportCentersWithCourtsAndServices: LiveData<List<SportCenterWithCourtsAndServices>>
    private lateinit var sportCenterWithCourtsAndReservationsAndServices: LiveData<List<SportCenterWithCourtsAndReservationsAndServices>>

    val allSportCenters = sportCenterDao.getAll()
    suspend fun insertCenter(sportCenter: SportCenter){
        sportCenterDao.save(sportCenter)
    }

    suspend fun deleteCenter(sportCenter: SportCenter){
        sportCenterDao.delete(sportCenter)
    }

    fun getAll(): LiveData<List<SportCenter>>{
        return sportCenterDao.getAll()
    }

    fun getById(id: Long): LiveData<SportCenter>{
        return sportCenterDao.getById(id)
    }

    fun getAllWithCourts(): LiveData<List<SportCenterWithCourts>>{
        sportCentersWithCourts = sportCenterDao.getAllWithCourts()
        return sportCentersWithCourts
    }

    fun getCenterWithCourts(id: Long): LiveData<SportCenterWithCourts>{

        return sportCenterDao.getByIdWithCourts(id)
    }

    fun getAllWithCourtsAndReservations(): LiveData<List<SportCenterWithCourtsAndReservations>>{
        return sportCenterDao.getAllWithCourtsAndReservations()
    }

    fun getCenterWithCourtsAndReservations(id: Long): LiveData<SportCenterWithCourtsAndReservations>{
        return sportCenterDao.getByIdWithCourtsAndReservations(id)
    }

    suspend fun getAllWithCourtsAndServices(): LiveData<List<SportCenterWithCourtsAndServices>>{
        return sportCenterDao.getAllWithCourtsAndServices()
    }

    suspend fun getCenterWithCourtsAndServices(id: Long): LiveData<SportCenterWithCourtsAndServices>{
        return sportCenterDao.getByIdWithCourtsAndServices(id)
    }

    suspend fun getAllWithCourtsAndReservationsAndServices(): LiveData<List<SportCenterWithCourtsAndReservationsAndServices>>{
        return sportCenterDao.getAllWithCourtsAndReservationsAndServices()
    }

    suspend fun getCenterWithCourtsAndReservationsAndServices(id: Long): LiveData<SportCenterWithCourtsAndReservationsAndServices>{
        return sportCenterDao.getByIdWithCourtsAndReservationsAndServices(id)
    }

}