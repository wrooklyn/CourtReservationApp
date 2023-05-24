package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.adapters.ServicesAdapter
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service


class ShowSelectServicesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_select_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Service Picker", "Services: ${(activity as CreateReservationActivity).viewModel.courtWithServices}")
        setUpServicesPicker(view, (activity as CreateReservationActivity))
        val editText = view.findViewById<EditText>(R.id.sv_text_field)
        editText.setText((activity as CreateReservationActivity).viewModel.reservationRequests)
        //todo set the right button
        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener {
            (activity as CreateReservationActivity).viewModel.reservationRequests = editText.text.toString()
            Log.i("Passing", "${(activity as CreateReservationActivity).viewModel.courtWithServices}")
            Log.i("Passing", "${(activity as CreateReservationActivity).viewModel.reservationServices}")
            (activity as CreateReservationActivity).ggNEXT();
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener {
            (activity as CreateReservationActivity).viewModel.reservationRequests = editText.text.toString()
            (activity as CreateReservationActivity).ggBack();
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            activity?.finish();
        }
    }

    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpServicesPicker(view: View, activity: CreateReservationActivity) {
        val recyclerView: RecyclerView =
            view.findViewById(R.id.services_recycler)


        //Show items as a simple linear list
        recyclerView.layoutManager =
            GridLayoutManager(activity, 3)

        //Populate recyclerView with data
        recyclerView.adapter = ServicesAdapter(activity.viewModel.courtWithServices.services, activity)
        Log.i("ServiceFragment", "Services: ${activity.viewModel.courtWithServices.services}")

    }


}