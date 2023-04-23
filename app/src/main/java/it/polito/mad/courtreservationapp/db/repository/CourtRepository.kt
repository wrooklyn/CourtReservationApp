package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.CourtDao
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
//import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.models.Court

class CourtRepository(private val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val courtDao = db.courtDao()

    suspend fun insertCourt(court: Court){
        courtDao.save(court)
    }

    suspend fun deleteCourt(court: Court){
        courtDao.delete(court)
    }

    suspend fun getAll(): LiveData<List<Court>> {
        return courtDao.getAll()
    }

    suspend fun getById(id: Long): LiveData<Court>{
        return courtDao.getById(id)
    }

    suspend fun getAllWithServices(): LiveData<List<CourtWithServices>>{
        return courtDao.getAllWithServices()
    }

    suspend fun getByIdWithServices(id: Long): LiveData<CourtWithServices>{
        return courtDao.getByIdWithServices(id)
    }

    suspend fun getAllWithReservations(): LiveData<List<CourtWithReservations>>{
        return courtDao.getAllWithReservations()
    }

    suspend fun getByIdWithReservations(id: Long): LiveData<CourtWithReservations>{
        return courtDao.getByIdWithReservations(id)
    }

}