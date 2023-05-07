package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "sportMasteries",
    foreignKeys = [
        ForeignKey(
            entity = Sport::class,
            parentColumns = ["id"],
            childColumns = ["sportId"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )
    ]
)
data class SportMastery(
    val sportId: Long,
    val userId: Long,
    val level: Int,
    val achievement: String?,
    @PrimaryKey(autoGenerate = true)
    val id : Long = 0
)
