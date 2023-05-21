package it.polito.mad.courtreservationapp.views.profile

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
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Gender
import it.polito.mad.courtreservationapp.views.AchievementSection
import it.polito.mad.courtreservationapp.views.EditProfileActivity
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.login.Login
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
    private var weight: Int = Int.MIN_VALUE
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
        Log.i("Show Profile", "On Create")
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
        val isFirstLaunch = sharedPrefs?.getBoolean("isFirstLaunch", true)
        if(isFirstLaunch!!) {
            // loadDefaultPrefs()
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
        Log.i("Show Profile", "On CreateView")
        val view= inflater.inflate(R.layout.fragment_profile, container, false)
        view.findViewById<ConstraintLayout>(R.id.mainLL).foreground.alpha = 0
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Show Profile", "On ViewCreated")
        view.findViewById<TextView>(R.id.editButtonTV).setOnClickListener{
            launchProfileEdit()
        }
        val composeView = view.findViewById<ComposeView>(R.id.composeContainer)
        composeView.setContent {
            val userWithSportMasteriesAndName = (activity as MainActivity).userViewModel.userWithSportMasteriesAndName
            AchievementSection(activity as MainActivity, userWithSportMasteriesAndName)
        }
        view.findViewById<TextView>(R.id.logoutTV).setOnClickListener(){
            val a = (activity as MainActivity)
            a.mGoogleSignInClient.signOut().addOnCompleteListener {
                a.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit().clear().apply()
                val intent= Intent(a, Login::class.java)
                startActivity(intent)
                a.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        val userInfo = activity?.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        //val profileString = sharedPref?.getString("profile", null)
        var newGender = gender.toString()

        if (userInfo != null) {
            //val profileData = JSONObject(profileString)
            username = userInfo.getString("username", "")!!
            firstName = userInfo.getString("firstname", "")!!
            lastName = userInfo.getString("lastname", "")!!
            email = userInfo.getString("email", "")!!
            address = userInfo.getString("address", "")!!
            phone = userInfo.getString("phone", "")!!
            when(userInfo.getInt("gender", -10)) {
                0 -> newGender = "Male"
                1 -> newGender = "Female"
                2 -> newGender = "Other"
            }
            height = userInfo.getInt("height", Int.MIN_VALUE)
            weight = userInfo.getInt("weight", Int.MIN_VALUE)

            photoPath = userInfo.getString("photoPath", "")!!
        }

        activity?.findViewById<TextView>(R.id.usernameTV)?.text  = "@$username"
        activity?.findViewById<TextView>(R.id.firstnameTV)?.text = firstName
        activity?.findViewById<TextView>(R.id.lastnameTV)?.text = lastName
        activity?.findViewById<TextView>(R.id.emailTV)?.text = email
        activity?.findViewById<TextView>(R.id.addressTV)?.text = address
        activity?.findViewById<TextView>(R.id.genderTV)?.text = newGender
        activity?.findViewById<TextView>(R.id.heightTV)?.text = if(height == Int.MIN_VALUE) null else "$height cm"
        activity?.findViewById<TextView>(R.id.weightTV)?.text = if(weight == Int.MIN_VALUE) null else "$weight kg"
        activity?.findViewById<TextView>(R.id.phoneTV)?.text = phone

        val pfpElement = activity?.findViewById<ImageView>(R.id.imageView2)
        val photoUri = DiskUtil.getUriFromPath(photoPath)
        if(photoUri != null){
            pfpElement?.setImageURI(photoUri)
        } else {
            pfpElement?.setImageResource(R.drawable.default_pfp)
        }
    }

    private fun launchProfileEdit() {
        val editProfileIntent = Intent(activity, EditProfileActivity::class.java)
        startActivity(editProfileIntent)
    }
}