package it.polito.mad.courtreservationapp.view_model

import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.FieldStatus
import it.polito.mad.courtreservationapp.models.OfferedCourt

object mockDb {

    fun getCourt(Cid:String):OfferedCourt{
        val c: OfferedCourt= OfferedCourt(
            Cid,
            "Tennis",
            FieldStatus.OPTIMAL,
            listOf(R.drawable.gesu), );
        return c;
    }
}