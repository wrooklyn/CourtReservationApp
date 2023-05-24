package it.polito.mad.courtreservationapp.db.remoteRepository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.utils.ServiceUtils
import kotlinx.coroutines.tasks.await

class FireSportCenterRepository(val application: Application) {
    val db: FirebaseFirestore = RemoteDataSource.instance
    val storage: FirebaseStorage = RemoteDataSource.storageInstance



    fun getAll(): LiveData<List<SportCenter>> {
        //return sportCenterDao.getAll()
        return MutableLiveData(); //TODO
    }

    suspend fun getImage(sportCenter: SportCenter){

    }

    suspend fun getById(id: String): SportCenter {

        val sportCenterDoc = db.collection("sport-centers").document(id).get().await()
        val scName = (sportCenterDoc.data?.get("name") as String?).toString()
        val scAddress = (sportCenterDoc.data?.get("address") as String?).toString()
        val scDescription = (sportCenterDoc.data?.get("description") as String?).toString()
        val image: String? = sportCenterDoc.data?.get("image_name") as String?
        return SportCenter(scName, scAddress, scDescription, sportCenterDoc.id, image)
    }


    suspend fun getCenterWithCourts2(id: String): SportCenterWithCourts {


        val sportCenterDoc = db.collection("sport-centers").document(id).get().await()
        val scName = (sportCenterDoc.data?.get("name") as String?).toString()
        val scAddress = (sportCenterDoc.data?.get("address") as String?).toString()
        val scDescription = (sportCenterDoc.data?.get("description") as String?).toString()
//        val scId = sportCenterDoc.data?.get("id") as Long
        val image: String? = sportCenterDoc.data?.get("image_name") as String?
        val sportCenter = SportCenter(scName, scAddress, scDescription, sportCenterDoc.id, image)
        val courtsOfCenterRef = db.collection("sport-centers").document(id).collection("courts")
        val courtsList = mutableListOf<Court>()
        val courtSnapshot = courtsOfCenterRef.get().await()
        for (courtDocument in courtSnapshot?.documents.orEmpty()) {
//            println("xp ${courtDocument.data}")
            val cSportName = courtDocument.data?.get("sport_name") as String
            val image: String? = courtDocument.data?.get("image_name") as String?
            val c = Court(sportCenterDoc.id, cSportName, 0, courtDocument.id, image)
            courtsList.add(c)
        }
        return SportCenterWithCourts(sportCenter, courtsList)
    }


    fun getAllWithCourtsAndServices(): Task<List<SportCenterWithCourtsAndServices>> {

        val dataList = mutableListOf<SportCenterWithCourtsAndServices>()
        // Reference to your Firestore collection
        val sportCenterRef = db.collection("sport-centers")
        val innerTasks = mutableListOf<Task<*>>()
        return sportCenterRef.get().continueWithTask { task ->
            val sportCenterSnapshot = task.result
            for (document in sportCenterSnapshot?.documents.orEmpty()) {
                // Map Firestore document to YourDataModel and add it to dataList
//                println("help")
//                println(document.id)
                println(document.data)
                val scName = (document.data?.get("name") as String?).toString()
                val scAddress = (document.data?.get("address") as String?).toString()
                val scDescription = (document.data?.get("description") as String?).toString()
//                val scId = document.data?.get("id") as Long?
//                Log.i("FireSportRepo", "scId: $scId")
                val image: String? = document.data?.get("image_name") as String?
                val sportCenter = SportCenter(scName, scAddress, scDescription,document.id, image)
                val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
                val courtWithServices = mutableListOf<CourtWithServices>()

                val innerTask = courtsOfCenterRef.get().continueWithTask { task ->
                    val courtSnapshot = task.result
                    for (courtDocument in courtSnapshot?.documents.orEmpty()) {
//                        println("xp ${courtDocument.data}")
                        val cSportName = courtDocument.data?.get("sport_name") as String
                        val cServices = courtDocument.data?.get("services") as List<*>?
                        val image: String? = courtDocument.data?.get("image_name") as String?
                        val s = ServiceUtils.getServices(cServices as List<Long>)
                        val c = Court(document.id, cSportName, 0, courtDocument.id, image)
                        courtWithServices.add(CourtWithServices(c, s))
                    }
                    dataList.add(SportCenterWithCourtsAndServices(sportCenter, courtWithServices))
                    Tasks.forResult(null)
                }
                innerTasks.add(innerTask)
            }
            Tasks.whenAllComplete(innerTasks).continueWith { _ ->
                dataList
            }
        }
    }

