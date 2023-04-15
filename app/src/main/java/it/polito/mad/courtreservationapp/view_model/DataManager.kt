package it.polito.mad.courtreservationapp.view_model

import it.polito.mad.courtreservationapp.models.OfferedCourt


object DataManager{

    init {
        //some init if needed, like opening the db
    }
    fun getCourt(Cid:String):OfferedCourt{
        val data :OfferedCourt
        = mockDb.getCourt("001");
        //in the real implementation we could have a layer that queries the db, and one that creates the object?
        return data;
    }
}