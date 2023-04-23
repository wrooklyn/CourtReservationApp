package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourts
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.SportCenter

class BrowseReservationsFragment : Fragment() {

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

    fun initReservations() {
        // TODO: add DB integration
        val userReservations: List<Reservation> = (activity as ReservationBrowserActivity).userReservations.reservations

        val datetimes: List<String> = userReservations.map{ it.reservationDate + " - " + timeslotMap[it.timeSlotId]!!}

        val reservedCourtsCodes: List<Long> = userReservations.map{ it.reservationCourtId }
        val reservedCourts: List<Court> = (activity as ReservationBrowserActivity).courts.filter{ it.courtId in reservedCourtsCodes }
        val reservedSportCenter: List<SportCenter> = (activity as ReservationBrowserActivity).sportCenters.filter{ it.centerId in reservedCourts.map{ it.courtCenterId }}

        val locations: List<String> = reservedSportCenter.map{ it.address }


    }

    class ReservationAdapter(private val locations: List<String>, private val datetimes: List<String>): RecyclerView.Adapter<ReservationViewHolder>() {

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

        override fun getItemCount() = locations.size

        override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
            holder.bind(locations[position], datetimes[position])
        }

    }

    class ReservationViewHolder(view: View, listener: ReservationAdapter.OnItemClickListener): RecyclerView.ViewHolder(view) {

        private var reservLocationTV: TextView = view.findViewById(R.id.reservation_locationTV)
        private var reservDatetimeTV: TextView = view.findViewById(R.id.reservation_datetimeTV)

        fun bind(locationName: String, reservDatetime: String) {
            reservLocationTV.text = locationName
            reservDatetimeTV.text = reservDatetime
        }

        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}