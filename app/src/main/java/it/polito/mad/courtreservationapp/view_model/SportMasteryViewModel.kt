package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.repository.FireSportMasteryRepository
import it.polito.mad.courtreservationapp.models.SportMastery
import kotlinx.coroutines.launch

class SportMasteryViewModel(application: Application): AndroidViewModel(application) {
    private val sportMasteryRepo = FireSportMasteryRepository(application)
    var userId: Long = 0
    var sportId: Long = 0
    var level: Int = 0
    var achievement: String = ""
    fun saveMastery(){
        viewModelScope.launch {
            val sportMastery = SportMastery(sportId, userId, level, achievement)
            Log.i("SM", "Saving $sportMastery")
            sportMasteryRepo.insertSportMastery(sportMastery)
        }
    }

}