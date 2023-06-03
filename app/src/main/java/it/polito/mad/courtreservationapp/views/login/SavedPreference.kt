package it.polito.mad.courtreservationapp.views.login

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import it.polito.mad.courtreservationapp.models.Coordinates

object SavedPreference {

    var EMAIL= "email"
    var USERNAME="username"
    var coordinates : Coordinates? = null
    private fun getSharedPreference(ctx: Context?): SharedPreferences? {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    private fun editor(context: Context, const:String, string: String){
        getSharedPreference(
            context
        )?.edit()?.putString(const,string)?.apply()
    }

    fun getEmail(context: Context)= getSharedPreference(
        context
    )?.getString(EMAIL,"") ?: EMAIL

    fun setEmail(context: Context, email: String){
        EMAIL = email
        editor(
            context,
            EMAIL,
            email
        )

    }

    fun setUsername(context: Context, username:String){
        USERNAME = username
        editor(
            context,
            USERNAME,
            username
        )

    }

    fun getUsername(context: Context) = getSharedPreference(
        context
    )?.getString(USERNAME,"") ?: USERNAME

}