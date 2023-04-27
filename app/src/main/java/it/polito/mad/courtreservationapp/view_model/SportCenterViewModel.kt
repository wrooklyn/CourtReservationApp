package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter
import kotlinx.coroutines.launch

class SportCenterViewModel(application: Application): AndroidViewModel(application) {

    private val sportCenterRepository: SportCenterRepository = SportCenterRepository(application)
    lateinit var sportCenter: LiveData<SportCenter>
    lateinit var sportCenterCourts: LiveData<List<Court>>
    suspend fun getAll(): LiveData<List<SportCenter>> {
        return sportCenterRepository.getAll()
    }


}