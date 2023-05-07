package it.polito.mad.courtreservationapp.view_model

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.UserWithSportMasteries
import it.polito.mad.courtreservationapp.db.repository.SportMasteryRepository
import it.polito.mad.courtreservationapp.db.repository.UserRepository
import it.polito.mad.courtreservationapp.models.SportMastery
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import kotlinx.coroutines.launch

class SportMasteryViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val sportMasteryRepo = SportMasteryRepository(application)

    lateinit var context: Context
    lateinit var userWithSportMasteriesLiveData: LiveData<UserWithSportMasteries>
    lateinit var userWithSportMasteries: UserWithSportMasteries

    fun init(context: MainActivity, userId: Long){
        this.context = context
        userWithSportMasteriesLiveData = userRepository.getUserWithMasteries(userId)
        userWithSportMasteriesLiveData.observe(context){
            userWithSportMasteries = it
            Log.i("sportMastery", "$it")
        }
    }

    fun saveMastery(){
        viewModelScope.launch {
            val sportMastery = SportMastery(1, 1, 1, "W")
            sportMasteryRepo.insertSportMastery(sportMastery)
        }
    }

}