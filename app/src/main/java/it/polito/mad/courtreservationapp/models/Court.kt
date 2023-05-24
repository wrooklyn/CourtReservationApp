package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

data class Court(
    val sportCenterId: String?,
    val sportName: String = "",
    val fieldStatus: Int = 0,
    var courtId: String,
    val image: String?
)
    //pictures are int because we will use resources (e.g. R.drawable.x which is an integer).


