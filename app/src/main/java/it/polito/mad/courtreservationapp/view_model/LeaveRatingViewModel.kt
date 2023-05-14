package it.polito.mad.courtreservationapp.view_model

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.repository.*
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LeaveRatingViewModel(application: Application): AndroidViewModel(application) {
    private val tag: String = "LeaveRatingViewModel"
    private val reviewRepo: ReviewRepository = ReviewRepository(application)
    private val sportCenterRepo: SportCenterRepository = SportCenterRepository(application)

    lateinit var sportCenterWithCourtsLiveData: LiveData<SportCenterWithCourts>
    lateinit var context: LeaveRatingActivity

    //display
    var sportCenterName = mutableStateOf("")
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
            reviewRepo.insertReview(review)
            context.finish()
        }
    }

    fun init( ctx: LeaveRatingActivity){
        sportCenterWithCourtsLiveData = sportCenterRepo.getCenterWithCourts(sportCenterId)
        context = ctx
    }

    fun isAlreadyRated(reservationId: Long): Boolean {
        val review: Review? = reviewRepo.getByReservationId(reservationId).value
        Log.i("REVIEW", "Review is $review")
        return review != null
    }

}