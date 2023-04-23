package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Reservation


class ShowSelectServicesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        setUpServicesPicker(view)
        //todo set the right button
        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener{
            (activity as CreateReservationActivity).ggNEXT();
            Log.v("gabri", "next pressed");
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener{
            (activity as CreateReservationActivity).ggBack();
            Log.v("gabri", "back pressed");
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener{
            activity?.finish();
        }
        //TODO dispose?
    }
    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpServicesPicker(view: View) {
        val recyclerView: RecyclerView =
            view.findViewById(R.id.services_recycler)

        //Initialize data to be displayed
        val myData: List<Int> = listOf(0, 1, 2, 3)

        //Show items as a simple linear list
        recyclerView.layoutManager =
            GridLayoutManager(activity, 2)

        //Populate recyclerView with data
        recyclerView.adapter = ServicesAdapter(myData)

    }
    class ServicesAdapter(val data: List<Int>) :
        RecyclerView.Adapter<ServicesViewHolder>() {
        override fun getItemCount() = data.size
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ServicesViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.services_item, parent, false)
            return ServicesViewHolder(v)
        }

        override fun onBindViewHolder(
            holder: ServicesViewHolder,
            position: Int
        ) {
            val u = data[position] //access data item
            // let the holder use data as needed
            holder.bind(u);
        }
    }
    class ServicesViewHolder(v: View) :
        RecyclerView.ViewHolder(v) {
        private val icon: ImageView = v.findViewById(R.id.sv_icon)
        fun bind(u: Int) {

        }
    }

}