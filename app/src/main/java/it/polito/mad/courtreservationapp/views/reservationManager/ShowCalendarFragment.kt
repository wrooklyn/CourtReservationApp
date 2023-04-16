package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import it.polito.mad.courtreservationapp.R
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
        setUpTimePicker(view)
        //todo set the right button
        //view.findViewById<TextView>(R.id.editButtonTV).setOnClickListener{
        //    (activity as CreateReservationActivity)!!.ggNEXT()
        //}

    }

    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpTimePicker(view: View) {
        val recyclerView: RecyclerView =
            view.findViewById(R.id.recyclerView)

        //Initialize data to be displayed
        val myData: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6, 7)

        //Show items as a simple linear list
        recyclerView.layoutManager =
            GridLayoutManager(activity, 3)

        //Populate recyclerView with data
        recyclerView.adapter = TimeSlotAdapter(myData)

    }

    class TimeSlotAdapter(val data: List<Int>) :
        RecyclerView.Adapter<TimeSlotViewHolder>() {
        override fun getItemCount() = data.size
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TimeSlotViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.time_picker_item, parent, false)
            return TimeSlotViewHolder(v)
        }

        override fun onBindViewHolder(
            holder: TimeSlotViewHolder,
            position: Int
        ) {
            val u = data[position] //access data item
            // let the holder use data as needed
            holder.bind(u);
        }
    }

    class TimeSlotViewHolder(v: View) :
        RecyclerView.ViewHolder(v) {
        private val timeSlot: TextView = v.findViewById(R.id.tp_text)
        fun bind(u: Int) {
            val startH = 10 + u
            val endH = startH + 1
            timeSlot.text = "$startH:00 - $endH:00"
        }
    }

    private fun setUpCalendar(view: View) {
        val calendarView = view.findViewById(R.id.calendar_view) as CustomCalendarView

//Initialize calendar with date
        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())
//Show Monday as first date of week
        calendarView.firstDayOfWeek = Calendar.MONDAY

//Show/hide overflow days of a month
        calendarView.setShowOverflowDate(false)

//call refreshCalendar to update calendar the view
        calendarView.refreshCalendar(currentCalendar)

//Handling custom calendar events

//Handling custom calendar events
        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {
                val df = SimpleDateFormat("dd-MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }

            override fun onMonthChanged(date: Date?) {
                val df = SimpleDateFormat("MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }
        })
    }
}