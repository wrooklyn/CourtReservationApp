package it.polito.mad.courtreservationapp.utils

import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Service

object IconUtils {
    private val sportIconsId : Map<String, Int> = mapOf(
        Pair("Soccer", R.drawable.soccer_ball),
        Pair("Iceskating", R.drawable.ice_skate),
        Pair("Basketball", R.drawable.basketball_icon),
        Pair("Hockey", R.drawable.hockey),
        Pair("Tennis", R.drawable.tennis),
        Pair("Volley", R.drawable.volleyball),
        Pair("Rugby", R.drawable.rugby),
        Pair("Swimming", R.drawable.swimming) //TODO: better visibility icon
    )
    private val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment),
        Pair(4, R.drawable.wifi),
        Pair(5, R.drawable.lockers) //TODO: locker icon
    )

    fun getSportIcon(sportName: String): Int{
        return sportIconsId[sportName] ?: R.drawable.gesu
    }

    fun getServiceIcon(serviceId: Long): Int{
        return servicesIcons[serviceId] ?: R.drawable.gesu
    }
}