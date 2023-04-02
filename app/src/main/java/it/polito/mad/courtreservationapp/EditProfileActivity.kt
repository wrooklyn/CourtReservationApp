package it.polito.mad.courtreservationapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EditProfileActivity : AppCompatActivity() {
    private var photo : Uri? = null
    private var username : String? = null
    private var firstName : String? = null
    private var lastName : String? = null
    private var email : String? = null
    private var address : String? = null

    private var gender : Gender? = null
    private var height : Int = 0
    private var weight : Double = 0.0
    private var phone : String?  = null

    private lateinit var mainLL: LinearLayout

    private lateinit var filename: File

    private val galleryActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //bitmap
            val inputImage: Bitmap = uriToBitmap(result.data?.data!!)!!
//            Log.i("Gallery", "Gallery URI: ${result.data?.toString()}")
            val pfpElement = findViewById<ImageView>(R.id.imageView3)
            pfpElement.setImageBitmap(inputImage)

            val imageURI: Uri = result.data?.data!!
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(imageURI, filePathColumn, null, null, null)
            cursor?.moveToFirst()

            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val imagePath = cursor?.getString(columnIndex!!)
            cursor?.close()
            Log.i("Gallery", "Gallery path: $imagePath")
            filename = imagePath?.let { File(it) }!!



        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //TODO capture the image using camera and display it
    private val cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
//        Log.i("DBG", "Result cb: $photo")
        if (result.resultCode == Activity.RESULT_OK) {
            val inputImage = uriToBitmap(photo!!)
            val date = Calendar.getInstance().time


            val pfpElement = findViewById<ImageView>(R.id.imageView3)
            Log.i("D", "result is: $photo")
            //pfpElement.setImageURI(photo)
            pfpElement.setImageBitmap(inputImage)

            val path = DiskUtil.getInternalFolder(this)
            filename = File(path, "$date.jpg")
            try {
                val fOut = FileOutputStream(filename)
                inputImage?.compress(Bitmap.CompressFormat.PNG, 100, fOut)

            }catch(e: IOException) {
                e.printStackTrace()
            }
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
            height = profileData.optInt("height",-1)
            weight = profileData.optDouble("weight",-1.0)
            val photoString = profileData.optString("photo", "")
            if (!photoString.isNullOrEmpty()) {
                photo = Uri.parse(photoString)
            }

            val path = profileData.optString("photopath", "")
            filename = File(path)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        mainLL = findViewById(R.id.mainLL)
        mainLL.foreground.alpha = 0
        Log.i("Background", "alpha value${mainLL.foreground.alpha}")
        //TODO ask for permission of camera upon first launch of application
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
               Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112)
        }

        DiskUtil.createFolderInt(this)
        DiskUtil.createRootFolderExt()

        loadDataFromSharedPreferences()

        val pfpElement = findViewById<ImageView>(R.id.imageView3)
        if(filename.isFile){
            val photoURI = Uri.fromFile(filename)
            pfpElement.setImageURI(photoURI)
        } else {
            pfpElement.setImageResource(R.drawable.gesu)
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
        val genderItems: List<Gender> = listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER)
        val adapter: ArrayAdapter<Gender> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderItems)
        genderElement.adapter = adapter
        genderElement.setSelection(gender?.ordinal ?: 0)
        val phoneElement = findViewById<TextView>(R.id.editPhone)
        phoneElement.text = phone
        val heightElement = findViewById<TextView>(R.id.editHeight)
        heightElement.text = if(height != -1) height.toString() else null
        val weightElement = findViewById<TextView>(R.id.editWeight)
        weightElement.text = if(weight != -1.0) weight.toString() else null

        val cameraButton = findViewById<ImageButton>(R.id.camera_button)
        cameraButton.setOnClickListener{
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
        Log.i("DBG", "$item")
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
        profileData.put("photopath", filename)
        if (photo != null) {
            profileData.put("photo", photo.toString())
        } else {
            profileData.put("photo", "")
        }

        editor.putString("profile", profileData.toString())
        editor.apply()
        finish()

    }

    private fun selectPhoto(view: View){
        val tag = "PHOTO"
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
        Log.i("D", "$photo")
        cameraActivityResultLauncher.launch(cameraIntent)
    }
    private fun fromGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(galleryIntent)
    }

    override fun onBackPressed() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        mainLL.foreground.alpha = 160
        Log.i("Background", "alpha value${mainLL.foreground.alpha}")
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAtLocation(findViewById(R.id.mainLL), Gravity.CENTER, 0, 0)

//        popupView.setOnTouchListener { view, motionEvent ->
//            mainLL.foreground.alpha = 0
//            Log.i("Background", "alpha value${mainLL.foreground.alpha}")
//            true
//        }

        popupWindow.setOnDismissListener {
            mainLL.foreground.alpha = 0
            Log.i("Background", "alpha value${mainLL.foreground.alpha}")
        }


        val yesButton = popupView.findViewById<Button>(R.id.yesButton)
        yesButton.setOnClickListener{
            saveChanges()
            popupWindow.dismiss()
            finish()
        }
        val noButton = popupView.findViewById<Button>(R.id.noButton)
        noButton.setOnClickListener {
            popupWindow.dismiss()
            finish()
        }


    }

}