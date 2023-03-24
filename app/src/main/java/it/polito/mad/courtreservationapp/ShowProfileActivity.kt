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
import java.util.*

enum class Gender{MALE, FEMALE, OTHER}
class ShowProfileActivity : AppCompatActivity() {
    var photo : Uri? = null
    private var username = "Gesú"
    private var firstName = "Gabri"
    private var lastName = "Fine"
    private var email = "email@email.it"
    private var password = ""
    private var address = "via carnival in Brazil 3"
    private var birthDate = Calendar.getInstance()

    private var gender : Gender? = null
    private var height : Int? = null
    private var weight : Float? = null
    private var phone : String?  = null
    private var bio : String? = null

    private fun storeInitialValues() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("username", username)
        editor.putString("firstname", firstName)
        editor.putString("lastname", lastName)
        editor.putString("email", email)
        editor.putString("address", address)
        editor.putString("gender", gender?.toString())
        editor.putInt("height", height ?: 0)
        editor.putFloat("weight", weight?.toFloat() ?: 0f)
        editor.putString("phone", phone)
        //editor.putString("photo", photo?.toString()) // Store the photo URI as a string

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
        /*val photoString = sharedPref.getString("photo", null)
        if (photoString != null) {
            photo = Uri.parse(photoString)
        }

        val pfpElement = findViewById<ImageView>(R.id.imageView3)

        if (photo == null || photo.toString().isEmpty()) {
            pfpElement.setImageResource(R.drawable.gesu)
        } else {
            pfpElement.setImageURI(photo)
        }*/
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
        username = sharedPref.getString("username", "").toString()
        firstName = sharedPref.getString("firstname", "").toString()
        lastName = sharedPref.getString("lastname", "").toString()
        email = sharedPref.getString("email", "").toString()
        address = sharedPref.getString("address", "").toString()
        val newGender = sharedPref.getString("gender", "")
        height = sharedPref.getInt("height", 0)
        weight = sharedPref.getFloat("weight", 0f)
        phone = sharedPref.getString("phone", "")
        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.fullname).text = "$firstName $lastName"
        findViewById<TextView>(R.id.email).text = email
        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.gender).text = newGender
        findViewById<TextView>(R.id.height).text = height.toString()
        findViewById<TextView>(R.id.weight).text = weight.toString()
        findViewById<TextView>(R.id.phone).text = phone
        val photoString = sharedPref.getString("photo", "")
        if (!photoString.isNullOrEmpty()) {
            photo = Uri.parse(photoString)
        }

        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        if (photo == null || photo.toString().isEmpty()) {
            pfpElement.setImageResource(R.drawable.gesu)
        } else {
            pfpElement.setImageURI(photo)
        }
    }
}