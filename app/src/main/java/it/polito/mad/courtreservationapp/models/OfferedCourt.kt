package it.polito.mad.courtreservationapp.models

data class OfferedCourt(
    val Cid:String,
    val sportName: String,
    val fieldStatus:FieldStatus?,
    val pictures:List<Int>,
    var reservations: List<Reservation>)
    //pictures are int because we will use resources (e.g. R.drawable.x which is an integer).


