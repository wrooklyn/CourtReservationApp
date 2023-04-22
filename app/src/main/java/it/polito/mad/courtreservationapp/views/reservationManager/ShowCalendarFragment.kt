package it.polito.mad.courtreservationapp.views.reservationManager

import android.app.Activity
import android.content.ContentProvider
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.R.color.red_button
import it.polito.mad.courtreservationapp.models.Reservation
import java.text.SimpleDateFormat
import java.util.*


class ShowCalendarFragment : Fragment() {
    var selectedSlot: Int? = null;
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
        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener{
            navigateNext();
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener{
            activity?.finish();
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener{
            activity?.finish();
        }

    }

    private fun navigateNext(){
        if(true) { //check fields are set
            (activity as CreateReservationActivity).ggNEXT();
        }else{
            Toast.makeText(activity, "Please pick a valid date/timeslot.", Toast.LENGTH_SHORT).show()
        }
    }

    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpTimePicker(view: View, activity : FragmentActivity?) {
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

    class TimeSlotAdapter(val data: List<Int>, val activity : FragmentActivity?) :
        RecyclerView.Adapter<TimeSlotViewHolder>() {
        override fun getItemCount() = data.size

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TimeSlotViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.time_picker_item, parent, false)
            return TimeSlotViewHolder(v, activity)
        }

        override fun onBindViewHolder(
            holder: TimeSlotViewHolder,
            position: Int
        ) {
            val u = data[position] //access data item
            // let the holder use data as needed
            holder.bind(u)
        }
    }

    class TimeSlotViewHolder(v: View, val activity : FragmentActivity?) :
        RecyclerView.ViewHolder(v) {
        private val timeSlotButton: Button = v.findViewById(R.id.tp_text)
        fun bind(u: Int) {
            val startH = 10 + u
            val endH = startH + 1
            timeSlotButton.text="$startH:00 - $endH:00"


            if((activity as CreateReservationActivity).reservationDate==null){
                timeSlotButton.setBackgroundColor(Color.BLACK)
                return
            }
            if(activity.reservationTimeSlot.contains(u)){
                timeSlotButton.setBackgroundColor(Color.RED)
            }
            else if (activity.reservationsByDateString[activity.reservationDate]!=null){
                if(activity.reservationsByDateString[activity.reservationDate]!!.contains(u.toLong())){

                    Log.v("gabri", activity.reservationsByDateString[activity.reservationDate].toString());
                    timeSlotButton.setBackgroundColor(Color.GRAY)
                    timeSlotButton.isEnabled=false
                    return
                }
                timeSlotButton.setBackgroundColor(Color.GREEN)
            }else{
                timeSlotButton.setBackgroundColor(Color.GREEN)
            }
            timeSlotButton.setOnClickListener{
                if(activity.reservationTimeSlot.contains(u)){
                    activity.reservationTimeSlot.remove(u);
                    timeSlotButton.setBackgroundColor(Color.GREEN);
                }else{
                    activity.reservationTimeSlot.add(u);
                    timeSlotButton.setBackgroundColor(Color.RED);
                }
                Toast.makeText(activity, "timeslot $u", Toast.LENGTH_SHORT).show()
                Log.v("gabri",activity.reservationTimeSlot.toString())
            }

        }
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
                val df = SimpleDateFormat("yyyy-MM-dd")
                (activity as CreateReservationActivity).reservationDate = df.format(date);
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
                view.findViewById<RecyclerView>(R.id.recyclerView).adapter?.notifyDataSetChanged()
                (activity as CreateReservationActivity).reservationTimeSlot.clear()
            }

            override fun onMonthChanged(date: Date?) {
                val df = SimpleDateFormat("MM-yyyy")
                Toast.makeText(activity, df.format(date), Toast.LENGTH_SHORT).show()
            }
        })
    }
}