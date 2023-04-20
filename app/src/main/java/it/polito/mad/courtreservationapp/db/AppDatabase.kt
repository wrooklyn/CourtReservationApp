package it.polito.mad.courtreservationapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.polito.mad.courtreservationapp.db.dao.CourtDao
import it.polito.mad.courtreservationapp.db.dao.ServiceDao
import it.polito.mad.courtreservationapp.db.dao.SportCenterDao
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.db.dao.UserDao
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(  entities = [
                User::class,
                SportCenter::class,
                Court::class,
                Service::class,
                Reservation::class
                       ],
            version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun sportCenterDao(): SportCenterDao

    abstract fun courtDao(): CourtDao

    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database").build()
                INSTANCE = instance

                instance
            }
        }
    }

}