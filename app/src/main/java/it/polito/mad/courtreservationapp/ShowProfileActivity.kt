package it.polito.mad.courtreservationapp
import android.content.Intent
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
    var photo : String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        //setting the values of the fields
        val pfpElement=findViewById<ImageView>(R.id.imageView3)
        pfpElement.setImageResource(R.drawable.gesu)
        val fullNameElement = findViewById<TextView>(R.id.fullname)
        fullNameElement.text =  "$firstName $lastName"
        val usernameElement = findViewById<TextView>(R.id.username)
        usernameElement.text = username
        val emailElement = findViewById<TextView>(R.id.email)
        emailElement.text = email
        val addressElement = findViewById<TextView>(R.id.address)
        addressElement.text = address

        val genderElement = findViewById<TextView>(R.id.gender)
        genderElement.text = if(gender != null) gender.toString() else "-"
        val phoneElement = findViewById<TextView>(R.id.phone)
        phoneElement.text = if(phone != null) phone else "-"
        val heightElement = findViewById<TextView>(R.id.height)
        heightElement.text = if(height != null) "$height cm" else "-"
        val weightElement = findViewById<TextView>(R.id.weight)
        weightElement.text = if(weight != null) "$weight kg" else "-"


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

    fun editUsername() {
        //todo start a new intent
        val myIntent = Intent(this, EditProfileActivity::class.java)
        passData(myIntent)
        startActivity(myIntent)
    }

    fun passData(intent: Intent){
        intent.putExtra("username", username)
        intent.putExtra("firstname", firstName)
        intent.putExtra("lastname", lastName)
        intent.putExtra("email", email)
        intent.putExtra("address", address)
        intent.putExtra("gender", gender)
        intent.putExtra("height", height)
        intent.putExtra("weight", weight)
        intent.putExtra("phone", phone)
    }
}