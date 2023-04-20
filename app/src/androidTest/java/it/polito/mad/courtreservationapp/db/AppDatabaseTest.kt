package it.polito.mad.courtreservationapp.db

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.polito.mad.courtreservationapp.db.dao.CourtDao
import it.polito.mad.courtreservationapp.db.dao.ServiceDao
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.db.relationships.CourtWithServices
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.models.SportCenter
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

    @Before
    fun createDB() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        sportCenterDao = db.sportCenterDao()
        courtDao = db.courtDao()
        serviceDao = db.serviceDao()
    }

    @After
    fun closeDB(){
        db.close()
    }

    @Test
    fun insertAndDeleteSportCenter() = runBlocking{

        val center = SportCenter(1,"SC1", "address")
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
    fun insertAndDeleteSportCenterWithCourts() = runBlocking{
        val center = SportCenter(1,"SC1", "address")
        sportCenterDao.save(center)

        val court = Court(1,"SC1", 1, 1)
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
//        courts.forEach(){Log.d("Test", "Hello")}
    }

    @Test
    fun insertAndDeleteCourt() = runBlocking{
        val center = SportCenter(1,"SC1", "address")
        sportCenterDao.save(center)
        val court = Court(1,"SC1", 1, 1)
        courtDao.save(court)
        var courts = courtDao.getAll().getOrAwaitValue()

        assertTrue(courts.contains(court))

        courtDao.delete(court)
        courts = courtDao.getAll().getOrAwaitValue()
        assertTrue(!courts.contains(court))
    }

    @Test
    fun insertAndDeleteCourtWithServices() = runBlocking{
        val center = SportCenter(1,"SC1", "address")
        sportCenterDao.save(center)
        val court = Court(1,"SC1", 1, 1)
        courtDao.save(court)

        val service = Service(1,"service 1", 10.5, 1, 1)
        serviceDao.save(service)

        val courtWithServices = CourtWithServices(court, listOf(service))
        Log.d("Test", "${courtWithServices.court}, ${courtWithServices.services}")

        var courtsWithServices = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("Test", "$courtsWithServices")
        assertTrue(courtsWithServices.contains(courtWithServices))

        courtDao.delete(court)
        courtsWithServices = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("Test", "$courtsWithServices")
        assertTrue(!courtsWithServices.contains(courtWithServices))

    }

    @Test
    fun insertAndDeleteSportCenterWithCourtsAndServices() = runBlocking{
        val center = SportCenter(1,"SC1", "address")
        val court = Court(1,"SC1", 1, 1)
        val service = Service(1, "serv 1", 10.0, 1,1)

        sportCenterDao.save(center)
        courtDao.save(court)
        serviceDao.save(service)

        val cs = CourtWithServices(court, listOf(service))
        val ccs = SportCenterWithCourtsAndServices(center, listOf(cs))

        var many_ccs = sportCenterDao.getAllWithCourtsAndServices().getOrAwaitValue()
        Log.d("test","$many_ccs")
        assertTrue(many_ccs.contains(ccs))

        sportCenterDao.delete(center)
        many_ccs = sportCenterDao.getAllWithCourtsAndServices().getOrAwaitValue()
        Log.d("test","SportCourtsServ: $many_ccs")
        assertTrue(many_ccs.isEmpty())

        val many_cs = courtDao.getAllWithServices().getOrAwaitValue()
        Log.d("test","CourtsWithServ: $many_cs")
        assertTrue(many_cs.isEmpty())



    }

}