package it.polito.mad.courtreservationapp.views.login

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SavedPreference {

    var EMAIL= "email"
    var USERNAME="username"

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
        editor(
            context,
            EMAIL,
            email
        )
        EMAIL = email
    }

    fun setUsername(context: Context, username:String){
        editor(
            context,
            USERNAME,
            username
        )
        USERNAME = username
    }

    fun getUsername(context: Context) = getSharedPreference(
        context
    )?.getString(USERNAME,"") ?: USERNAME

}