package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.CourtDao
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviews
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

    fun getAll(): LiveData<List<Court>> {
        return courtDao.getAll()
    }

    fun getById(id: Long): LiveData<Court>{
        return courtDao.getById(id)
    }

    fun getAllWithServices(): LiveData<List<CourtWithServices>>{
        return courtDao.getAllWithServices()
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



}