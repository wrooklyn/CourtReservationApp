package it.polito.mad.courtreservationapp
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

enum class Gender{MALE, FEMALE, OTHER}
class ShowProfileActivity : AppCompatActivity() {
    private var photo : Uri? = null
    private var username = ""
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var address = ""

    private var gender : Gender? = null
    private var height : Int? = null
    private var weight : Double? = null
    private var phone : String?  = null

    lateinit var filename: File

    private fun storeInitialValues() {

        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val profileData = JSONObject().apply {
            put("username", username)
            put("firstname", firstName)
            put("lastname", lastName)
            put("email", email)
            put("address", address)
            put("gender", gender?.toString())
            put("height", height ?: 0)
            put("weight", weight?.toFloat() ?: 0f)
            put("phone", phone)
            put("photo",photo?.toString())
            put("photopath", "")
        }

        editor.putString("profile", profileData.toString())
        editor.apply()

        Log.i("Pref", "${profileData.optString("photopath")}")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 112)
            }
        }

        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

        val isFirstLaunch = sharedPref.getBoolean("isFirstLaunch", true)
        if (isFirstLaunch) {
            storeInitialValues()

            val editor = sharedPref.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.apply()
        }

    }

    //setting appbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_edit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit -> {
                editUsername()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun editUsername() {
        //todo start a new intent
        val myIntent = Intent(this, EditProfileActivity::class.java)
        //passData(myIntent)
        startActivity(myIntent)

    }

    //show updated values
    override fun onResume() {
        super.onResume()
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val profileString = sharedPref.getString("profile", null)
        var newGender = gender.toString()
        var path = ""

        if (profileString != null) {
            val profileData = JSONObject(profileString)
            username = profileData.optString("username", "")
            firstName = profileData.optString("firstname", "")
            lastName = profileData.optString("lastname", "")
            email = profileData.optString("email", "")
            address = profileData.optString("address", "")
            phone = profileData.optString("phone", "")
            newGender = profileData.optString("gender", newGender)
            height = profileData.optInt("height",0)
            weight = profileData.optDouble("weight",0.0)
            val photoString = profileData.optString("photo", "")
            if (!photoString.isNullOrEmpty()) {
                photo = Uri.parse(photoString)
            }

             path = profileData.optString("photopath", "")
        }
        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.fullname).text = "$firstName $lastName"
        findViewById<TextView>(R.id.email).text = email
        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.gender).text = newGender
        findViewById<TextView>(R.id.height).text = height.toString()
        findViewById<TextView>(R.id.weight).text = weight.toString()
        findViewById<TextView>(R.id.phone).text = phone

        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        if(path.isNotEmpty() && File(path).isFile){
            filename = File(path)
            val photoURI = Uri.fromFile(filename)
            pfpElement.setImageURI(photoURI)
        } else {
            pfpElement.setImageResource(R.drawable.gesu)
        }

//        if (photo == null || photo.toString().isEmpty()) {
//            pfpElement.setImageResource(R.drawable.gesu)
//        } else {
//            pfpElement.setImageURI(photo)
//        }

    }

}