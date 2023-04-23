package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.databinding.ActivityReservationBrowserBinding
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.*

class ReservationBrowserActivity : AppCompatActivity() {

    /* MOCK DATA */
    // TODO: add DB integration

    val user: User = User("pontifex", "Piero", "Angela", "superquark@spam.it", "Via Le Mani Dal Naso 420, Baghdad", 1, 183, 89, "+32 333456789", 1)

    private val sportCenter1: SportCenter = SportCenter("Sport Center #1", "Via Roma, 1 - Riva presso Chieri", 1)
    private val sportCenter2: SportCenter = SportCenter("Sport Center #2", "Via delle Moglie, 23 - Riva presso Chieri", 2)
    private val sportCenter3: SportCenter = SportCenter("Sport Center #3", "Piazza La Bomba, 69 - Kuala Lumpur", 3)

    val sportCenters: List<SportCenter> = listOf(sportCenter1, sportCenter2, sportCenter3)

    private val court1: Court = Court(1, "Soccer", 0, 1)
    private val court2: Court = Court(1, "Basketball", 0, 2)
    private val court3: Court = Court(1, "Volleyball", 0, 3)
    private val court4: Court = Court(2, "Soccer", 0, 4)
    private val court5: Court = Court(2, "Volleyball", 0, 5)
    private val court6: Court = Court(3, "Volleyball", 0, 6)

    val courts: List<Court> = listOf(court1, court2, court3, court4, court5, court5, court6)

    private val s1 : Service = Service("Showers",0)
    private val s2 : Service = Service("Equipment rental",1)

    private val court1Services: CourtWithServices = CourtWithServices(court1, listOf(s1))
    private val court2Services: CourtWithServices = CourtWithServices(court2, listOf(s1, s2))
    private val court3Services: CourtWithServices = CourtWithServices(court3, listOf(s1, s2))
    private val court4Services: CourtWithServices = CourtWithServices(court4, listOf(s2))
    private val court5Services: CourtWithServices = CourtWithServices(court5, listOf(s2))
    private val court6Services: CourtWithServices = CourtWithServices(court6, listOf())

    val courtsWithServices: List<CourtWithServices> = listOf(court1Services, court2Services, court3Services, court4Services, court5Services, court6Services)

    private val r1 : Reservation = Reservation("2023-04-23",1, 1,1,0)
    private val r2 : Reservation = Reservation("2023-04-24",2, 1,3,1)
    private val r3 : Reservation = Reservation("2023-04-24",3, 3,5,2)
    private val r4 : Reservation = Reservation("2023-04-27",0, 2,5,3)
    private val r5 : Reservation = Reservation("2023-04-30",1, 2,6,4)
    private val r6 : Reservation = Reservation("2023-05-01",2, 1,1,5)
    private val r7 : Reservation = Reservation("2023-05-01",3, 2,2,6)
    private val r8 : Reservation = Reservation("2023-05-01",0, 4,1,7)

    val reservations: List<Reservation> = listOf(r1, r2, r3, r4, r5, r6, r7, r8)

    private val r1Services: ReservationWithServices = ReservationWithServices(r1, listOf(s1))
    private val r2Services: ReservationWithServices = ReservationWithServices(r2, listOf(s2))
    private val r3Services: ReservationWithServices = ReservationWithServices(r3, listOf(s2))
    private val r4Services: ReservationWithServices = ReservationWithServices(r4, listOf())
    private val r5Services: ReservationWithServices = ReservationWithServices(r5, listOf())
    private val r6Services: ReservationWithServices = ReservationWithServices(r6, listOf(s1))
    private val r7Services: ReservationWithServices = ReservationWithServices(r7, listOf(s1, s2))
    private val r8Services: ReservationWithServices = ReservationWithServices(r8, listOf())

    val reservationsWithServices: List<ReservationWithServices> = listOf(r1Services, r2Services, r3Services, r4Services, r5Services, r6Services, r7Services, r8Services)

    private val court1Reservations: CourtWithReservations = CourtWithReservations(court1, listOf(r1, r6, r8))
    private val court2Reservations: CourtWithReservations = CourtWithReservations(court2, listOf(r7))
    private val court3Reservations: CourtWithReservations = CourtWithReservations(court3, listOf(r2))
    private val court4Reservations: CourtWithReservations = CourtWithReservations(court4, listOf())
    private val court5Reservations: CourtWithReservations = CourtWithReservations(court5, listOf(r4))
    private val court6Reservations: CourtWithReservations = CourtWithReservations(court6, listOf(r5))

