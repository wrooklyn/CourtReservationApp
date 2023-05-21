package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.repository.FireSportMasteryRepository
import kotlinx.coroutines.launch

class SportMasteryViewModel(application: Application): AndroidViewModel(application) {
    private val sportMasteryRepo = FireSportMasteryRepository(application)
    var email: String = ""
    var sport: String = ""
    var level: Int = 0
    var achievement: String? = null
    fun saveMastery(){
        viewModelScope.launch {
//            val sportMastery = SportMastery(sportId, email, level, achievement)
//            Log.i("SM", "Saving $sportMastery")
            sportMasteryRepo.insertSportMastery(email, sport, level, achievement)
        }
    }

}