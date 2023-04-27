package it.polito.mad.courtreservationapp.views

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Gender
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject

class ShowProfileFragment : Fragment(R.layout.fragment_profile) {

    private var username: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var email: String = ""
    private var address: String = ""
    private var gender: Gender = Gender.MALE
    private var height: Int = Int.MIN_VALUE
    private var weight: Double = Double.MIN_VALUE
    private var phone: String? = null
    private lateinit var photoPath: String

    private fun loadDefaultPrefs() {
        val sharedPrefs = activity?.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = sharedPrefs?.edit()

        val profileData = JSONObject().apply {
            put("username", username)
            put("firstname", firstName)
            put("lastname", lastName)
            put("email", email)
            put("address", address)
            put("gender", gender.toString())
            put("height", height)
            put("weight", weight)
            put("phone", phone)
            put("photoPath", "")
        }

        editor?.putString("profile", profileData.toString())
        editor?.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            activity?.checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            activity?.checkSelfPermission(Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION)
            requestPermissions(permission, 112)
        }

        val sharedPrefs = activity?.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        Log.i("ShowProfile", "$sharedPrefs")
        val isFirstLaunch = sharedPrefs?.getBoolean("isFirstLaunch", true)
        Log.i("ShowProfile", "$isFirstLaunch")
        if(isFirstLaunch!!) {
            loadDefaultPrefs()
            val editor = sharedPrefs.edit()
            editor.putBoolean("isFirstLaunch", false)
            editor.apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.editButtonTV).setOnClickListener{
            launchProfileEdit()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val sharedPref = activity?.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val profileString = sharedPref?.getString("profile", null)
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

        activity?.findViewById<TextView>(R.id.usernameTV)?.text  = "@$username"
        activity?.findViewById<TextView>(R.id.fullname)?.text = if(firstName.isEmpty() && lastName.isEmpty()) "" else "$firstName $lastName"
        activity?.findViewById<TextView>(R.id.emailTV)?.text = email
        activity?.findViewById<TextView>(R.id.addressTV)?.text = address
        activity?.findViewById<TextView>(R.id.genderTV)?.text = newGender
        activity?.findViewById<TextView>(R.id.heightTV)?.text = if(height == Int.MIN_VALUE) null else "$height cm"
        activity?.findViewById<TextView>(R.id.weightTV)?.text = if(weight == Double.MIN_VALUE)null else "$weight kg"
        activity?.findViewById<TextView>(R.id.phoneTV)?.text = phone

        val pfpElement = activity?.findViewById<ImageView>(R.id.imageView3)
        val photoUri = DiskUtil.getUriFromPath(photoPath)
        if(photoUri != null){
            pfpElement?.setImageURI(photoUri)
        } else {
            pfpElement?.setImageResource(R.drawable.default_pfp)
        }
    }

    fun launchProfileEdit() {
        val editProfileIntent = Intent(activity, EditProfileActivity::class.java)
        startActivity(editProfileIntent)
    }
}