    suspend fun getAllWithCourtsAndServices2(): List<SportCenterWithCourtsAndServices> {

        val dataList = mutableListOf<SportCenterWithCourtsAndServices>()
        // Reference to your Firestore collection
        val sportCenterRef = db.collection("sport-centers")

        val sportCenterSnapshot = sportCenterRef.get().await()
        for (document in sportCenterSnapshot.documents){
            val scName = (document.data?.get("name") as String?).toString()
            val scAddress = (document.data?.get("address") as String?).toString()
            val scDescription = (document.data?.get("description") as String?).toString()
            val image: String? = document.data?.get("image_name") as String?
            val sportCenter = SportCenter(scName, scAddress, scDescription,document.id, image)
            val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
            val courtWithServices = mutableListOf<CourtWithServices>()
            val courtsSnapshot = courtsOfCenterRef.get().await()

            for(courtDocument in courtsSnapshot.documents){
                val cSportName = courtDocument.data?.get("sport_name") as String
                val cServices = courtDocument.data?.get("services") as List<*>?
                val s = (cServices as List<Long>?)?.let { ServiceUtils.getServices(it) } ?: emptyList()
                val image: String? = courtDocument.data?.get("image_name") as String?
                val c = Court(document.id, cSportName, 0, courtDocument.id, image)
                courtWithServices.add(CourtWithServices(c, s))
            }
            dataList.add(SportCenterWithCourtsAndServices(sportCenter, courtWithServices))
        }


            /*
            val sportCenterSnapshot = task.result
            for (document in sportCenterSnapshot?.documents.orEmpty()) {
                // Map Firestore document to YourDataModel and add it to dataList
//                println("help")
//                println(document.id)
                println(document.data)
                val scName = (document.data?.get("name") as String?).toString()
                val scAddress = (document.data?.get("address") as String?).toString()
                val scDescription = (document.data?.get("description") as String?).toString()
//                val scId = document.data?.get("id") as Long?
//                Log.i("FireSportRepo", "scId: $scId")
                val sportCenter = SportCenter(scName, scAddress, scDescription,document.id)
                val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
                val courtWithServices = mutableListOf<CourtWithServices>()

                val innerTask = courtsOfCenterRef.get().continueWithTask { task ->
                    val courtSnapshot = task.result
                    for (courtDocument in courtSnapshot?.documents.orEmpty()) {
//                        println("xp ${courtDocument.data}")
                        val cSportName = courtDocument.data?.get("sport_name") as String
                        val cCourtId = 19L
                        val cServices = courtDocument.data?.get("services") as List<Long>?
                        val s = cServices?.map { e -> Service("desct temporary", e) } ?: listOf()
                        val c = Court(document.id, cSportName, 0, courtDocument.id)
                        courtWithServices.add(CourtWithServices(c, s))
                    }
                    dataList.add(SportCenterWithCourtsAndServices(sportCenter, courtWithServices))
                    Tasks.forResult(null)
                }
                innerTasks.add(innerTask)
            }
            Tasks.whenAllComplete(innerTasks).continueWith { _ ->
                dataList
            }*/
        return dataList
    }



    suspend fun getAllWithCourtsAndReviewsAndUsers(): List<SportCenterWIthCourtsAndReviewsAndUsers> {

        val dataList = mutableListOf<SportCenterWIthCourtsAndReviewsAndUsers>()
        // Reference to your Firestore collection
        val sportCenterRef = db.collection("sport-centers")


        val sportCenterSnapshot = sportCenterRef.get().await()
        for (document in sportCenterSnapshot?.documents.orEmpty()) {
            // Map Firestore document to YourDataModel and add it to dataList
//            println("help")
//            println(document.id)
            println(document.data)
            val scName: String = document.data?.get("name") as String
            val scAddress = document.data?.get("address") as String
            val scDescription = document.data?.get("description") as String
//            val scId = document.data?.get("id") as Long
            val image: String? = document.data?.get("image_name") as String?
            val sportCenter = SportCenter(scName, scAddress, scDescription, document.id, image)
            val courtsOfCenterRef = sportCenterRef.document(document.id).collection("courts")
            val courtWithReviewsAndUsersList = mutableListOf<CourtWithReviewsAndUsers>()


            val courtSnapshot = courtsOfCenterRef.get().await()
            for (courtDocument in courtSnapshot?.documents.orEmpty()) {
//                println("xp ${courtDocument.data}")
                val cSportName = courtDocument.data?.get("sport_name") as String
                val image: String? = courtDocument.data?.get("image_name") as String?
                val c = Court( document.id, cSportName, 0, courtDocument.id, image)


                val reviewsOfCourtRef =
                    courtsOfCenterRef.document(courtDocument.id).collection("reservations")
                val reviewsWithUsers = mutableListOf<ReviewWithUser>()
                val reservationSnapshot = reviewsOfCourtRef.get().await()
                for (reservationDocument in reservationSnapshot?.documents.orEmpty()) {
                    val rating = (reservationDocument.data?.get("rating") as Long?)?.toInt()
                    if (rating != null) {
                        val reviewText =
                            reservationDocument.data?.get("review_content") as String?
                        val reviewDate =
                            reservationDocument.data?.get("review_content") as String
                        val review = Review(courtDocument.id, null, reservationDocument.id, reviewText, rating, reviewDate, reservationDocument.id )

                        val username = reservationDocument.data?.get("user") as String
                        val user = User(username, "", "", "", "", 0, 0, 0, "", "")
                        reviewsWithUsers.add(ReviewWithUser(review, user))
                    }
                }
                val courtWithReviewsAndUsers = CourtWithReviewsAndUsers(c, reviewsWithUsers)
                courtWithReviewsAndUsersList.add(courtWithReviewsAndUsers)
            }
            val sportCenterWIthCourtsAndReviewsAndUsers = SportCenterWIthCourtsAndReviewsAndUsers(sportCenter, courtWithReviewsAndUsersList)
            dataList.add(sportCenterWIthCourtsAndReviewsAndUsers)
        }

        return dataList

    }


}