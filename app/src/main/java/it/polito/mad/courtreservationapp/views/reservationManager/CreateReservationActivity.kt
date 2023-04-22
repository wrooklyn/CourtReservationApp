package it.polito.mad.courtreservationapp.views.reservationManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.db.AppDatabase
import it.polito.mad.courtreservationapp.db.dao.CourtDao
import it.polito.mad.courtreservationapp.db.dao.ReservationDao
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservationsAndServices
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.views.reservationManager.ShowSummaryFragment
import it.polito.mad.utils.getOrAwaitValue
import java.text.SimpleDateFormat
import java.util.*

class CreateReservationActivity : AppCompatActivity() {
    //private var myReservation: Reservation = Reservation();
    var reservationDate : String? = null ;
    var reservationTimeSlot : MutableList<Int> = mutableListOf() ;
    var reservationServices : List<Int>? = null ;
    var reservationRequests : String? = null ;
    private var pageNumber : Int = 0

    //private val db = Room.databaseBuilder(this, AppDatabase::class.java, "Test5").createFromAsset("database/app.db").allowMainThreadQueries().build()
    //private val dao : CourtDao = db.courtDao()
    //val court = dao.getByIdWithReservationsAndServices(1).getOrAwaitValue()
    val c : Court=Court(1,"Calcio", 0, 1)
    private val r1 : Reservation = Reservation("2023-04-23",1, 2,1,0)
    private val r2 : Reservation = Reservation("2023-04-23",2, 2,1,1)
    private val r3 : Reservation = Reservation("2023-04-25",3, 2,1,2)
    private val r4 : Reservation = Reservation("2023-04-27",0, 2,1,3)
    private val s1 : Service = Service("showers",0)
    private val s2 : Service = Service("equipment",1)
    private val courtReserv : CourtWithReservations = CourtWithReservations(c, listOf(r1,r2,r3,r4))
    private val group = courtReserv.reservations.groupBy { it.reservationDate }
    val reservationsByDateString = group.mapValues { entry ->
        entry.value.map { it.timeSlotId }
    }

    val courtServ : CourtWithServices = CourtWithServices(c, listOf(s1,s2))
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
        finish()
    }

    fun setSchedule(){
        //updates myReservation with the first fragment data
    }
    fun setServices(){
        //updates myReservation with the second fragment data
    }
    fun ggNEXT(){
        //take current index and
        if(pageNumber==0)pageNumber++
        if(pageNumber+1>3) return;
        pageNumber++
        when(pageNumber){
            1 -> replaceFragment(ShowSelectServicesFragmentFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            3 -> commitReservation()
        }
    }
    fun ggBack(){
        //take current index and
        if(pageNumber==2)pageNumber--
        if(pageNumber-1<0) return;
        pageNumber--
        when(pageNumber){
            0 -> replaceFragment(ShowCalendarFragment())
            1 -> replaceFragment(ShowSelectServicesFragmentFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            //3 -> commitReservation()
        }
    }
}