    val courtsWithReservations: List<CourtWithReservations> = listOf(court1Reservations, court2Reservations, court3Reservations, court4Reservations, court5Reservations, court6Reservations)

    private val court1ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court1, listOf(r1Services, r6Services, r8Services))
    private val court2ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court2, listOf(r7Services))
    private val court3ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court3, listOf(r2Services))
    private val court4ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court4, listOf())
    private val court5ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court5, listOf(r4Services))
    private val court6ReservServices: CourtWithReservationsAndServices = CourtWithReservationsAndServices(court6, listOf(r5Services))

    val courtsWithReservationsAndServices: List<CourtWithReservationsAndServices> = listOf(court1ReservServices, court2ReservServices, court3ReservServices, court4ReservServices, court5ReservServices, court6ReservServices)

    val userReservations: UserWithReservations = UserWithReservations(user, listOf(r1, r2, r6))
    val userReservationsWithServices: UserWithReservationsAndServices = UserWithReservationsAndServices(user, listOf(r1Services, r2Services, r6Services))

    private val sportCenter1Courts: SportCenterWithCourts = SportCenterWithCourts(sportCenter1, listOf(court1, court2, court3))
    private val sportCenter2Courts: SportCenterWithCourts = SportCenterWithCourts(sportCenter2, listOf(court4, court5))
    private val sportCenter3Courts: SportCenterWithCourts = SportCenterWithCourts(sportCenter3, listOf(court6))

    val sportCentersWithCourts: List<SportCenterWithCourts> = listOf(sportCenter1Courts, sportCenter2Courts, sportCenter3Courts)

    private val sportCenter1CourtsReserv: SportCenterWithCourtsAndReservations = SportCenterWithCourtsAndReservations(sportCenter1, listOf(court1Reservations, court2Reservations, court3Reservations))
    private val sportCenter2CourtsReserv: SportCenterWithCourtsAndReservations = SportCenterWithCourtsAndReservations(sportCenter2, listOf(court4Reservations, court5Reservations))
    private val sportCenter3CourtsReserv: SportCenterWithCourtsAndReservations = SportCenterWithCourtsAndReservations(sportCenter3, listOf(court6Reservations))

    val sportCentersWithCourtsAndReservations: List<SportCenterWithCourtsAndReservations> = listOf(sportCenter1CourtsReserv, sportCenter2CourtsReserv, sportCenter3CourtsReserv)

    private val sportCenter1CourtsReservServices: SportCenterWithCourtsAndReservationsAndServices = SportCenterWithCourtsAndReservationsAndServices(sportCenter1, listOf(court1ReservServices, court2ReservServices, court3ReservServices))
    private val sportCenter2CourtsReservServices: SportCenterWithCourtsAndReservationsAndServices = SportCenterWithCourtsAndReservationsAndServices(sportCenter2, listOf(court4ReservServices, court5ReservServices))
    private val sportCenter3CourtsReservServices: SportCenterWithCourtsAndReservationsAndServices = SportCenterWithCourtsAndReservationsAndServices(sportCenter3, listOf(court6ReservServices))

    val sportCentersWithCourtsAndReservationsAndServices: List<SportCenterWithCourtsAndReservationsAndServices> = listOf(sportCenter1CourtsReservServices, sportCenter2CourtsReservServices, sportCenter3CourtsReservServices)

    private val sportCenter1CourtsServices: SportCenterWithCourtsAndServices = SportCenterWithCourtsAndServices(sportCenter1, listOf(court1Services, court2Services, court3Services))
    private val sportCenter2CourtsServices: SportCenterWithCourtsAndServices = SportCenterWithCourtsAndServices(sportCenter2, listOf(court4Services, court5Services))
    private val sportCenter3CourtsServices: SportCenterWithCourtsAndServices = SportCenterWithCourtsAndServices(sportCenter3, listOf(court6Services))

    val sportCentersWithCourtsAndServices: List<SportCenterWithCourtsAndServices> = listOf(sportCenter1CourtsServices, sportCenter2CourtsServices, sportCenter3CourtsServices)


    lateinit var binding: ActivityReservationBrowserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(BrowseReservationsFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.reservBrowserFragmentContainer,fragment)
        fragmentTransaction.commit()
    }
}