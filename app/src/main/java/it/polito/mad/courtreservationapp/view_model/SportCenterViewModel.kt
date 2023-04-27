package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository

class SportCenterViewModel(application: Application): AndroidViewModel(application) {

    private val sportCenterRepository: SportCenterRepository = SportCenterRepository(application)

    lateinit var sportCentersLiveData: LiveData<List<SportCenterWithCourtsAndServices>>



    fun initialize(){
        Log.i("SportVM", "init")
        sportCentersLiveData = sportCenterRepository.getAllWithCourtsAndServices()
        Log.i("SportVM", "LiveData: $sportCentersLiveData")
    }
}