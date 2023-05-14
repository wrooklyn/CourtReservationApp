package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.models.TimeslotMap
import it.polito.mad.courtreservationapp.view_model.LeaveRatingViewModel
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.ratings.LeaveRatingActivity
import it.polito.mad.utils.BitmapUtil
import it.polito.mad.utils.DiskUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class ReservationDetailsFragment : Fragment() {

    private lateinit var centerName: String
    private lateinit var username: String
    private lateinit var sportCenterAddress: String
    private lateinit var courtName: String
    private var courtId: Long = 0
    private var reservationId: Long = 0
    private lateinit var date: String
    private var timeslotId: Long = 0
    private lateinit var sportName: String
    private var sportCenterId: Long = 0
    private var specialRequests: String? = null
    private lateinit var serviceIds: LongArray
    private lateinit var serviceDescriptions: Array<String>
    private var reviewed: Boolean = false

    lateinit var viewModel: ReservationBrowserViewModel
    lateinit var ratingViewModel: LeaveRatingViewModel

    private lateinit var mainContainerCL: ConstraintLayout

    private val REQUEST_CODE_REVIEW = 1


    companion object {
        fun newInstance(
            username: String,
            reservWithSportCenter: ReservationWithSportCenter,
            reservWithServices: ReservationWithServices,
            reviewed: Boolean
        ): ReservationDetailsFragment {
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
                "${reservWithSportCenter.courtWithSportCenter.court.sportName} court - #C${reservWithSportCenter.courtWithSportCenter.court.courtId}"
            )
            args.putLong("courtId", reservWithSportCenter.courtWithSportCenter.court.courtId)
            args.putLong("reservationId", reservWithSportCenter.reservation.reservationId)
            args.putString("date", reservWithSportCenter.reservation.reservationDate)
            args.putLong("timeslotId", reservWithSportCenter.reservation.timeSlotId)
            args.putString("sportName", reservWithSportCenter.courtWithSportCenter.court.sportName)
            args.putString("specialRequests", reservWithSportCenter.reservation.request)
            args.putLong(
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
            args.putBoolean("reviewed", reviewed)
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
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).reservationBrowserViewModel
        ratingViewModel =(activity as MainActivity).ratingViewModel
        centerName = requireArguments().getString("centerName")!!
        username = requireArguments().getString("username")!!
        sportCenterAddress = requireArguments().getString("sportCenterAddress")!!
        courtName = requireArguments().getString("courtName")!!
        date = requireArguments().getString("date")!!
        timeslotId = requireArguments().getLong("timeslotId")
        sportName = requireArguments().getString("sportName")!!
        courtId = requireArguments().getLong("courtId")
        reservationId = requireArguments().getLong("reservationId")
        sportCenterId = requireArguments().getLong("sportCenterId")
        specialRequests = requireArguments().getString("specialRequests")
        serviceIds = requireArguments().getLongArray("serviceIds")!!
        serviceDescriptions = requireArguments().getStringArray("serviceDescriptions")!!
        reviewed = requireArguments().getBoolean("reviewed")
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
            view.findViewById<ConstraintLayout>(R.id.servicesCL).layoutParams = params
        } else {
            serviceTV.visibility = View.VISIBLE
            recyclerView.adapter = RequestedServiceAdapter(
                serviceIds,
                serviceDescriptions,
                viewModel.servicesIcons,
                activity as MainActivity
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
        Log.i("asdasd", "$sportName")
        when (sportName) {
            "Soccer" -> courtImageIV.setImageResource(R.drawable.football_court)
            "Iceskate" -> courtImageIV.setImageResource(R.drawable.iceskating_rink)
            "Basket" -> courtImageIV.setImageResource(R.drawable.basket_center)
            "Hockey" -> courtImageIV.setImageResource(R.drawable.hockey_png)
            "Tennis" -> courtImageIV.setImageResource(R.drawable.tennis_court)
            "Volley" -> courtImageIV.setImageResource(R.drawable.volley_court)
            "Swimming" -> courtImageIV.setImageResource(R.drawable.swimming_pool)
        }

        val editReservationButton = view.findViewById<Button>(R.id.edit_reservation_button)
        editReservationButton.setOnClickListener {
            val intent = Intent(context, CreateReservationActivity::class.java)
            intent.putExtra("courtId", courtId)
            intent.putExtra("reservationId", reservationId)
            intent.putExtra("sportCenterId", sportCenterId)
            startActivity(intent)
            Handler().postDelayed({
                (activity as MainActivity).replaceFragment(BrowseReservationsFragment())
            }, 1000)
        }

        val cancelReservationButton = view.findViewById<Button>(R.id.cancel_reserv_button)
        cancelReservationButton.setOnClickListener {
            val inflater =
                (activity as MainActivity).getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var popupView = inflater.inflate(R.layout.popup_confirm_delete_reserv, null)
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
                    reservationId
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
    }

    class RequestedServiceAdapter(
        private val serviceIds: LongArray,
        private val serviceDescriptions: Array<String>,
        private val imagesMap: Map<Long, Int>,
        val activity: MainActivity
    ) : RecyclerView.Adapter<RequestedServiceAdapter.RequestedServiceViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): RequestedServiceViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.service_description_item, parent, false)
            return RequestedServiceViewHolder(view, activity)
        }

        override fun onBindViewHolder(holder: RequestedServiceViewHolder, position: Int) {
            val serviceId = serviceIds[position]
            val serviceDescription = serviceDescriptions[position]
            val imageId = imagesMap[serviceId] ?: R.drawable.gesu
            holder.bind(serviceDescription, imageId)
        }

        override fun getItemCount() = serviceIds.size

        class RequestedServiceViewHolder(itemView: View, activity: MainActivity) :
            RecyclerView.ViewHolder(itemView) {
            private val icon: ImageView = itemView.findViewById(R.id.service_description_image)
            private val serviceName: TextView = itemView.findViewById(R.id.service_description_name)
            private val a = activity
            fun bind(serviceDescription: String, imageSrc: Int) {

                icon.setImageResource(imageSrc)
                serviceName.text = serviceDescription

            }

        }
    }
}