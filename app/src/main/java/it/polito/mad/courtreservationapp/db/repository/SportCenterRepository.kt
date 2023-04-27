package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservations
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.SportCenter

class SportCenterRepository(val application: Application) {

    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val sportCenterDao: SportCenterDao = db.sportCenterDao()

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
        return sportCenterDao.getAllWithCourts()
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

    fun getAllWithCourtsAndServices(): LiveData<List<SportCenterWithCourtsAndServices>>{
        Log.i("SportVM", "Repo")
        return sportCenterDao.getAllWithCourtsAndServices()
    }

    fun getCenterWithCourtsAndServices(id: Long): LiveData<SportCenterWithCourtsAndServices>{
        return sportCenterDao.getByIdWithCourtsAndServices(id)
    }

    fun getAllWithCourtsAndReservationsAndServices(): LiveData<List<SportCenterWithCourtsAndReservationsAndServices>>{
        return sportCenterDao.getAllWithCourtsAndReservationsAndServices()
    }

    fun getCenterWithCourtsAndReservationsAndServices(id: Long): LiveData<SportCenterWithCourtsAndReservationsAndServices>{
        return sportCenterDao.getByIdWithCourtsAndReservationsAndServices(id)
    }

}