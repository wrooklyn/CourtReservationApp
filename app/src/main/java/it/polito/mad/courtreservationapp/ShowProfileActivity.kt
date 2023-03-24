package it.polito.mad.courtreservationapp
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONObject
import java.util.*

enum class Gender{MALE, FEMALE, OTHER}
class ShowProfileActivity : AppCompatActivity() {
    private var photo : Uri? = null
    private var username = "Gesú"
    private var firstName = "Gabri"
    private var lastName = "Fine"
    private var email = "email@email.it"
    private var password = ""
    private var address = "via carnival in Brazil 3"
    private var birthDate = Calendar.getInstance()

    private var gender : Gender? = null
    private var height : Int? = null
    private var weight : Double? = null
    private var phone : String?  = null
    private var bio : String? = null

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
        }

        editor.putString("profile", profileData.toString())
        editor.apply()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

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
        if (photo == null || photo.toString().isEmpty()) {
            pfpElement.setImageResource(R.drawable.gesu)
        } else {
            pfpElement.setImageURI(photo)
        }

    }
}