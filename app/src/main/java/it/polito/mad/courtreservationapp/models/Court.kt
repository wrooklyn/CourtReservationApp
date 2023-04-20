package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "courts",
    foreignKeys = [
        ForeignKey(entity = SportCenter::class,
            parentColumns = ["centerId"],
            childColumns = ["courtCenterId"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        ),
    ]
)
data class Court(
    @PrimaryKey(autoGenerate = true)
    val courtId: Int,
    val sportName: String,
    val fieldStatus: Int,

    val courtCenterId: Int
//    val pictures: List<Int>,
)
    //pictures are int because we will use resources (e.g. R.drawable.x which is an integer).


