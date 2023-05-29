package it.polito.mad.courtreservationapp.views.reservationManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Gender
import it.polito.mad.courtreservationapp.utils.DiskUtil
import org.json.JSONObject
import org.w3c.dom.Text

class ShowSummaryFragment : Fragment(R.layout.summary_layout) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.summary_layout, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val a = (activity as CreateReservationActivity)
        view.findViewById<ConstraintLayout>(R.id.mainLL).foreground.alpha=0
        val sportCenterName: TextView = view.findViewById(R.id.centerName)
        val addressSubtitle: TextView = view.findViewById(R.id.addressSubtitle)
        val address: TextView = view.findViewById(R.id.addressTV)
        val username: TextView = view.findViewById(R.id.usernameTV)
        val date: TextView = view.findViewById(R.id.dateTV)
        val timeslot: TextView = view.findViewById(R.id.timeslotTV)
        val courtname: TextView = view.findViewById(R.id.courtnameTV)
        val servicesTitle: TextView = view.findViewById(R.id.servicesTitle)
        val services: TextView = view.findViewById(R.id.servicesTV)
        val params: ViewGroup.LayoutParams = view.findViewById<ConstraintLayout>(R.id.serviceCL).layoutParams
        val ratingBar:RatingBar = view.findViewById(R.id.ratingBar)
        val reviewsTv: TextView = view.findViewById(R.id.textView6)

        sportCenterName.text = a.viewModel.sportCenter.name
        addressSubtitle.text = a.viewModel.sportCenter.address
        address.text = a.viewModel.sportCenter.address
        username.text = a.viewModel.user.username
        date.text = a.viewModel.reservationDate
        a.viewModel.reservationTimeSlots.sort()

        ratingBar.rating = a.viewModel.rating.toFloat()
        reviewsTv.text = a.viewModel.reviews


        val slotStr: String = a.viewModel.reservationTimeSlots.fold("") { acc, i ->
            val startH = 10 + i
            val endH = startH + 1
            "$acc$startH:00 - $endH:00\n"
        }

        timeslot.text = slotStr;
        courtname.text = "${a.viewModel.courtWithServices.court.sportName} court - ${a.viewModel.courtWithServices.court.courtId}"

        services.text = a.viewModel.getServicesInfo()

        if (a.viewModel.reservationServices.isNullOrEmpty() && a.viewModel.reservationRequests.isNullOrEmpty()) {
            servicesTitle.visibility = View.INVISIBLE
            services.visibility = View.INVISIBLE
            params?.height = 0
            view?.findViewById<ConstraintLayout>(R.id.serviceCL)?.layoutParams=params
        }


        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener {
            showConfirmationPopup(activity as CreateReservationActivity)
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener {
            a.ggBack()
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            a.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
    }

    private fun showConfirmationPopup(a: CreateReservationActivity){
        val inflater = a.getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_window, null)
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, width, height, true)
        val layout = a.findViewById<ConstraintLayout>(R.id.mainLL)
        val message = popupView.findViewById<TextView>(R.id.text)
        message.text = "Are you sure you want to reserve those time slots?"
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0)
        layout.foreground.alpha = 160

        popupWindow.setOnDismissListener {
            layout.foreground.alpha = 0
        }
        popupView.findViewById<Button>(R.id.noButton).setOnClickListener {
            popupWindow.dismiss()
        }
        popupView.findViewById<Button>(R.id.yesButton).setOnClickListener {
            a.commitReservation()
            Toast.makeText(activity, "Court successfully reserved", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }


}