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
    val courtCenterId: Long = 0L,
    val sportName: String = "",
    val fieldStatus: Int = 0,

    @PrimaryKey(autoGenerate = true)
    var courtId: Long = 0,
)
    //pictures are int because we will use resources (e.g. R.drawable.x which is an integer).


