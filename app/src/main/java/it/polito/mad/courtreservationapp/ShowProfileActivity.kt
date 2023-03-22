package it.polito.mad.courtreservationapp

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
    private var username = "Gesú"
    var firstName = ""
    var surname = ""
    var email = ""
    var password = ""
    var address = ""
    var height = 0.0
    var weight = 0.0
    var birth = Calendar.getInstance()
    var gender = Gender.FEMALE
    var phone = ""
    var bio = ""
    var photo:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)
        val usernameElement = findViewById<TextView>(R.id.username)
        usernameElement.text = username
        val pfpElement=findViewById<ImageView>(R.id.imageView3)
        pfpElement.setImageResource(R.drawable.gesu)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
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
        username = "Cristo"
        val usernameElement = findViewById<TextView>(R.id.username)
        usernameElement.text = username
    }
}