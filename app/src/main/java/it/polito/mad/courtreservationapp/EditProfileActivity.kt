package it.polito.mad.courtreservationapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    var photo : Uri? = null
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

    private val galleryActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val tag = "PHOTO"
            photo = result.data?.data
            val pfpElement = findViewById<ImageView>(R.id.imageView3)
            pfpElement.setImageURI(photo)
        }
    }

    //TODO capture the image using camera and display it
    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val pfpElement = findViewById<ImageView>(R.id.imageView3)
            pfpElement.setImageURI(photo)
        }
    }

    private fun loadDataFromSharedPreferences() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val profileString = sharedPref.getString("profile", null)

        if (profileString != null) {
            val profileData = JSONObject(profileString)
            username = profileData.optString("username", "")
            firstName = profileData.optString("firstname", "")
            lastName = profileData.optString("lastname", "")
            email = profileData.optString("email", "")
            address = profileData.optString("address", "")
            phone = profileData.optString("phone", "")
            val genderString = profileData.optString("gender", "")
            gender = when (genderString) {
                "FEMALE" -> Gender.FEMALE
                "MALE" -> Gender.MALE
                "OTHER" -> Gender.OTHER
                else -> null
            }
            height = profileData.optInt("height",0)
            weight = profileData.optDouble("weight",0.0)
            val photoString = profileData.optString("photo", "")
            if (!photoString.isNullOrEmpty()) {
                photo = Uri.parse(photoString)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //TODO ask for permission of camera upon first launch of application
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                   Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_DENIED
            ) {
                val permission = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 112)
            }
        }

        loadDataFromSharedPreferences()

        val pfpElement = findViewById<ImageView>(R.id.imageView3)

        if (photo == null || photo.toString().isEmpty()) {
            pfpElement.setImageResource(R.drawable.gesu)
        } else {
            pfpElement.setImageURI(photo)
        }
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

        val genderElement: Spinner = findViewById(R.id.editGender)
        val genderItems: List<Gender> = listOf(Gender.FEMALE, Gender.MALE, Gender.OTHER)
        val adapter: ArrayAdapter<Gender> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderItems)
        genderElement.adapter = adapter
        val phoneElement = findViewById<TextView>(R.id.editPhone)
        phoneElement.text = phone
        val heightElement = findViewById<TextView>(R.id.editHeight)
        heightElement.text = if(height != 0) height.toString() else null
        val weightElement = findViewById<TextView>(R.id.editWeight)
        weightElement.text = if(weight != 0.0) weight.toString() else null

        val cameraButton = findViewById<LinearLayout>(R.id.camera_button)
        cameraButton.setOnClickListener(){
            selectPhoto(it)
        }
    }

    //setting appbar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.save_changes -> {
                saveChanges()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveChanges() {
        val sharedPref = getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        val profileData = JSONObject()
        profileData.put("username", findViewById<TextView>(R.id.editUsername).text.toString())
        profileData.put("firstname", findViewById<TextView>(R.id.editFirstName).text.toString())
        profileData.put("lastname", findViewById<TextView>(R.id.editLastName).text.toString())
        profileData.put("email", findViewById<TextView>(R.id.editEmail).text.toString())
        profileData.put("address", findViewById<TextView>(R.id.editAddress).text.toString())
        profileData.put("gender", (findViewById<Spinner>(R.id.editGender).selectedItem as Gender).toString())
        profileData.put("height", findViewById<TextView>(R.id.editHeight).text.toString().toIntOrNull() ?: 0)
        profileData.put("weight", findViewById<TextView>(R.id.editWeight).text.toString().toDoubleOrNull() ?: 0.0)
        profileData.put("phone", findViewById<TextView>(R.id.editPhone).text.toString())
        if (photo != null) {
            profileData.put("photo", photo.toString())
        } else {
            profileData.put("photo", "")
        }

        editor.putString("profile", profileData.toString())
        editor.apply()

        val myIntent = Intent(this, ShowProfileActivity::class.java)
        startActivity(myIntent)

    }

    private fun selectPhoto(view: View){
        val tag: String = "PHOTO"
        Log.i(tag, "Starting")
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_photo, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.gallery -> {
                    // Code to run when menu item 1 is clicked
                    Log.i(tag, "Selected gallery")
                    fromGallery()
                    true
                }
                R.id.camera -> {
                    // Code to run when menu item 2 is clicked
                    Log.i(tag, "Selected camera")
                    Log.i(Manifest.permission.CAMERA, "Manifest Camera")
                    Log.i(Manifest.permission.WRITE_EXTERNAL_STORAGE, "external storage")
                    Log.i(PackageManager.PERMISSION_DENIED.toString(), "permission denied")
                    if (Build.VERSION.SDK_INT <= 30) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            == PackageManager.PERMISSION_DENIED
                        ) {
                            val permission =
                                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            requestPermissions(permission, 112)
                        } else {
                            fromCamera()
                        }
                    } else {
                        fromCamera()
                    }

                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun fromCamera(){
        val values = ContentValues()
        Log.i("PHOTO", "Opening camera")

        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        photo = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photo)
        cameraActivityResultLauncher.launch(cameraIntent)
    }
    private fun fromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(galleryIntent)
    }
}