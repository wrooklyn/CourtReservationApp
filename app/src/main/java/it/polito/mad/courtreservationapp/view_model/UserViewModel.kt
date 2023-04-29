package it.polito.mad.courtreservationapp.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import it.polito.mad.courtreservationapp.db.repository.UserRepository
import it.polito.mad.courtreservationapp.models.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepo: UserRepository = UserRepository(application)

    lateinit var user: LiveData<User>

    fun insertUser(user: User) {
        viewModelScope.launch{
            userRepo.insertUser(user)
        }
    }
    fun setCurrentUser(userId: Long) {
        user = userRepo.getById(userId)
    }

    fun updateUser(u: User) {
        viewModelScope.launch{
            userRepo.updateUser(u)
        }
    }
}