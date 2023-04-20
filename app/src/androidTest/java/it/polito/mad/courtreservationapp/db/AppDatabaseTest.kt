package it.polito.mad.courtreservationapp.db

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polito.mad.courtreservationapp.db.crossref.CourtServiceCrossRef
import it.polito.mad.courtreservationapp.db.crossref.ReservationServiceCrossRef
import it.polito.mad.courtreservationapp.db.dao.*
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.utils.getOrAwaitValue
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest: TestCase(){
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var sportCenterDao: SportCenterDao
    private lateinit var courtDao: CourtDao
    private lateinit var serviceDao: ServiceDao
    private lateinit var courtAndServiceDao: CourtAndServiceDao
    private lateinit var userDao: UserDao
    private lateinit var reservationDao: ReservationDao
    private lateinit var reservationAndServiceDao: ReservationAndServiceDao

    private lateinit var center: SportCenter
    private lateinit var court: Court
    private lateinit var service1: Service
    private lateinit var service2: Service
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var reservation1: Reservation
    private lateinit var reservation2: Reservation


    @Before
    fun createDB() {
        initObjects()
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        sportCenterDao = db.sportCenterDao()
        courtDao = db.courtDao()
        serviceDao = db.serviceDao()
        courtAndServiceDao = db.courtAndServiceDao()
        userDao = db.userDao()
        reservationDao = db.reservationDao()
        reservationAndServiceDao = db.reservationAndServiceDao()
    }

    @After
    fun closeDB(){
        db.close()
    }

    private fun initObjects(){
        center = SportCenter(1,"SC1", "address")
        court = Court(1,"SC1", 1, 1)
        service1 = Service(1,"service 1", 10.5)
        service2 = Service(2,"service 2", 15.5)
        user1 = User(1, "u1", "first", "last", "mail", "uAddress", 1, 170, 70.0, "phone")
        user2 = User(2, "u2", "first", "last", "mail", "uAddress", 1, 175, 70.0, "phone")
        reservation1 = Reservation(1, "today", 1, 1, 1)
        reservation2 = Reservation(2, "today", 1, 2, 1)
    }
    @Test
    fun sportCenterDeleteCenter() = runBlocking{

        Log.d("Test", "$center")
        sportCenterDao.save(center)

        var centers = sportCenterDao.getAll().getOrAwaitValue()
        Log.d("Test", "$centers")
        assertTrue(centers.contains(center))

        sportCenterDao.delete(center)
        centers = sportCenterDao.getAll().getOrAwaitValue()
        Log.d("Test", "$centers")
        assertTrue(!centers.contains(center))

    }

    @Test
    fun sportCenterWithCourtsDeleteCenter() = runBlocking{

        sportCenterDao.save(center)
        courtDao.save(court)

        var courts = courtDao.getAll().getOrAwaitValue()
        Log.d("Test", "$courts")
        assertTrue(courts.contains(court))

        val centerWithCourts = SportCenterWithCourts(center, listOf(court))
        Log.d("Test", "${centerWithCourts.sportCenter}, ${centerWithCourts.courts}")

        var centersWithCourts = sportCenterDao.getAllWithCourts().getOrAwaitValue()
        Log.d("Test", "$centersWithCourts")
        assertTrue(centersWithCourts.contains(centerWithCourts))

        sportCenterDao.delete(center)
        centersWithCourts = sportCenterDao.getAllWithCourts().getOrAwaitValue()
        Log.d("Test", "$centersWithCourts")
        assertTrue(!centersWithCourts.contains(centerWithCourts))

        courts = courtDao.getAll().getOrAwaitValue()
        Log.d("Test", "$courts")
        assertTrue(courts.isEmpty())

    }

    @Test
    fun sportCenterWithCourtsAndServicesDeleteSportCenter() = runBlocking{
        sportCenterDao.save(center)
        courtDao.save(court)
        serviceDao.save(service1)
        courtAndServiceDao.save(CourtServiceCrossRef(court.courtId, service1.serviceId))

        val cs = CourtWithServices(court, listOf(service1))
        val ccs = SportCenterWithCourtsAndServices(center, listOf(cs))

        var many_ccs = sportCenterDao.getAllWithCourtsAndServices().getOrAwaitValue()
        Log.d("test","SportCenters: $many_ccs")
        assertTrue(many_ccs.contains(ccs))

        sportCenterDao.delete(center)
        many_ccs = sportCenterDao.getAllWithCourtsAndServices().getOrAwaitValue()
        Log.d("test","SportCenters: $many_ccs")
        assertTrue(many_ccs.isEmpty())

        val many_cs = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("test","CourtsWithServ: $many_cs")
        assertTrue(many_cs.isEmpty())

    }

    @Test
    fun sportCenterWithCourtsAndReservationsDeleteSportCenter() = runBlocking{
        sportCenterDao.save(center)
        courtDao.save(court)
        userDao.save(user1, user2)
        reservationDao.save(reservation1, reservation2)

        val scr = SportCenterWithCourtsAndReservations(center, listOf(
            CourtWithReservations(court,
                listOf(reservation1, reservation2)
            )
        ))

        var scrs = sportCenterDao.getAllWithCourtsAndReservations().getOrAwaitValue()
        Log.d("test","SportCenters: $scrs")
        assertTrue(scrs.contains(scr))

        sportCenterDao.delete(center)
        scrs = sportCenterDao.getAllWithCourtsAndReservations().getOrAwaitValue()
        Log.d("test","SportCenters: $scrs")
        assertTrue(scrs.isEmpty())

    }

    @Test
    fun courtDeleteCourt() = runBlocking{
        sportCenterDao.save(center)
        courtDao.save(court)

        var courts = courtDao.getAll().getOrAwaitValue()
        assertTrue(courts.contains(court))

        courtDao.delete(court)
        courts = courtDao.getAll().getOrAwaitValue()
        assertTrue(!courts.contains(court))
    }

    @Test
    fun courtWithServicesDeleteCourt() = runBlocking{
        sportCenterDao.save(center)
        courtDao.save(court)
        serviceDao.save(service1)
        courtAndServiceDao.save(CourtServiceCrossRef(court.courtId, service1.serviceId))

        val courtWithServices = CourtWithServices(court, listOf(service1))
        Log.d("Test", "CourtWithServices: $courtWithServices")

        var courtsWithServices = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("Test", "CourtsWithServices: $courtsWithServices")
        assertTrue(courtsWithServices.contains(courtWithServices))

        courtDao.delete(court)
        courtsWithServices = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("Test", "$courtsWithServices")
        assertTrue(!courtsWithServices.contains(courtWithServices))

    }

    @Test
    fun courtWithReservationAndServicesDeleteCourt() = runBlocking {
        sportCenterDao.save(center)
        courtDao.save(court)
        serviceDao.save(service1, service2)
        userDao.save(user1)
        reservationDao.save(reservation1)
        reservationAndServiceDao.save(ReservationServiceCrossRef(reservation1.reservationId,service1.serviceId))
        reservationAndServiceDao.save(ReservationServiceCrossRef(reservation1.reservationId,service2.serviceId))


        val crs = CourtWithReservationsAndServices(court,
            listOf(ReservationWithServices(reservation1, listOf(service1, service2)))
            )
        var courts = courtDao.getAllWithReservationsAndServices().getOrAwaitValue()
        Log.d("Test", "CourtWithResAndServ: $courts")
        assertTrue(courts.contains(crs))

        courtDao.delete(court)
        courts = courtDao.getAllWithReservationsAndServices().getOrAwaitValue()
        Log.d("Test", "CourtWithResAndServ: $courts")
        assertTrue(courts.isEmpty())

    }

    @Test
    fun userDeleteUser() = runBlocking {
        userDao.save(user1)

        var users = userDao.getAll().getOrAwaitValue()
        Log.d("test","Users: $users")
        assertTrue(users.contains(user1))

        userDao.delete(user1)
        users = userDao.getAll().getOrAwaitValue()
        Log.d("test","Users: $users")
        assertTrue(users.isEmpty())

    }

    @Test
    fun userWithReservationsDeleteUser() = runBlocking {
        sportCenterDao.save(center)
        courtDao.save(court)
        userDao.save(user1)
        reservationDao.save(reservation1)

        val userWithReservations = UserWithReservations(user1, listOf(reservation1))

        var users = userDao.getAllWithReservations().getOrAwaitValue()
        Log.d("test","Users: $users")
        assertTrue(users.contains(userWithReservations))

        userDao.delete(user1)
        users = userDao.getAllWithReservations().getOrAwaitValue()
        Log.d("test","Users: $users")
        assertTrue(users.isEmpty())

    }


    @Test
    fun courtWithReservationsDeleteReservation() = runBlocking {
        sportCenterDao.save(center)
        courtDao.save(court)
        userDao.save(user1)
        reservationDao.save(reservation1)

        val courtWithReservations = CourtWithReservations(court, listOf(reservation1))

        var courts = courtDao.getAllWithReservations().getOrAwaitValue()
        Log.d("test","Courts: $courts.")
        assertTrue(courts.contains(courtWithReservations))

        reservationDao.delete(reservation1)
        courts = courtDao.getAllWithReservations().getOrAwaitValue()
        Log.d("test","Courts: $courts")
        assertTrue(courts[0].reservations.isEmpty())

    }

    @Test
    fun courtWithReservationsDeleteCourt() = runBlocking {
        sportCenterDao.save(center)
        courtDao.save(court)
        userDao.save(user1)
        reservationDao.save(reservation1)

        val courtWithReservations = CourtWithReservations(court, listOf(reservation1))

        var courts = courtDao.getAllWithReservations().getOrAwaitValue()
        var reservations = reservationDao.getAll().getOrAwaitValue()
        Log.d("test","Courts: $courts.")
        Log.d("test","Reservations: $reservations")
        assertTrue(courts.contains(courtWithReservations))

        courtDao.delete(court)
        courts = courtDao.getAllWithReservations().getOrAwaitValue()
        reservations = reservationDao.getAll().getOrAwaitValue()
        Log.d("test","Courts: $courts")
        Log.d("test","Reservations: $reservations")
        assertTrue(courts.isEmpty())

    }

}