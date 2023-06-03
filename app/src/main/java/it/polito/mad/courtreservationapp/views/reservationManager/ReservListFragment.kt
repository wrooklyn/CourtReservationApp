package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.models.TimeslotMap
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.views.MainActivity
import java.time.LocalDate

class ReservListFragment: Fragment() {

    lateinit var user: User
    lateinit var reservationsWithSportCenter: List<ReservationWithSportCenter>
    lateinit var reservationsWithServices: List<ReservationWithServices>
    lateinit var reservationsWithReview: List<ReservationWithReview>
    lateinit var sportCentersWithCourtsAndReviewsAndUsers: List<SportCenterWIthCourtsAndReviewsAndUsers>

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
        user = (activity as MainActivity).userViewModel.user
        sportCentersWithCourtsAndReviewsAndUsers = (activity as MainActivity).sportCenterViewModel.sportCentersWithCourtsAndReviewsAndUsers
        val userReservLocations = (activity as MainActivity).userReservationsLocations.sortedByDescending { res -> res.reservation.reservationDate }
        val userReservServices = (activity as MainActivity).userReservationsServices.sortedByDescending { res -> res.reservation.reservationDate }
        val userReservationWithReview = (activity as MainActivity).userReservationsReviews.sortedByDescending { res -> res.reservation.reservationDate }
        val isUpcoming = requireArguments().getBoolean("isUpcoming")
        Log.i("ReservListFragment", "location: $userReservLocations")
        Log.i("ReservListFragment", "services: $userReservServices")
        val today = LocalDate.now()
        if(isUpcoming) {
            reservationsWithSportCenter = userReservLocations.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(today) }
            reservationsWithServices = userReservServices.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(today) }
            reservationsWithReview = userReservationWithReview.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isAfter(today) }
            Log.i("Upcoming", "$reservationsWithSportCenter")
        } else {
            reservationsWithSportCenter = userReservLocations.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isBefore(today) || LocalDate.parse(res.reservation.reservationDate).isEqual(today)}
            reservationsWithServices = userReservServices.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isBefore(today) || LocalDate.parse(res.reservation.reservationDate).isEqual(today) }
            reservationsWithReview = userReservationWithReview.filter{ res -> LocalDate.parse(res.reservation.reservationDate).isBefore(today) || LocalDate.parse(res.reservation.reservationDate).isEqual(today) }
            Log.i("Past", "$reservationsWithSportCenter")
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
                val sportCenter: SportCenter = reservationsWithSportCenter[position].courtWithSportCenter.sportCenter
                Log.i("ReservListFrag", "sportCenter: $sportCenter")
                val court: Court = reservationsWithSportCenter[position].courtWithSportCenter.court
                Log.i("ReservListFrag", "court: $court")
                val sportCenterWithCourtsAndReviews = sportCentersWithCourtsAndReviewsAndUsers.first { sportCenterWithAll -> sportCenterWithAll.sportCenter.centerId == sportCenter.centerId }
                Log.i("ReservListFrag", "sportCenterFiltered: $sportCenterWithCourtsAndReviews")
                val courtWithReviewsAndUsers = sportCenterWithCourtsAndReviews.courtsWithReviewsAndUsers.first{ courtWithReviewsAndUsers ->  courtWithReviewsAndUsers.court == court }
                Log.i("ReservListFrag", "courtFiltered: $courtWithReviewsAndUsers")
                val averageRating = courtWithReviewsAndUsers.reviewsWithUser.map { it.review.rating }.average().run { if (isNaN()) 0.0 else this}
                val ratingTxt = if (courtWithReviewsAndUsers.reviewsWithUser.size == 1) "review" else "reviews"
                val currentReview = "$averageRating (${courtWithReviewsAndUsers.reviewsWithUser.size} $ratingTxt)"

                val fragment = ReservationDetailsFragment.newInstance(user.username, reservationsWithSportCenter[position], reservationsWithServices[position], reviewed, averageRating, currentReview)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragment, "fragmentId")
                    ?.addToBackStack(null)
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
            reservCourtTitle.text = reservationWithSportCenter.courtWithSportCenter.court.sportName + " Court"
            reservLocationTV.text = reservationWithSportCenter.courtWithSportCenter.sportCenter.address
            reservDatetimeTV.text = reservationWithSportCenter.reservation.reservationDate + " - " + TimeslotMap.getTimeslotString(reservationWithSportCenter.reservation.timeSlotId)
            ImageUtils.setImage("courts", reservationWithSportCenter.courtWithSportCenter.court.image, reservImageIV)
        }

        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}