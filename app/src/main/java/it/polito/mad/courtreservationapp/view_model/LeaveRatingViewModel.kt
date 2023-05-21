package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.repository.*
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LeaveRatingViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "LeaveRatingViewModel"
    private val reviewRepo: FireReviewRepository = FireReviewRepository(application)
    private val sportCenterRepo: FireSportCenterRepository = FireSportCenterRepository(application)

     val sportCenterWithCourtsLiveData: MutableLiveData<SportCenterWithCourts> = MutableLiveData()
    lateinit var context: LeaveRatingActivity

    //display
    var sportCenterName = mutableStateOf("")
    var isSubmitting = mutableStateOf(false)
    lateinit var courtName: String

    //data from caller
    var courtId: Long = 0
    var sportCenterId: Long = 0
    var reservationId: Long = 0
    var userId: Long = 0

    //new data
    var selectedRating : Int = 0
    var reviewText : String = ""
    var selectedImprovements : MutableList<Int> = mutableListOf()


    fun submitRating(){
        viewModelScope.launch {
            val today = LocalDateTime.now()
            val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateStr = today.format(df)

            Log.v(tag, "$selectedRating")
            Log.v(tag, "$reviewText")
            Log.v(tag, "$selectedImprovements")
            val review: Review = Review(courtId, userId, reservationId, reviewText, selectedRating, dateStr)
            reviewRepo.insertReview("A4pjoFykPhVSfpkfYUXK", "80A69RdLDZhzICaVa1qA", "Gabriel", "TeExQeFkvXgUrY3i30uA", reviewText, selectedRating, dateStr)
            context.finish()
        }
    }

    fun init( ctx: LeaveRatingActivity){
        //sportCenterWithCourtsLiveData = sportCenterRepo.getCenterWithCourts(sportCenterId)
        //sportCenterWithCourtsLiveData = sportCenterRepo.getCenterWithCourts(sportCenterId)
        runBlocking(Dispatchers.Default) {
            launch {
                val res = sportCenterRepo.getCenterWithCourts2("A4pjoFykPhVSfpkfYUXK")
                sportCenterWithCourtsLiveData.postValue(res)
//                println("updated: hehe ${res}")
            }
        }
        context = ctx
    }

}