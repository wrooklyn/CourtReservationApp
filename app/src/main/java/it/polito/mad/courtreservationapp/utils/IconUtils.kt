package it.polito.mad.courtreservationapp.utils

import it.polito.mad.courtreservationapp.R

object IconUtils {
    private val sportIconsId : Map<String, Int> = mapOf(
        Pair("soccer", R.drawable.soccer_ball),
        Pair("iceskating", R.drawable.ice_skate),
        Pair("basketball", R.drawable.basketball_icon),
        Pair("hockey", R.drawable.hockey),
        Pair("tennis", R.drawable.tennis),
        Pair("volley", R.drawable.volleyball),
        Pair("rugby", R.drawable.rugby),
        Pair("swimming", R.drawable.swimming)
    )
    private val servicesIcons: Map<Long, Int> = mapOf(
        Pair(0, R.drawable.safety_shower),
        Pair(1, R.drawable.equipment),
        Pair(2, R.drawable.coach),
        Pair(3, R.drawable.refreshment),
        Pair(4, R.drawable.wifi),
        Pair(5, R.drawable.lockers)
    )

    fun getSportIcon(sportName: String): Int{
        return sportIconsId[sportName.lowercase()] ?: R.drawable.gesu
    }

    fun getServiceIcon(serviceId: Long): Int{
        return servicesIcons[serviceId] ?: R.drawable.gesu
    }
}