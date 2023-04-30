package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.db.repository.SportCenterRepository

class SportCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val sportCenterRepository: SportCenterRepository = SportCenterRepository(application)
    val sportCentersLiveData: LiveData<List<SportCenterWithCourtsAndServices>> = sportCenterRepository.getAllWithCourtsAndServices()
    lateinit var sportCentersWithCourtsAndServices: List<SportCenterWithCourtsAndServices>

    var sportFilters : MutableList<String> = mutableListOf()
    var allSports: MutableList<String> = mutableListOf()


    val sportIconsId : Map<String, Int> = mapOf(
        Pair("Soccer",R.drawable.soccer_ball),
        Pair("Iceskate",R.drawable.ice_skate),
        Pair("Basketball",R.drawable.basketball_icon),
        Pair("Hockey",R.drawable.hockey),
        Pair("Tennis",R.drawable.tennis),
        Pair("Volley",R.drawable.volleyball),
        Pair("Rugby",R.drawable.rudgby)
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
                    Log.i("Home", "Adding: ${courtWithServices.court.sportName}")
                    allSports.add(courtWithServices.court.sportName)
                }
            }
        }
    }
}