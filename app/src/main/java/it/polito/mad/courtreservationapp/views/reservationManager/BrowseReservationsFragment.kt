package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.dao.ReservationDao
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment

class BrowseReservationsFragment : Fragment() {

    lateinit var userReservations: Array<Reservation>

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
        initReservations()
    }

    fun initReservations() {
        // TODO get Reservation's locations and court name from DB

        val reservationLocations = arrayOf(
            "Sport Centre 1 - Via Roma 1, Chieri",
            "Sport Centre 2 - Via Roma 2, Chieri",
            "Sport Centre 3 - Via Roma 3, Chieri",
            "Sport Centre 4 - Via Roma 4, Chieri",
            "Sport Centre 5 - Via Roma 5, Chieri",
        )

        val reservationDatetimes = arrayOf(
            "15/03/2023 - 15:30",
            "16/03/2023 - 16:30",
            "17/03/2023 - 17:30",
            "18/03/2023 - 09:30",
            "19/03/2023 - 12:00"
        )

        val recyclerView: RecyclerView? = view?.findViewById(R.id.reservations_recycler)
        val adapter = ReservationAdapter(reservationLocations, reservationDatetimes)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView?.adapter = adapter
    }

    class ReservationAdapter(private val reservationLocations: Array<String>, private val reservationDatetimes: Array<String>): RecyclerView.Adapter<ReservationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.reservation_card_item, parent, false)
            return ReservationViewHolder(itemView)
        }

        override fun getItemCount() = reservationDatetimes.size

        override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
            holder.bind(reservationLocations[position], reservationDatetimes[position])
        }

    }

    // TODO: add onClickListener
    class ReservationViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private var reservLocationTV: TextView = view.findViewById(R.id.reservation_locationTV)
        private var reservDatetimeTV: TextView = view.findViewById(R.id.reservation_datetimeTV)

        fun bind(locationName: String, reservDatetime: String) {
            reservLocationTV.text = locationName
            reservDatetimeTV.text = reservDatetime
        }

        // TODO: add init with setOnClickListener
    }
}