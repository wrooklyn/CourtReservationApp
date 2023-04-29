package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.TimeslotMap
import it.polito.mad.courtreservationapp.view_model.ReservationBrowserViewModel
import it.polito.mad.courtreservationapp.views.MainActivity


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

    lateinit var viewModel: ReservationBrowserViewModel

    private lateinit var mainContainerCL: ConstraintLayout

    companion object {
        fun newInstance(username: String, reservWithSportCenter: ReservationWithSportCenter, reservWithServices: ReservationWithServices): ReservationDetailsFragment {
            val fragment = ReservationDetailsFragment()
            val args = Bundle()
            Log.i("DBG", reservWithSportCenter.toString())
            args.putString("username", username)
            args.putString("centerName", reservWithSportCenter.courtWithSportCenter.sportCenter.name)
            args.putString("sportCenterAddress", reservWithSportCenter.courtWithSportCenter.sportCenter.address)
            args.putString("courtName", "${reservWithSportCenter.courtWithSportCenter.court.sportName} court - #C${reservWithSportCenter.courtWithSportCenter.court.courtId}")
            args.putLong("courtId", reservWithSportCenter.courtWithSportCenter.court.courtId)
            args.putLong("reservationId", reservWithSportCenter.reservation.reservationId)
            args.putString("date", reservWithSportCenter.reservation.reservationDate)
            args.putLong("timeslotId", reservWithSportCenter.reservation.timeSlotId)
            args.putString("sportName", reservWithSportCenter.courtWithSportCenter.court.sportName)
            args.putString("specialRequests", reservWithSportCenter.reservation.request)
            args.putLong("sportCenterId", reservWithSportCenter.courtWithSportCenter.sportCenter.centerId)
            args.putLongArray("serviceIds", reservWithServices.services.map{ it.serviceId }.toTypedArray().toLongArray())
            args.putStringArray("serviceDescriptions", reservWithServices.services.map{ it.description }.toTypedArray())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).reservationBrowserViewModel
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

        Log.i("ASD", "Details, centerId:$sportCenterId")
        Log.i("ASD", "Details, courtId:$courtId")
        Log.i("ASD", "Details, reservationId:$reservationId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reservation_details, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.servicesRecycler)
        val serviceTV = view.findViewById<TextView>(R.id.servicesTitle)
        val params: ViewGroup.LayoutParams = view.findViewById<ConstraintLayout>(R.id.servicesCL).layoutParams
        if (serviceIds.isEmpty()) {
            serviceTV.visibility = View.INVISIBLE
            params.height = 0
            view.findViewById<ConstraintLayout>(R.id.servicesCL).layoutParams = params
        } else {
            serviceTV.visibility = View.VISIBLE
            recyclerView.adapter = RequestedServiceAdapter(serviceIds, serviceDescriptions, viewModel.servicesIcons, activity as MainActivity)

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
        when(sportName) {
            "Calcio" -> courtImageIV.setImageResource(R.drawable.football_court)
            "Iceskate" -> courtImageIV.setImageResource(R.drawable.iceskating_rink)
            "Basket" -> courtImageIV.setImageResource(R.drawable.basket_center)
            "Hockey" -> courtImageIV.setImageResource(R.drawable.hockey_png)
            "Tennis" -> courtImageIV.setImageResource(R.drawable.tennis_court)
            "Pallavolo" -> courtImageIV.setImageResource(R.drawable.volley_court)
            "Nuoto" -> courtImageIV.setImageResource(R.drawable.swimming_pool)
        }

        val editReservationButton = view.findViewById<Button>(R.id.edit_reservation_button)
        editReservationButton.setOnClickListener{
            val intent = Intent(context, CreateReservationActivity::class.java)
            intent.putExtra("courtId", courtId)
            intent.putExtra("reservationId", reservationId)
            intent.putExtra("sportCenterId", sportCenterId)
            startActivity(intent)
        }

        val cancelReservationButton = view.findViewById<Button>(R.id.cancel_reserv_button)
        cancelReservationButton.setOnClickListener{
            val inflater = (activity as MainActivity).getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

            yesButton.setOnClickListener{
                (activity as MainActivity).reservationBrowserViewModel.deleteReservation(reservationId)
                popupWindow.dismiss()
                val popupView2 = inflater.inflate(R.layout.reserv_cancel_confirmation, null)
                val dismissButton = popupView2.findViewById<Button>(R.id.dismissButton)
                popupWindow = PopupWindow(popupView2, width, height, focusable)
                popupWindow.showAtLocation(mainContainerCL, Gravity.CENTER, 0, 0)
                mainContainerCL.foreground.alpha = 160

                popupWindow.setOnDismissListener {
                    mainContainerCL.foreground.alpha = 0
                }

                dismissButton.setOnClickListener{
                    popupWindow.dismiss()
                    (activity as MainActivity).replaceFragment(BrowseReservationsFragment())
                }

            }

            noButton.setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }

    class RequestedServiceAdapter(private val serviceIds: LongArray, private val serviceDescriptions: Array<String>, private val imagesMap: Map<Long, Int>, val activity: MainActivity): RecyclerView.Adapter<RequestedServiceAdapter.RequestedServiceViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestedServiceViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.service_description_item, parent, false)
            return RequestedServiceViewHolder(view, activity)
        }

        override fun onBindViewHolder(holder: RequestedServiceViewHolder, position: Int) {
            val serviceId = serviceIds[position]
            val serviceDescription = serviceDescriptions[position]
            val imageId = imagesMap[serviceId] ?: R.drawable.gesu
            holder.bind(serviceDescription, imageId)
        }

        override fun getItemCount() = serviceIds.size

        class RequestedServiceViewHolder(itemView: View, activity: MainActivity) : RecyclerView.ViewHolder(itemView) {
            private val icon: ImageView = itemView.findViewById(R.id.service_description_image)
            //private val layout: ConstraintLayout = itemView.findViewById(R.id.sv_layout)
            private val serviceName: TextView = itemView.findViewById(R.id.service_description_name)
            private val a = activity
            fun bind(serviceDescription: String, imageSrc: Int) {
                //text.text = serviceDescription
                //Log.i("binder image", serviceDescription)
                icon.setImageResource(imageSrc)
                serviceName.text=serviceDescription
                //val blue = ContextCompat.getColor(a, R.color.deep_blue)
                /*var drawable: Drawable? = null
                when (serviceId.toInt()) {
                    0 -> drawable = ContextCompat.getDrawable(a, R.drawable.shower_sv)
                    1 -> drawable = ContextCompat.getDrawable(a, R.drawable.equipment)
                    2 -> drawable = ContextCompat.getDrawable(a, R.drawable.coach)
                    3 -> drawable = ContextCompat.getDrawable(a, R.drawable.refreshment)
                }
                //layout.backgroundTintList = ColorStateList.valueOf(blue)
                //icon.setBackgroundColor(blue)
            *//*    drawable?.setColorFilter(
                    ContextCompat.getColor(a, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )*//*
                icon.setImageDrawable(drawable)*/
            }

        }
    }
}