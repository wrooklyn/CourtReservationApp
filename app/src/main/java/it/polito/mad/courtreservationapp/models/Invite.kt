package it.polito.mad.courtreservationapp.models

enum class Status(stateString: String){
    PENDING("pending"),
    ACCEPTED("accepted"),
    REFUSED("refused")
}

data class Invite(
    val reservationId: String,
    val status: Status,
    val inviter:String,
    val additionalInfo: AdditionalInfo?=null,

)

data class AdditionalInfo(
    var date: String,
    var timeslot: Long,
    var sport : String,
    var centerName : String,
    var address : String,

)