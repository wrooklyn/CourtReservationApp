package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.fragment_reservation_details, null)
        // Bind data to views in the layout
        builder.setView(view)
        return builder.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_details, container, false)
    }

}