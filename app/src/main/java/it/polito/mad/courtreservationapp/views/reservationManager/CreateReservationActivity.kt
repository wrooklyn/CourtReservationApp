package it.polito.mad.courtreservationapp.views.reservationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityCreateReservationBinding
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReservations
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.view_model.ReservationFragmentViewModel
import java.util.Calendar

class CreateReservationActivity : AppCompatActivity() {
    //private var myReservation: Reservation = Reservation();
    var reservationDate : String? = null ;
    var reservationTimeSlot : MutableList<Long> = mutableListOf() ;
    var reservationServices : MutableList<Long> = mutableListOf() ;
    var reservationRequests : String = "";

    private var pageNumber : Int = 0

    //ViewModel
    lateinit var viewModel: ReservationFragmentViewModel
    lateinit var sportCenter: SportCenter
    lateinit var user: User
    lateinit var courtWithReservations: CourtWithReservations
    lateinit var courtWithServices: CourtWithServices


    //private val db = Room.databaseBuilder(this, AppDatabase::class.java, "Test5").createFromAsset("database/app.db").allowMainThreadQueries().build()
    //private val dao : CourtDao = db.courtDao()
    //val court = dao.getByIdWithReservationsAndServices(1).getOrAwaitValue()

//    //Testing without db
    val c : Court=Court(1,"Calcio", 0, 1)
    private val r1 : Reservation = Reservation("2023-04-23",1, 2,1,0)
    private val r2 : Reservation = Reservation("2023-04-23",2, 2,1,1)
    private val r3 : Reservation = Reservation("2023-04-25",3, 2,1,2)
    private val r4 : Reservation = Reservation("2023-04-27",0, 2,1,3)
    private val s1 : Service = Service("showers",0)
    private val s2 : Service = Service("equipment",1)
    private var courtReserv : CourtWithReservations = CourtWithReservations(c, listOf(r1,r2,r3,r4))
    private var group = courtReserv.reservations.groupBy { it.reservationDate }
    var reservationsByDateString = group.mapValues { entry ->
        entry.value.map { it.timeSlotId }
    }
    var courtServ : CourtWithServices = CourtWithServices(c, listOf(s1,s2))

    lateinit var binding: ActivityCreateReservationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCreateReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ReservationFragmentViewModel::class.java]
        viewModel.initAll(1,1,1)
//        viewModel.initCourt(1, 1) //TODO: courtId and centerId from other Activity
//        viewModel.initUser(1) //TODO: retrieve userId from other sources

        viewModel.sportCenter.observe(this){
            Log.i("VM", "${it.centerId}")
            sportCenter = it
        }

        viewModel.user.observe(this){
            user = it
        }


        viewModel.courtReservations.observe(this){ court ->
            courtWithReservations = court

            //mapping
            group = court.reservations.groupBy { it.reservationDate }
            reservationsByDateString = group.mapValues { entry ->
                entry.value.map { it.timeSlotId }
            }
        }
        viewModel.courtServices.observe(this){
            courtWithServices = it

            courtServ = it
            Log.i("Reserv", "Services: $it")
        }

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
//        val reservations: MutableList<ReservationWithServices> = mutableListOf()
//        for(timeSlot in reservationTimeSlot){
//            val res = Reservation(reservationDate?: Calendar.getInstance().toString(), timeSlot, user.userId, courtReserv.court.courtId)
////            reservations.add(ReservationWithServices(res, reservationServices as List<Service>))
//            val services = reservationServices.map { id ->
//                courtWithServices.services.first { it.serviceId == id }
//            }
//            val resWithServices = ReservationWithServices(res, services)
//            reservations.add(resWithServices)
//        }
//        val list: List<ReservationWithServices> = reservations

        viewModel.saveReservation(reservationDate, reservationTimeSlot, reservationServices)
        finish()
    }

    fun setSchedule(){
        //updates myReservation with the first fragment data
    }
    fun setServices(){
        //updates myReservation with the second fragment data
    }
    fun ggNEXT(){
        //take current index and handle navigation
        if(pageNumber+1>3) return;
        pageNumber++
        when(pageNumber){
            1 -> replaceFragment(ShowSelectServicesFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            3 -> commitReservation()
        }
    }
    fun ggBack(){
        //take current index and and handle navigation
        if(pageNumber-1<0) return;
        pageNumber--
        when(pageNumber){
            0 -> replaceFragment(ShowCalendarFragment())
            1 -> replaceFragment(ShowSelectServicesFragment())
            2 -> replaceFragment(ShowSummaryFragment())
            //3 -> commitReservation()
        }
    }
}