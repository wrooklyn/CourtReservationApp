package it.polito.mad.courtreservationapp
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject
import java.util.*

enum class Gender{MALE, FEMALE, OTHER}
class ShowProfileActivity : AppCompatActivity() {

    private var username = ""
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var address = ""

    private var gender : Gender? = null
    private var height : Int = Int.MIN_VALUE
    private var weight : Double = Double.MIN_VALUE
    private var phone : String?  = null

    private lateinit var photoPath: String

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
            put("height", height)
            put("weight", weight)
            put("phone", phone)
            put("photoPath", "")
        }

        editor.putString("profile", profileData.toString())
        editor.apply()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112)
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
        val myIntent = Intent(this, EditProfileActivity::class.java)
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

        if (profileString != null) {
            val profileData = JSONObject(profileString)
            username = profileData.optString("username", "")
            firstName = profileData.optString("firstname", "")
            lastName = profileData.optString("lastname", "")
            email = profileData.optString("email", "")
            address = profileData.optString("address", "")
            phone = profileData.optString("phone", "")
            newGender = profileData.optString("gender", "")
            height = profileData.optInt("height", Int.MIN_VALUE)
            weight = profileData.optDouble("weight", Double.MIN_VALUE)

            photoPath = profileData.optString("photoPath", "")
        }

        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.fullname).text = if(firstName.isEmpty() && lastName.isEmpty()) "" else "$firstName $lastName"
        findViewById<TextView>(R.id.email).text = email
        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.gender).text = newGender
        findViewById<TextView>(R.id.height).text = if(height == Int.MIN_VALUE) null else height.toString()
        findViewById<TextView>(R.id.weight).text = if(weight == Double.MIN_VALUE)null else weight.toString()
        findViewById<TextView>(R.id.phone).text = phone

        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        val photoUri = DiskUtil.getUriFromPath(photoPath)
        if(photoUri != null){
            pfpElement.setImageURI(photoUri)
        } else {
            pfpElement.setImageResource(R.drawable.default_pfp)
        }


    }

}