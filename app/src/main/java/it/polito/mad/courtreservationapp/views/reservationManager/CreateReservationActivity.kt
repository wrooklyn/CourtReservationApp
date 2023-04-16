package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.views.reservationManager.ShowSummaryFragment
import java.util.*

class CreateReservationActivity : AppCompatActivity() {
    //private var myReservation: Reservation = Reservation("","", Date(),1,"")
    private var pageNumber : Int = 0
    lateinit var binding: ActivityCreateReservationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(ShowCalendarFragment())
    }
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer,fragment)
        fragmentTransaction.commit()
    }

    fun commitReservation(){
        //take myReservation and call the dao
        //close activity
    }

    fun setSchedule(){
        //updates myReservation with the first fragment data
    }
    fun setServices(){
        //updates myReservation with the second fragment data
    }
    fun ggNEXT(){
        //take current index and
        pageNumber++
        when(pageNumber){
            //1 -> replaceFragment()
            2 -> replaceFragment(ShowSummaryFragment())
            3 -> commitReservation()
        }
    }
}