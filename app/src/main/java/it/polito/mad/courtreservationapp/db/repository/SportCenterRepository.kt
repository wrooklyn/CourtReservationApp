package it.polito.mad.courtreservationapp.db.repository

import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.models.SportCenter

class SportCenterRepository(private val sportCenterDao: SportCenterDao) {
    suspend fun insertCenter(sportCenter: SportCenter){
        sportCenterDao.save(sportCenter)
    }

    suspend fun deleteCenter(sportCenter: SportCenter){
        sportCenterDao.delete(sportCenter)
    }

    suspend fun getAll(): LiveData<List<SportCenter>>{
        return sportCenterDao.getAll()
    }

    suspend fun getById(id: Long): LiveData<SportCenter>{
        return sportCenterDao.getById(id)
    }

    suspend fun getAllWithCourts(): LiveData<List<SportCenterWithCourts>>{
        return sportCenterDao.getAllWithCourts()
    }

    suspend fun getCenterWithCourts(id: Long): LiveData<SportCenterWithCourts>{
        return sportCenterDao.getByIdWithCourts(id)
    }

}