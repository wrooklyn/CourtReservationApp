package it.polito.mad.courtreservationapp.views.reservationManager

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import com.stacktips.view.utils.CalendarUtils
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.adapters.TimeSlotAdapter
import it.polito.mad.courtreservationapp.models.Reservation
import java.text.SimpleDateFormat
import java.util.*


class ShowCalendarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCalendar(view)
        setUpTimePicker(view, activity)

        //todo set the right button
        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener {
            navigateNext();
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener {
            activity?.finish();
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener {
            activity?.finish();
        }
        if ((activity as CreateReservationActivity).viewModel.reservationDate == null)
            view.findViewById<TextView>(R.id.timeslot_heading).text = "Please pick a date"
        else
            view.findViewById<TextView>(R.id.timeslot_heading).text = "Available Hours"
    }

    private fun navigateNext() {
        val a = (activity as CreateReservationActivity)
        if (a.viewModel.reservationDate.isNullOrEmpty() || a.viewModel.reservationTimeSlots.isNullOrEmpty()) {
            Toast.makeText(activity, "Please pick a valid date/timeslot.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        (activity as CreateReservationActivity).ggNEXT();
    }

    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpTimePicker(view: View, activity: FragmentActivity?) {
        val recyclerView: RecyclerView =
            view.findViewById(R.id.recyclerView)

        //Initialize data to be displayed
        val myData: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        //Show items as a simple linear list
        recyclerView.layoutManager =
            GridLayoutManager(activity, 3)

        //Populate recyclerView with data
        recyclerView.adapter = TimeSlotAdapter(myData, activity)

    }


    private fun setUpCalendar(view: View) {
        val calendarView = view.findViewById(R.id.calendar_view) as CustomCalendarView

        //Initialize calendar with date
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
        //Show Monday as first date of week
        calendarView.firstDayOfWeek = Calendar.MONDAY

        //Show/hide overflow days of a month
        calendarView.setShowOverflowDate(true)

        //call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar)


        //Handling custom calendar events
        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {

                if (!CalendarUtils.isPastDay(date)) {
                    val df = SimpleDateFormat("yyyy-MM-dd")
                    (activity as CreateReservationActivity).viewModel.reservationDate = df.format(date)
                    (activity as CreateReservationActivity).viewModel.reservationTimeSlots.clear()
                    view.findViewById<TextView>(R.id.timeslot_heading).text = "Available Hours"
                } else {
                    Toast.makeText(activity, "Cannot reserve a past date.", Toast.LENGTH_SHORT).show()
                    (activity as CreateReservationActivity).viewModel.reservationDate = null
                    view.findViewById<TextView>(R.id.timeslot_heading).text = "Please pick a date"
                }
                view.findViewById<RecyclerView>(R.id.recyclerView).adapter?.notifyDataSetChanged()
            }

            override fun onMonthChanged(date: Date?) {
                val df = SimpleDateFormat("MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }
        })
    }
}