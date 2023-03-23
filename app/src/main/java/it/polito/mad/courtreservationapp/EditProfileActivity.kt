package it.polito.mad.courtreservationapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class EditProfileActivity : AppCompatActivity() {
    var photo : String? = null
    private var username : String? = null
    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var password : String? = null
    private var address : String? = null
    private var birthDate : String? = null

    private var gender : Gender? = null
    private var height : Int = 0
    private var weight : Double = 0.0
    private var phone : String?  = null
    private var bio : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        getData()

        val pfpElement=findViewById<ImageView>(R.id.imageView3)
        pfpElement.setImageResource(R.drawable.gesu)
        val firstNameElement = findViewById<TextView>(R.id.editFirstName)
        firstNameElement.text =  firstName
        val lastNameElement = findViewById<TextView>(R.id.editLastName)
        lastNameElement.text =  lastName
        val usernameElement = findViewById<TextView>(R.id.editUsername)
        usernameElement.text = username
        val emailElement = findViewById<TextView>(R.id.editEmail)
        emailElement.text = email
        val addressElement = findViewById<TextView>(R.id.editAddress)
        addressElement.text = address

        val genderElement = findViewById<TextView>(R.id.editGender)
        genderElement.text = gender?.toString()
        val phoneElement = findViewById<TextView>(R.id.editPhone)
        phoneElement.text = phone
        val heightElement = findViewById<TextView>(R.id.editHeight)
        heightElement.text = if(height != 0) height.toString() else null
        val weightElement = findViewById<TextView>(R.id.editWeight)
        weightElement.text = if(weight != 0.0) weight.toString() else null
    }

    private fun getData(){
        username = intent.getStringExtra("username")
        firstName = intent.getStringExtra("firstname")
        lastName = intent.getStringExtra("lastname")
        email = intent.getStringExtra("email")
        address = intent.getStringExtra("address")
        val genderString = intent.getStringExtra("gender")
        gender = when(genderString) {
            "FEMALE" -> Gender.FEMALE
            "MALE" -> Gender.MALE
            "OTHER" -> Gender.OTHER
            else -> null
        }
        height = intent.getIntExtra("height", 0)
        weight = intent.getDoubleExtra("weight", 0.0)
        intent.putExtra("weight", weight)
        intent.putExtra("phone", phone)


    }
}