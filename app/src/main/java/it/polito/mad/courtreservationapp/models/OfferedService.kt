package it.polito.mad.courtreservationapp.models

data class OfferedService(
    val Sid:String,
    val pricePerHour:Number,
    val type:ServiceType,)