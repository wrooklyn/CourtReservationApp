package it.polito.mad.courtreservationapp.models

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {

    fun calculateDistance(other : Coordinates): Double {
        val earthRadius = 6371.0 // Radius of the Earth in kilometers

        val latDistance = Math.toRadians(other.latitude - latitude)
        val lonDistance = Math.toRadians(other.longitude - longitude)

        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}