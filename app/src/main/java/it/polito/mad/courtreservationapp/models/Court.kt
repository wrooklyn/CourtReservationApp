package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE

@Entity(tableName = "courts",
        primaryKeys = ["centerId", "courtId"],
        foreignKeys = [
            ForeignKey(entity = SportCenter::class,
                parentColumns = ["centerId"],
                childColumns = ["centerId"],
                onDelete = CASCADE,
                onUpdate = CASCADE)
        ]
        )
data class Court(
    val centerId: String,
    val courtId: String,
    val sportName: String,
    val fieldStatus: Int
//    val pictures: List<Int>,
)
    //pictures are int because we will use resources (e.g. R.drawable.x which is an integer).


