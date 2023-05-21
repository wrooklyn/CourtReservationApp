package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    val username: String = "",

    val firstName: String = "",

    val lastName: String = "",

    val email: String,

    val address: String = "",

    val gender: Int = 0,

    val height: Int = 0,

    val weight: Int = 0,

    val phone: String = "",

    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0
)