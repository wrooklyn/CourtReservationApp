package it.polito.mad.courtreservationapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(

    val username: String,

    val firstName: String,

    val lastName: String,

    val email: String,

    val address: String,

    val gender: Int,

    val height: Int,

    val weight: Int,

    val phone: String,

    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0
)