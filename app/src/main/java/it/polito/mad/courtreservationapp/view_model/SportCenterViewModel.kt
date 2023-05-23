package it.polito.mad.courtreservationapp.view_model

import FireSportCenterRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWIthCourtsAndReviewsAndUsers
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SportCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository: FireSportCenterRepository = FireSportCenterRepository(application)
    val sportCentersLiveData: MutableLiveData<List<SportCenterWithCourtsAndServices>> = MutableLiveData()
    val sportCentersWithReviewsAndUsersLiveData: MutableLiveData<List<SportCenterWIthCourtsAndReviewsAndUsers>> = MutableLiveData()
    lateinit var sportCentersWithCourtsAndServices: List<SportCenterWithCourtsAndServices>
    lateinit var sportCentersWithCourtsAndReviewsAndUsers: List<SportCenterWIthCourtsAndReviewsAndUsers>

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

    val sportIconsId : Map<String, Int> = mapOf(
        Pair("Soccer",R.drawable.soccer_ball),
        Pair("Iceskate",R.drawable.ice_skate),
        Pair("Basketball",R.drawable.basketball_icon),
        Pair("Hockey",R.drawable.hockey),
        Pair("Tennis",R.drawable.tennis),
        Pair("Volley",R.drawable.volleyball),
        Pair("Rugby",R.drawable.rugby)
        )
    val sportCenterImages : Map<Long, Int> = mapOf(
        Pair(1, R.drawable.run_center),
        Pair(2, R.drawable.golf_center),
        Pair(3, R.drawable.basket_center)
    )
    val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment)
    )

    val courtImages: Map<String, Int> = mapOf(
        Pair("Tennis",R.drawable.tennis_court),
        Pair("Basketball",R.drawable.basket_court),
        Pair("Soccer",R.drawable.football_court),
        Pair("Volley",R.drawable.volley_court),
        Pair("Iceskate", R.drawable.iceskating_rink),
        Pair("Swimming", R.drawable.swimming_pool),
        Pair("Hockey", R.drawable.hockey),
        Pair("Rugby", R.drawable.rugby_court)
    )

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