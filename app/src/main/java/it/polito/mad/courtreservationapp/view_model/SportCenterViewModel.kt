package it.polito.mad.courtreservationapp.view_model

import it.polito.mad.courtreservationapp.db.remoteRepository.FireSportCenterRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWIthCourtsAndReviewsAndUsers
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SportCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository: FireSportCenterRepository = FireSportCenterRepository(application)
    val sportCentersLiveData: MutableLiveData<List<SportCenterWithCourtsAndServices>> = MutableLiveData()
    val sportCentersWithReviewsAndUsersLiveData: MutableLiveData<List<SportCenterWIthCourtsAndReviewsAndUsers>> = MutableLiveData()
    lateinit var sportCentersWithCourtsAndServices: List<SportCenterWithCourtsAndServices>
    lateinit var sportCentersWithCourtsAndReviewsAndUsers: List<SportCenterWIthCourtsAndReviewsAndUsers>

    val isPopupOpen = MutableLiveData(false)
    var distanceFilterValue : Double? = null
    var sportFilters : MutableList<String> = mutableListOf()
    var allSports: MutableList<String> = mutableListOf()
     fun initData(){
         runBlocking(Dispatchers.Default) {
             launch {/*
                 sportCenterRepository.getAllWithCourtsAndServices().addOnCompleteListener() { t->
                     sportCentersLiveData.postValue(t.result)
                 }*/
                 sportCentersLiveData.postValue(sportCenterRepository.getAllWithCourtsAndServices2())
                 //this one is the new best approach
                 val res = sportCenterRepository.getAllWithCourtsAndReviewsAndUsers()
                 sportCentersWithReviewsAndUsersLiveData.postValue(res)
//                 println("updated: hehe ${res.first().courtsWithReviewsAndUsers}")
             }
         }

    }

    fun loadSportCenters(centersWithCourtsAndServices: List<SportCenterWithCourtsAndServices>) {
        sportCentersWithCourtsAndServices = centersWithCourtsAndServices
        sportCentersWithCourtsAndServices.map { sportCenterWithCourtsAndServices ->
            sportCenterWithCourtsAndServices.courtsWithServices.forEach() { courtWithServices ->
                if (!allSports.contains(courtWithServices.court.sportName)) {
                    allSports.add(courtWithServices.court.sportName)
                }
            }
        }
    }

    fun loadReviews(centersWIthCourtsAndReviewsAndUsers: List<SportCenterWIthCourtsAndReviewsAndUsers>){
        sportCentersWithCourtsAndReviewsAndUsers = centersWIthCourtsAndReviewsAndUsers
    }
}