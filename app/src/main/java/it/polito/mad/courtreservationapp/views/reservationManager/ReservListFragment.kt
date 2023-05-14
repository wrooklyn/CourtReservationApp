package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithReview
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.TimeslotMap
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.DescriptionFragment
import java.time.LocalDate

class ReservListFragment: Fragment() {

    lateinit var user: User
    lateinit var reservationsWithSportCenter: List<ReservationWithSportCenter>
    lateinit var reservationsWithServices: List<ReservationWithServices>
    lateinit var reservationsWithReview: List<ReservationWithReview>

    companion object{
        fun newInstance(isUpcoming: Boolean): ReservListFragment {
            val fragment = ReservListFragment()
            val args = Bundle()
            args.putBoolean("isUpcoming", isUpcoming)

            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = (activity as MainActivity).user
        val userReservLocations = (activity as MainActivity).userReservationsLocations.sortedByDescending { res -> res.reservation.reservationDate }
        val userReservServices = (activity as MainActivity).userReservationsServices.sortedByDescending { res -> res.reservation.reservationDate }
        val userReservationWithReview = (activity as MainActivity).userReservationsReviews.sortedByDescending { res -> res.reservation.reservationDate }
        val isUpcoming = requireArguments().getBoolean("isUpcoming")
        if(isUpcoming) {
            reservationsWithSportCenter = userReservLocations.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(LocalDate.now()) }
            reservationsWithServices = userReservServices.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(LocalDate.now()) }
            reservationsWithReview = userReservationWithReview.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(LocalDate.now()) }
        } else {
            reservationsWithSportCenter = userReservLocations.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isBefore(LocalDate.now()) }
            reservationsWithServices = userReservServices.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isBefore(LocalDate.now()) }
            reservationsWithReview = userReservationWithReview.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(LocalDate.now()) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reserv_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.reservations_recycler)
        val adapter = ReservationAdapter(reservationsWithSportCenter)
        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : ReservationAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val reviewed = (reservationsWithReview[position].review != null)
                val fragment = ReservationDetailsFragment.newInstance(user.username, reservationsWithSportCenter[position], reservationsWithServices[position], reviewed)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragment, "fragmentId")
                    ?.commit()
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

        private var reservLocationTV: TextView = view.findViewById(R.id.reservation_locationTV)
        private var reservDatetimeTV: TextView = view.findViewById(R.id.reservation_datetimeTV)
        private var reservImageIV: ImageView = view.findViewById(R.id.reservCardImage)
        private var reservCourtTitle: TextView = view.findViewById(R.id.reservedCourtId)

        fun bind(reservationWithSportCenter: ReservationWithSportCenter) {
            reservCourtTitle.text = reservationWithSportCenter.courtWithSportCenter.court.sportName + " - Court " + reservationWithSportCenter.courtWithSportCenter.court.courtId
            reservLocationTV.text = reservationWithSportCenter.courtWithSportCenter.sportCenter.address
            reservDatetimeTV.text = reservationWithSportCenter.reservation.reservationDate + " - " + TimeslotMap.getTimeslotString(reservationWithSportCenter.reservation.timeSlotId)
            when(reservationWithSportCenter.courtWithSportCenter.court.sportName) {
                "Soccer" -> reservImageIV.setImageResource(R.drawable.football_court)
                "Iceskate" -> reservImageIV.setImageResource(R.drawable.iceskating_rink)
                "Basketball" -> reservImageIV.setImageResource(R.drawable.basket_center)
                "Hockey" -> reservImageIV.setImageResource(R.drawable.hockey_png)
                "Tennis" -> reservImageIV.setImageResource(R.drawable.tennis_court)
                "Volley" -> reservImageIV.setImageResource(R.drawable.volley_court)
                "Rugby" -> reservImageIV.setImageResource(R.drawable.rugby_court)
            }
        }

        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}