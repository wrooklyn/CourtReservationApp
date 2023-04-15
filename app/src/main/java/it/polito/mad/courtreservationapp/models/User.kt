package it.polito.mad.courtreservationapp.models

enum class Gender{MALE, FEMALE, OTHER}

data class User(var username: String,
                var firstName: String,
                var lastName: String,
                var email: String,
                var address: String,
                var gender: Gender,
                var height: Int,
                var weight: Double,
                var phone: String,
                var photoPath: String)