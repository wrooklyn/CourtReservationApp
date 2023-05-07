package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.repository.CourtRepository
import it.polito.mad.courtreservationapp.db.repository.ReservationRepository
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository
import it.polito.mad.courtreservationapp.db.repository.UserRepository
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity


class LeaveRatingViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "LeaveRatingViewModel"
    //private val reviewRepo: ReviewRepository = ReviewRepository(application)
    private val sportCenterRepo: SportCenterRepository = SportCenterRepository(application)

    lateinit var sportCenterWithCourtsLiveData: LiveData<SportCenterWithCourts>

    //display
    var sportCenterName = mutableStateOf("")
    lateinit var courtName: String

    //data from caller
    var courtId: Long = 0
    var sportCenterId: Long = 0
    var reservationId: Long = 0

    //new data
    var selectedRating : Int = 0
    var reviewText : String = ""
    var selectedImprovements : MutableList<Int> = mutableListOf()


    fun submitRating(){
        Log.v(tag, "$selectedRating")
        Log.v(tag, "$reviewText")
        Log.v(tag, "$selectedImprovements")
    }

    fun init( ctx: LeaveRatingActivity){
        sportCenterWithCourtsLiveData = sportCenterRepo.getCenterWithCourts(sportCenterId)
    }

}