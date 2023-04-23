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

    suspend fun insertCenter(sportCenter: SportCenter){
        sportCenterDao.save(sportCenter)
    }

    suspend fun deleteCenter(sportCenter: SportCenter){
        sportCenterDao.delete(sportCenter)
    }

    suspend fun getAll(): LiveData<List<SportCenter>>{
        sportCenters = sportCenterDao.getAll()
        return sportCenters
    }

    suspend fun getById(id: Long): LiveData<SportCenter>{
        return sportCenterDao.getById(id)
    }

    suspend fun getAllWithCourts(): LiveData<List<SportCenterWithCourts>>{
        sportCentersWithCourts = sportCenterDao.getAllWithCourts()
        return sportCentersWithCourts
    }

    suspend fun getCenterWithCourts(id: Long): LiveData<SportCenterWithCourts>{

        return sportCenterDao.getByIdWithCourts(id)
    }

    suspend fun getAllWithCourtsAndReservations(): LiveData<List<SportCenterWithCourtsAndReservations>>{
        return sportCenterDao.getAllWithCourtsAndReservations()
    }

    suspend fun getCenterWithCourtsAndReservations(id: Long): LiveData<SportCenterWithCourtsAndReservations>{
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