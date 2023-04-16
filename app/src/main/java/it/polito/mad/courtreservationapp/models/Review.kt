package it.polito.mad.courtreservationapp.models

import java.util.Date
//TODO: optional
data class Review(val nickname:String,
                  val rating:Int,
                  val description:String,
                  val date: Date)
