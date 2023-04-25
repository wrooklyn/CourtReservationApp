package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.ReservationFragmentViewModel

class ReservationDetailsFragment : Fragment() {

    private lateinit var username: String
    private lateinit var sportCenterAddress: String
    private lateinit var courtName: String
    private lateinit var date: String
    private var timeslotId: Long = 0
    private lateinit var sportName: String

    private val timeslotMap: Map<Long, String> = mapOf(
        Pair(0, "10:00 - 11:00"),
        Pair(1, "11:00 - 12:00"),
        Pair(2, "12:00 - 13:00"),
        Pair(3, "13:00 - 14:00"),
        Pair(4, "14:00 - 15:00"),
        Pair(5, "15:00 - 16:00"),
        Pair(6, "16:00 - 17:00"),
        Pair(7, "17:00 - 18:00"),
    )

    companion object {
        fun newInstance(username: String, sportCenterAddress: String, court: Court, date:String, timeslotId: Long): ReservationDetailsFragment {
            val fragment = ReservationDetailsFragment()
            val args = Bundle()
            args.putString("username", username)
            args.putString("sportCenterAddress", sportCenterAddress)
            args.putString("courtName", "${court.sportName} court - #C${court.courtId}")
            args.putString("date", date)
            args.putLong("timeslotId", timeslotId)
            args.putString("sportName", court.sportName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = requireArguments().getString("username")!!
        sportCenterAddress = requireArguments().getString("sportCenterAddress")!!
        courtName = requireArguments().getString("courtName")!!
        date = requireArguments().getString("date")!!
        timeslotId = requireArguments().getLong("timeslotId")
        sportName = requireArguments().getString("sportName")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameTV: TextView = view.findViewById(R.id.usernameTV)
        val addressTV: TextView = view.findViewById(R.id.addressTV)
        val dateTV: TextView = view.findViewById(R.id.dateTV)
        val timeslotTV: TextView = view.findViewById(R.id.timeslotTV)
        val courtNameTV: TextView = view.findViewById(R.id.courtnameTV)
        val courtImageIV: ImageView = view.findViewById(R.id.courtImageIV)

        usernameTV.text = username
        addressTV.text = sportCenterAddress
        dateTV.text = date
        timeslotTV.text = timeslotMap[timeslotId]
        courtNameTV.text = courtName
        when(sportName) {
            "Calcio" -> courtImageIV.setImageResource(R.drawable.football_court)
            "Iceskate" -> courtImageIV.setImageResource(R.drawable.iceskating_rink)
            "Basket" -> courtImageIV.setImageResource(R.drawable.basket_center)
            "Hockey" -> courtImageIV.setImageResource(R.drawable.hockey_png)
            "Tennis" -> courtImageIV.setImageResource(R.drawable.tennis_court)
            "Pallavolo" -> courtImageIV.setImageResource(R.drawable.volley_court)
            "Nuoto" -> courtImageIV.setImageResource(R.drawable.swimming_pool)
        }

        val editReservationButton = view.findViewById<Button>(R.id.edit_reservation_button)
        editReservationButton.setOnClickListener{
            val intent = Intent(context, CreateReservationActivity::class.java)
            startActivity(intent)
        }
    }
}