package it.polito.mad.courtreservationapp.models

enum class FieldStatus (statusCode:Int) {
    OPTIMAL(0),
    DRY(1),
    WET(2),
    MUDDY(3),
    ONGOING_ZOMBIE_APOCALYPSE(4);
}