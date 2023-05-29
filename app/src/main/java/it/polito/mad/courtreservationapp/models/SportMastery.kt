package it.polito.mad.courtreservationapp.models


data class SportMastery(
    val sportId: Long = 0L,
    val userId: String?,
    val level: Int = 0,
    val achievement: String?,
    val id: Long = 0
)
