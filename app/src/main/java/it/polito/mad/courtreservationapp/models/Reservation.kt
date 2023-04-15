package it.polito.mad.courtreservationapp.models

import java.util.Date

data class Reservation(
    val Rid:String?,
    val Uid:String,
    var date: Date,
    var timeSlot: Int,
    var customRequest:String?)

//there will be a mapping of time slots in the form:
/*
0 -> 10-11 am
1 -> 11-12 am
2 -> 12-13 pm
3 -> 13-14 pm
4 -> 14-15 pm
5 -> 15-16 pm
6 -> 16-17 pm
7 -> 17-18 pm
 */
