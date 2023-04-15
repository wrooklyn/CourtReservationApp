package it.polito.mad.courtreservationapp.models



data class User(val Uid: String,
                var username: String,
                var firstName: String,
                var lastName: String,
                var email: String,
                var address: String,
                var gender: Gender,
                var height: Int,
                var weight: Double,
                var phone: String,
                var photoPath: String,
                var favorites: List<OfferedCourt> = mutableListOf()
)