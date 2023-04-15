package it.polito.mad.courtreservationapp.models

data class SportsCenter(
    val SCid:String,
    val name:String,
    val address:String,
    val courts:List<OfferedCourt>,
    val services:List<OfferedService>)