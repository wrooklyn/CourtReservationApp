package it.polito.mad.courtreservationapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Gender
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.UserViewModel
import it.polito.mad.utils.BitmapUtil
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class EditProfileActivity : AppCompatActivity() {

    private var username : String? = null
    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var address : String? = null

    private var gender : Gender? = null
    private var height : Int = Int.MIN_VALUE
    private var weight : Double = Double.MIN_VALUE
    private var phone : String?  = null

    private lateinit var mainLL: ConstraintLayout

    private lateinit var photoFile: File

    private var newPhotoUri : Uri? = null
    private var newPhotoPath: String? = null

    private val userViewModel: UserViewModel by viewModels()

    private val galleryActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val inputImage: Bitmap = BitmapUtil.uriToBitmap(result.data?.data!!, this)!!
            val pfpElement = findViewById<ImageView>(R.id.imageView3)
            pfpElement.setImageBitmap(inputImage)
            val imageURI: Uri = result.data?.data!!
            val imagePath = DiskUtil.getImagePathFromUri(imageURI, this)

            photoFile = DiskUtil.getFileFromPath(imagePath)!!

        }
    }

    //capture the image using camera and display it
    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val inputImage = BitmapUtil.uriToBitmap(newPhotoUri!!, this)
            val pfpElement = findViewById<ImageView>(R.id.imageView3)

            pfpElement.setImageBitmap(inputImage)

            photoFile = DiskUtil.getSaveFile(this)

            try {
                val fOut = FileOutputStream(photoFile)
                inputImage?.compress(Bitmap.CompressFormat.PNG, 30, fOut)

            } catch (e: IOException) {
                Log.e("DEBUG", "Error on ${photoFile.absoluteFile}")
                e.printStackTrace()
            }
        }
    }

    private fun loadDataFromSharedPreferences() {
        val userInfo = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        if (userInfo != null) {
            username = userInfo.getString("username", "")!!
            firstName = userInfo.getString("firstname", "")!!
            lastName = userInfo.getString("lastname", "")!!
            email = userInfo.getString("email", "")!!
            address = userInfo.getString("address", "")!!
            phone = userInfo.getString("phone", "")!!
            when(userInfo.getInt("gender", -10)) {
                0 -> gender = Gender.MALE
                1 -> gender = Gender.FEMALE
                2 -> gender = Gender.OTHER
            }
            height = userInfo.getInt("height", Int.MIN_VALUE)
            weight = userInfo.getInt("weight", Int.MIN_VALUE).toDouble()

            val path = userInfo.getString("photoPath", "")!!
            photoFile = DiskUtil.getFileFromPath(path) ?: DiskUtil.getDefaultImage(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        mainLL = findViewById(R.id.mainLL)
        mainLL.foreground.alpha = 0

        // ask for permission of camera upon first launch of application
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112)
        }

        DiskUtil.createFolderInt(this)

        loadDataFromSharedPreferences()

        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        if(photoFile.isFile){
            val photoURI = Uri.fromFile(photoFile)
            pfpElement.setImageURI(photoURI)
        } else {
            pfpElement.setImageResource(R.drawable.profile_picture)
        }


        findViewById<TextView>(R.id.editFirstName).text =  firstName
        findViewById<TextView>(R.id.editLastName).text =  lastName
        findViewById<TextView>(R.id.editUsername).text = username
        findViewById<TextView>(R.id.editEmail).text = email
        findViewById<TextView>(R.id.editAddress).text = address

        val genderElement: Spinner = findViewById(R.id.editGender)
        val genderItems: List<Gender> = listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER)
        val adapter: ArrayAdapter<Gender> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderItems)
        genderElement.adapter = adapter
        genderElement.setSelection(gender?.ordinal ?: 0)

        findViewById<TextView>(R.id.editPhone).text = phone

        val heightView = findViewById<TextView>(R.id.editHeight)
        heightView.text = if(height != Int.MIN_VALUE) height.toString() else null
        heightView.doAfterTextChanged {
            val value = heightView.text.toString().toIntOrNull() ?: Int.MIN_VALUE
            if( value < 0 || value > 260){
                heightView.setTextColor(Color.RED)
            } else {
                heightView.setTextColor(Color.BLACK)
            }
        }

        val weightView = findViewById<TextView>(R.id.editWeight)
        weightView.text = if(weight != Double.MIN_VALUE) weight.toString() else null
        weightView.doAfterTextChanged {
            val value = weightView.text.toString().toDoubleOrNull() ?: Double.MIN_VALUE
            if( value < 0.0 || value > 150.0){
                weightView.setTextColor(Color.RED)
            } else {
                weightView.setTextColor(Color.BLACK)
            }
        }

        findViewById<ImageButton>(R.id.camera_button).setOnClickListener{
            modifyPicture(it)
        }

        findViewById<TextView>(R.id.confirmButtonTV).setOnClickListener{
            saveChanges()
        }
    }

    override fun onResume() {
        super.onResume()
        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        if(photoFile.isFile){
            val photoURI = Uri.fromFile(photoFile)
            pfpElement.setImageURI(photoURI)
        } else {
            pfpElement.setImageResource(R.drawable.profile_picture)
        }
    }

    @SuppressLint("InflateParams")
    private fun saveChanges() {
        if(validateData()){
            username = findViewById<TextView>(R.id.editUsername).text.toString()
            firstName = findViewById<TextView>(R.id.editFirstName).text.toString()
            lastName = findViewById<TextView>(R.id.editLastName).text.toString()
            email = findViewById<TextView>(R.id.editEmail).text.toString()
            address = findViewById<TextView>(R.id.editAddress).text.toString()
            gender = findViewById<Spinner>(R.id.editGender).selectedItem as Gender
            height = findViewById<TextView>(R.id.editHeight).text.toString().toIntOrNull() ?: Int.MIN_VALUE
            weight = findViewById<TextView>(R.id.editWeight).text.toString().toDoubleOrNull() ?: Double.MIN_VALUE
            phone = findViewById<TextView>(R.id.editPhone).text.toString()
            val sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()

            JSONObject()
            editor.putString("username", username)
            editor.putString("firstname", firstName)
            editor.putString("lastname", lastName)
            editor.putString("email", email)
            editor.putString("address", address)
            editor.putInt("gender", gender!!.ordinal)
            editor.putInt("height", height)
            editor.putInt("weight", weight.toInt())
            editor.putString("phone", phone)
            editor.putString("photoPath", photoFile.absolutePath)

            editor.apply()
            userViewModel.updateUser(User(
                username!!,
                firstName!!,
                lastName!!,
                email!!,
                address!!,
                gender!!.ordinal,
                height,
                weight.toInt(),
                phone!!,
                sharedPref.getLong("UserId", -1)))
            finish()
        } else {
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_warning, null)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it
            val popupWindow = PopupWindow(popupView, width, height, focusable)
            val dismissButton = popupView.findViewById<Button>(R.id.dismissButton)

            popupWindow.showAtLocation(findViewById(R.id.mainLL), Gravity.CENTER, 0, 0)
            mainLL.foreground.alpha = 160

            popupWindow.setOnDismissListener {
                mainLL.foreground.alpha = 0
            }

            dismissButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }

    }

    private fun revertChanges(){
        val sharedPref = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        var path = ""

        if (sharedPref != null) {
            path = sharedPref.getString("photoPath", null) ?: ""
        }
        if(!newPhotoPath.isNullOrEmpty()){
            DiskUtil.deleteFile(newPhotoPath!!)
        }
        if (path != photoFile.absolutePath){
            photoFile.delete()
        }
    }

    private fun modifyPicture(view: View){
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_photo, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.gallery -> {
                    // Code to run when menu item 1 is clicked
                    fromGallery()
                    true
                }
                R.id.camera -> {
                    // Code to run when menu item 2 is clicked
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
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        newPhotoUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        newPhotoPath = DiskUtil.getImagePathFromUri(newPhotoUri!!, this)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri)

        cameraActivityResultLauncher.launch(cameraIntent)
    }
    private fun fromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(galleryIntent)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("InflateParams")
    override fun onBackPressed() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        val yesButton = popupView.findViewById<Button>(R.id.yesButton)
        val noButton = popupView.findViewById<Button>(R.id.noButton)

        popupWindow.showAtLocation(findViewById(R.id.mainLL), Gravity.CENTER, 0, 0)
        mainLL.foreground.alpha = 160

        popupWindow.setOnDismissListener {
            mainLL.foreground.alpha = 0
        }

        yesButton.setOnClickListener{
            saveChanges()
            popupWindow.dismiss()
            finish()
        }

        noButton.setOnClickListener {
            popupWindow.dismiss()
            revertChanges()
            finish()
        }

    }

    private fun validateData(): Boolean{
        var success = true
        val height = findViewById<TextView>(R.id.editHeight).text.toString().toIntOrNull() ?: Int.MIN_VALUE
        val weight = findViewById<TextView>(R.id.editWeight).text.toString().toDoubleOrNull() ?: Double.MIN_VALUE
        if(height <= 0 || height > 260) {
            success = false
        }
        if( weight <= 0 || weight > 150) {
            success = false
        }

        return success
    }
}