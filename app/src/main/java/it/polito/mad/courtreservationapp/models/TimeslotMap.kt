package it.polito.mad.courtreservationapp.models

object TimeslotMap {
    private val timeslotMap: MutableMap<Long, String> = HashMap()

    init {
        timeslotMap[0] = "10:00 - 11:00"
        timeslotMap[1] = "11:00 - 12:00"
        timeslotMap[2] = "12:00 - 13:00"
        timeslotMap[3] = "13:00 - 14:00"
        timeslotMap[4] = "14:00 - 15:00"
        timeslotMap[5] = "15:00 - 16:00"
        timeslotMap[6] = "16:00 - 17:00"
        timeslotMap[7] = "17:00 - 18:00"
    }

    fun getTimeslotString(timeslotId: Long): String? {
        return timeslotMap[timeslotId]
    }
}
