package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.mad.courtreservationapp.db.repository.UserRepository
import it.polito.mad.courtreservationapp.models.User

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepo: UserRepository = UserRepository(application)

    lateinit var user: LiveData<User>

    fun setCurrentUser(userId: Long) {
        user = userRepo.getById(userId)
    }
}