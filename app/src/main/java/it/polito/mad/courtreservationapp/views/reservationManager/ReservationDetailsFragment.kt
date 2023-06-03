package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.TimeslotMap
import it.polito.mad.courtreservationapp.utils.IconUtils
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.view_model.LeaveRatingViewModel
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment
import it.polito.mad.courtreservationapp.views.login.SavedPreference
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ReservationDetailsFragment : Fragment() {

    private lateinit var centerName: String
    private lateinit var username: String
    private lateinit var sportCenterAddress: String
    private lateinit var courtName: String
    private lateinit var courtId: String
    private lateinit var reservationId: String
    private lateinit var date: String
    private var timeslotId: Long = 0
    private lateinit var sportName: String
    private lateinit var sportCenterId: String
    private var specialRequests: String? = null
    private lateinit var serviceIds: LongArray
    private lateinit var serviceDescriptions: Array<String>
    private lateinit var serviceCosts: DoubleArray
    private var reviewed: Boolean = false
    private var courtImage: String? = null
    private var rating: Double = 0.0
    private var reviews: String = "0 reviews"
    private var courtCost: Double = 0.0

    lateinit var viewModel: ReservationBrowserViewModel
    private lateinit var sportCenterViewModel: SportCenterViewModel

    private lateinit var mainContainerCL: ConstraintLayout

    private val REQUEST_CODE_REVIEW = 1


    companion object {
        fun newInstance(
            username: String,
            reservWithSportCenter: ReservationWithSportCenter,
            reservWithServices: ReservationWithServices,
            reviewed: Boolean,
            averageRating: Double,
            currentReview: String
        ): ReservationDetailsFragment {
            Log.i("ReservationDetailsFragment", "ResWithServices: $reservWithServices")
            Log.i("ReservationDetailsFragment", "ResWithCenter: $reservWithSportCenter")
            val fragment = ReservationDetailsFragment()
            val args = Bundle()
            args.putString("username", username)
            args.putString(
                "centerName",
                reservWithSportCenter.courtWithSportCenter.sportCenter.name
            )
            args.putString(
                "sportCenterAddress",
                reservWithSportCenter.courtWithSportCenter.sportCenter.address
            )
            args.putString(
                "courtName",
                "${reservWithSportCenter.courtWithSportCenter.court.sportName} Court"
            )
            args.putString("courtId", reservWithSportCenter.reservation.reservationCourtId)
            args.putDouble("courtCost", reservWithSportCenter.courtWithSportCenter.court.cost)
            args.putString("reservationId", reservWithSportCenter.reservation.reservationId)
            args.putString("date", reservWithSportCenter.reservation.reservationDate)
            args.putLong("timeslotId", reservWithSportCenter.reservation.timeSlotId)
            args.putString("sportName", reservWithSportCenter.courtWithSportCenter.court.sportName)
            args.putString("specialRequests", reservWithSportCenter.reservation.request)
            args.putString(
                "sportCenterId",
                reservWithSportCenter.courtWithSportCenter.sportCenter.centerId
            )
            args.putLongArray(
                "serviceIds",
                reservWithServices.services.map { it.serviceId }.toTypedArray().toLongArray()
            )
            args.putStringArray(
                "serviceDescriptions",
                reservWithServices.services.map { it.description }.toTypedArray()
            )
            args.putDoubleArray(
                "serviceCosts",
                reservWithServices.services.map { it.cost }.toTypedArray().toDoubleArray()
            )
            args.putBoolean("reviewed", reviewed)
            args.putString("courtImage", reservWithSportCenter.courtWithSportCenter.court.image)
            args.putDouble("rating", averageRating)
            args.putString("reviews", currentReview)
            fragment.arguments = args
            return fragment
        }
    }

    private val reviewActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val reviewSubmitted = data?.getBooleanExtra("review_submitted", false)
            Log.i("REVIEW", "Review submitted is $reviewSubmitted")
            if (reviewSubmitted == true) {
                val leaveReviewButton = view?.findViewById<Button>(R.id.leave_review_button)
                leaveReviewButton?.visibility = View.GONE
            }
            sportCenterViewModel.initData()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).reservationBrowserViewModel
        sportCenterViewModel =(activity as MainActivity).sportCenterViewModel
        centerName = requireArguments().getString("centerName")!!
        username = requireArguments().getString("username")!!
        sportCenterAddress = requireArguments().getString("sportCenterAddress")!!
        courtName = requireArguments().getString("courtName")!!
        date = requireArguments().getString("date")!!
        timeslotId = requireArguments().getLong("timeslotId")
        sportName = requireArguments().getString("sportName")!!
        courtId = requireArguments().getString("courtId")!!
        reservationId = requireArguments().getString("reservationId")!!
        sportCenterId = requireArguments().getString("sportCenterId")!!
        specialRequests = requireArguments().getString("specialRequests")
        serviceIds = requireArguments().getLongArray("serviceIds")!!
        serviceDescriptions = requireArguments().getStringArray("serviceDescriptions")!!
        serviceCosts = requireArguments().getDoubleArray("serviceCosts") ?: doubleArrayOf()
        reviewed = requireArguments().getBoolean("reviewed")
        courtImage = requireArguments().getString("courtImage")
        rating = requireArguments().getDouble("rating")
        reviews = requireArguments().getString("reviews") ?: "0 reviews"
        courtCost = requireArguments().getDouble("courtCost")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reservation_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.servicesRecycler)
        val serviceTV = view.findViewById<TextView>(R.id.servicesTitle)
        val params: ViewGroup.LayoutParams =
            view.findViewById<ConstraintLayout>(R.id.servicesCL).layoutParams
        if (serviceIds.isEmpty()) {
            serviceTV.visibility = View.INVISIBLE
            params.height = 0
//            view.findViewById<ConstraintLayout>(R.id.servicesCL).layoutParams = params
        } else {
            serviceTV.visibility = View.VISIBLE
            recyclerView.adapter = RequestedServiceAdapter(
                serviceIds,
                serviceDescriptions,
                serviceCosts
            )

        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val centerNameTV: TextView = view.findViewById(R.id.centerName)
        val centerAddressTV: TextView = view.findViewById(R.id.addressSubtitle)
        val usernameTV: TextView = view.findViewById(R.id.usernameTV)
        val addressTV: TextView = view.findViewById(R.id.addressTV)
        val dateTV: TextView = view.findViewById(R.id.dateTV)
        val timeslotTV: TextView = view.findViewById(R.id.timeslotTV)
        val courtNameTV: TextView = view.findViewById(R.id.courtnameTV)
        val courtImageIV: ImageView = view.findViewById(R.id.courtImageIV)
        val specialRequestsTV: TextView = view.findViewById(R.id.specialRequestsTV)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val reviewsTv: TextView = view.findViewById(R.id.textView6)
        val totalTV: TextView = view.findViewById(R.id.totalTV)
        mainContainerCL = view.findViewById(R.id.mainContainerCL)
        mainContainerCL.foreground.alpha = 0

        centerNameTV.text = centerName
        centerAddressTV.text = sportCenterAddress
        usernameTV.text = username
        addressTV.text = sportCenterAddress
        dateTV.text = date
        timeslotTV.text = TimeslotMap.getTimeslotString(timeslotId)
        courtNameTV.text = courtName
        specialRequestsTV.text = specialRequests ?: "You did not have any special request"
        Log.i("asdasd", sportName)
        ImageUtils.setImage("courts", courtImage, courtImageIV)
        ratingBar.rating = rating.toFloat()
        reviewsTv.text = reviews

        totalTV.text = String.format("Total: %.2f €", serviceCosts.fold(courtCost){
                acc, el ->
            acc + el
        })
//        val curText = servicesTitleTV.text
//        servicesTitleTV.text = String.format("$curText - Total: %.2f", serviceCosts.fold(0.0){
//                acc, el ->
//            acc + el
//        })

        val editReservationButton = view.findViewById<Button>(R.id.edit_reservation_button)
        editReservationButton.setOnClickListener {
            val intent = Intent(context, CreateReservationActivity::class.java)
            intent.putExtra("courtId", courtId)
            intent.putExtra("reservationId", reservationId)
            intent.putExtra("sportCenterId", sportCenterId)
            (context as MainActivity).registerForReservationActivityResult.launch(intent)
//            startActivity(intent)
//            Handler().postDelayed({
//                (activity as MainActivity).replaceFragment(BrowseReservationsFragment())
//            }, 1000)
        }

        val cancelReservationButton = view.findViewById<Button>(R.id.cancel_reserv_button)
        cancelReservationButton.setOnClickListener {
            val inflater =
                (activity as MainActivity).getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = inflater.inflate(R.layout.popup_confirm_delete_reserv, null)
            val popupInviteFriendsView = inflater.inflate(R.layout.popup_invite_friends, null)
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it
            var popupWindow = PopupWindow(popupView, width, height, focusable)
            val yesButton = popupView.findViewById<Button>(R.id.yesButton)
            val noButton = popupView.findViewById<Button>(R.id.noButton)

            popupWindow.showAtLocation(mainContainerCL, Gravity.CENTER, 0, 0)
            mainContainerCL.foreground.alpha = 160

            popupWindow.setOnDismissListener {
                mainContainerCL.foreground.alpha = 0
            }

            yesButton.setOnClickListener {
                (activity as MainActivity).reservationBrowserViewModel.deleteReservation(
                    reservationId,
                    SavedPreference.EMAIL,
                    sportCenterId,
                    courtId
                )
                popupWindow.dismiss()
                val popupView2 = inflater.inflate(R.layout.reserv_cancel_confirmation, null)
                val dismissButton = popupView2.findViewById<Button>(R.id.dismissButton)
                popupWindow = PopupWindow(popupView2, width, height, focusable)
                popupWindow.showAtLocation(mainContainerCL, Gravity.CENTER, 0, 0)
                mainContainerCL.foreground.alpha = 160

                popupWindow.setOnDismissListener {
                    mainContainerCL.foreground.alpha = 0
                }

                dismissButton.setOnClickListener {
                    popupWindow.dismiss()
                    (activity as MainActivity).replaceFragment(BrowseReservationsFragment())
                }

            }

            noButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }
        val addFriendsButton = view.findViewById<ImageView>(R.id.add_friends_button)
        addFriendsButton.setOnClickListener {

        }

        val leaveReviewButton = view.findViewById<Button>(R.id.leave_review_button)

        leaveReviewButton.setOnClickListener{
            val intent = Intent(activity, LeaveRatingActivity::class.java)
            intent.putExtra("courtId", courtId)
            intent.putExtra("reservationId", reservationId)
            intent.putExtra("sportCenterId", sportCenterId)
            reviewActivityResult.launch(intent)
        }


        val reservationDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val today = LocalDate.now()
        if (reservationDate > today) {
            editReservationButton.visibility = View.VISIBLE
            cancelReservationButton.visibility = View.VISIBLE
            leaveReviewButton.visibility = View.GONE
        } else {
            editReservationButton.visibility = View.GONE
            addFriendsButton.visibility = View.GONE
            cancelReservationButton.visibility = View.GONE
            if(reviewed) {
                Log.i("REVIEW", "Reservation $reservationId is already rated")
                leaveReviewButton.visibility = View.GONE
            }
            else {
                Log.i("REVIEW", "Reservation $reservationId is not already rated")
                leaveReviewButton.visibility = View.VISIBLE
            }
        }
        val isReviewed = viewModel.hasAlreadyReviewed(sportCenterId, courtId, reservationId)
        if(isReviewed){
            leaveReviewButton.visibility=View.GONE
        }
    }

    class RequestedServiceAdapter(
        private val serviceIds: LongArray,
        private val serviceDescriptions: Array<String>,
        private val serviceCosts: DoubleArray
    ) : RecyclerView.Adapter<RequestedServiceAdapter.RequestedServiceViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestedServiceViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.service_description_item, parent, false)
            return RequestedServiceViewHolder(view)
        }

        override fun onBindViewHolder(holder: RequestedServiceViewHolder, position: Int) {
            val serviceId = serviceIds[position]
            val serviceDescription = serviceDescriptions[position]
            val serviceCost = serviceCosts[position]
            val imageId = IconUtils.getServiceIcon(serviceId)
            holder.bind(serviceDescription, imageId, serviceCost)
        }

        override fun getItemCount() = serviceIds.size

        class RequestedServiceViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
            private val icon: ImageView = itemView.findViewById(R.id.service_description_image)
            private val serviceName: TextView = itemView.findViewById(R.id.service_description_name)

            fun bind(serviceDescription: String, imageSrc: Int, serviceCost: Double) {

                icon.setImageResource(imageSrc)
                serviceName.text = serviceDescription


            }

        }
    }
}