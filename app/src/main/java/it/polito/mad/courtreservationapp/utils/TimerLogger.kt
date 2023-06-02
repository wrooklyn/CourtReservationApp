package it.polito.mad.courtreservationapp.utils

import android.util.Log

class TimerLogger (val tag:String?){
    var start:Long =0;
    var end:Long =0;

    init {
        start = System.currentTimeMillis();
    }

    fun stop(){
        end = System.currentTimeMillis();
        val timeMillis = end-start;
        val seconds = (timeMillis/1000).toInt();
        val residual = timeMillis%1000;
        if(tag!=null){
            Log.v("TimerLogger","It took $seconds s $residual ms, $tag");
        }else{
            Log.v("TimerLogger","It took $seconds s $residual ms");
        }
    }
}