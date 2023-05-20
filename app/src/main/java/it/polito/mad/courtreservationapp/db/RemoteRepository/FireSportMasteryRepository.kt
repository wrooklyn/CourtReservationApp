package it.polito.mad.courtreservationapp.db.repository

import android.app.Application
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.models.SportMastery

class FireSportMasteryRepository(private val application: Application) {
    private val db: AppDatabase = AppDatabase.getDatabase(application)
    private val sportMasteryDao = db.sportMasteryDao()

    suspend fun insertSportMastery(sportMastery: SportMastery): Long{
        return sportMasteryDao.save(sportMastery)
    }

    suspend fun deleteSportMastery(sportMastery: SportMastery){
        return sportMasteryDao.delete(sportMastery)
    }

}