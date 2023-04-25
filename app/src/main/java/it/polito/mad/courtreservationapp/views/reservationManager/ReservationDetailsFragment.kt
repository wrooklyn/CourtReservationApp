package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.views.MainActivity

class ReservationDetailsFragment : Fragment() {

    private lateinit var username: String
    private lateinit var sportCenterAddress: String
    private lateinit var courtName: String
    private var courtId: Long = 0
    private var reservationId: Long = 0
    private lateinit var date: String
    private var timeslotId: Long = 0
    private lateinit var sportName: String

    private lateinit var mainContainerCL: ConstraintLayout

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

    companion object {
        fun newInstance(username: String, reservWithSportCenter: ReservationWithSportCenter): ReservationDetailsFragment {
            val fragment = ReservationDetailsFragment()
            val args = Bundle()
            Log.i("DBG", reservWithSportCenter.toString())
            args.putString("username", username)
            args.putString("sportCenterAddress", reservWithSportCenter.courtWithSportCenter.sportCenter.address)
            args.putString("courtName", "${reservWithSportCenter.courtWithSportCenter.court.sportName} court - #C${reservWithSportCenter.courtWithSportCenter.court.courtId}")
            args.putLong("courtId", reservWithSportCenter.courtWithSportCenter.court.courtId)
            args.putLong("reservationId", reservWithSportCenter.reservation.reservationId)
            args.putString("date", reservWithSportCenter.reservation.reservationDate)
            args.putLong("timeslotId", reservWithSportCenter.reservation.timeSlotId)
            args.putString("sportName", reservWithSportCenter.courtWithSportCenter.court.sportName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = requireArguments().getString("username")!!
        sportCenterAddress = requireArguments().getString("sportCenterAddress")!!
        courtName = requireArguments().getString("courtName")!!
        date = requireArguments().getString("date")!!
        timeslotId = requireArguments().getLong("timeslotId")
        sportName = requireArguments().getString("sportName")!!
        courtId = requireArguments().getLong("courtId")
        reservationId = requireArguments().getLong("reservationId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameTV: TextView = view.findViewById(R.id.usernameTV)
        val addressTV: TextView = view.findViewById(R.id.addressTV)
        val dateTV: TextView = view.findViewById(R.id.dateTV)
        val timeslotTV: TextView = view.findViewById(R.id.timeslotTV)
        val courtNameTV: TextView = view.findViewById(R.id.courtnameTV)
        val courtImageIV: ImageView = view.findViewById(R.id.courtImageIV)

        mainContainerCL = view.findViewById(R.id.mainContainerCL)
        mainContainerCL.foreground.alpha = 0

        usernameTV.text = username
        addressTV.text = sportCenterAddress
        dateTV.text = date
        timeslotTV.text = timeslotMap[timeslotId]
        courtNameTV.text = courtName
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
                popupView = inflater.inflate(R.layout.reserv_cancel_confirmation, null)
                val dismissButton = popupView.findViewById<Button>(R.id.dismissButton)
                popupWindow = PopupWindow(popupView, width, height, focusable)
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
}