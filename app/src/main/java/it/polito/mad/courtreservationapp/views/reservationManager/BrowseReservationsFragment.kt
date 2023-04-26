package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.*
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.homeManager.CenterDetailFragment
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment

class BrowseReservationsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = (activity as MainActivity).user
        val userReservLocations = (activity as MainActivity).userReservationsLocations
        val userReservServices = (activity as MainActivity).userReservationsServices
        val recyclerView: RecyclerView = view.findViewById(R.id.reservations_recycler)
        val adapter = ReservationAdapter(userReservLocations)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ReservationAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val fragment = ReservationDetailsFragment.newInstance(user.username, userReservLocations[position], userReservServices[position])
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragment, "fragmentId")
                    ?.commit();
            }

        })
    }

    class ReservationAdapter(private val reservationsLocations: List<ReservationWithSportCenter>): RecyclerView.Adapter<ReservationViewHolder>() {

         private lateinit var reservListener: OnItemClickListener

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: OnItemClickListener) {
            reservListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.reservation_card_item, parent, false)
            return ReservationViewHolder(itemView, reservListener)
        }

        override fun getItemCount() = reservationsLocations.size

        override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
            holder.bind(reservationsLocations[position])
        }

    }

    class ReservationViewHolder(view: View, listener: ReservationAdapter.OnItemClickListener): RecyclerView.ViewHolder(view) {

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

        private var reservLocationTV: TextView = view.findViewById(R.id.reservation_locationTV)
        private var reservDatetimeTV: TextView = view.findViewById(R.id.reservation_datetimeTV)
        private var reservImageIV: ImageView = view.findViewById(R.id.reservCardImage)

        fun bind(reservationWithSportCenter: ReservationWithSportCenter) {
            reservLocationTV.text = reservationWithSportCenter.courtWithSportCenter.sportCenter.address + " - Court " + reservationWithSportCenter.courtWithSportCenter.court.courtId
            reservDatetimeTV.text = reservationWithSportCenter.reservation.reservationDate + " - " + timeslotMap[reservationWithSportCenter.reservation.timeSlotId]
            when(reservationWithSportCenter.courtWithSportCenter.court.sportName) {
                "Calcio" -> reservImageIV.setImageResource(R.drawable.football_court)
                "Iceskate" -> reservImageIV.setImageResource(R.drawable.iceskating_rink)
                "Basket" -> reservImageIV.setImageResource(R.drawable.basket_center)
                "Hockey" -> reservImageIV.setImageResource(R.drawable.hockey_png)
                "Tennis" -> reservImageIV.setImageResource(R.drawable.tennis_court)
                "Pallavolo" -> reservImageIV.setImageResource(R.drawable.volley_court)
                "Nuoto" -> reservImageIV.setImageResource(R.drawable.swimming_pool)
            }
        }

        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}