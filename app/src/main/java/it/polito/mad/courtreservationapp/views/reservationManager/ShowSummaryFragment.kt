package it.polito.mad.courtreservationapp.views.reservationManager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Gender
import it.polito.mad.utils.DiskUtil
import org.json.JSONObject
import org.w3c.dom.Text

class ShowSummaryFragment : Fragment(R.layout.summary_layout){


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
            val sportCenterName : TextView = view.findViewById(R.id.centerName)
            val addressSubtitle : TextView = view.findViewById(R.id.addressSubtitle)
            val address : TextView = view.findViewById(R.id.addressTV)
            val username : TextView = view.findViewById(R.id.usernameTV)
            val date : TextView = view.findViewById(R.id.dateTV)
            val timeslot : TextView = view.findViewById(R.id.timeslotTV)
            val courtname : TextView = view.findViewById(R.id.courtnameTV)
            val services : TextView = view.findViewById(R.id.servicesTV)

            sportCenterName.text="The Athetic Club";
            addressSubtitle.text="Via delle ciliegie";
            address.text="Via delle ciliegie";
            username.text="MARIO";
            date.text=a.reservationDate
            a.reservationTimeSlot.sort()
            val str : String = a.reservationTimeSlot.fold(""){
                acc, i ->
                val startH = 10 + i
                val endH = startH + 1
                "$acc$startH:00 - $endH:00\n"
            }

            timeslot.text=str;
            courtname.text="${a.courtServ.court.sportName} court - ${a.courtServ.court.courtId}";
            services.text="OK";

            view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener {
                a.commitReservation();
            }
            view.findViewById<Button>(R.id.f1_back_button).setOnClickListener {
                a.ggBack();
            }
            view.findViewById<ImageView>(R.id.close_button).setOnClickListener {
                a.finish();
            }
        }

        override fun onResume() {
            super.onResume()
            updateUI()
        }

        @SuppressLint("SetTextI18n")
        private fun updateUI() {



        }


    }