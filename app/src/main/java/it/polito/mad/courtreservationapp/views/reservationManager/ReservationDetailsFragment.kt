package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import it.polito.mad.courtreservationapp.R

class ReservationDetailsFragment : DialogFragment() {

    private lateinit var reservationLocation: String
    private lateinit var reservationDatetime: String

    companion object {
        fun newInstance(reservationLocation: String, reservationDatetime: String): ReservationDetailsFragment {
            val fragment = ReservationDetailsFragment()
            val args = Bundle()
            args.putString("location", reservationLocation)
            args.putString("datetime", reservationDatetime)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reservationLocation = requireArguments().getString("location")!!
        reservationDatetime = requireArguments().getString("datetime")!!
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
        val editReservationButton = view.findViewById<Button>(R.id.edit_reservation_button)
        editReservationButton.setOnClickListener{
            val intent = Intent(context, CreateReservationActivity::class.java)
            startActivity(intent)
        }
    }
}