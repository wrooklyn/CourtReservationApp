package it.polito.mad.courtreservationapp.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeSlotAdapter(val data: List<Int>, val activity: FragmentActivity?) :
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
        holder.bind(u.toLong())
    }
}

class TimeSlotViewHolder(v: View, val activity: FragmentActivity?) :
    RecyclerView.ViewHolder(v) {
    private val timeSlotButton: Button = v.findViewById(R.id.tp_text)
    private val today = LocalDateTime.now()
    fun bind(u: Long) {
        val a = activity as CreateReservationActivity
        val startH = 10 + u
        val endH = startH + 1

        timeSlotButton.text = "$startH:00 - $endH:00"
        val orange = ContextCompat.getColor(activity, R.color.orange_highlight)
        val grey = ContextCompat.getColor(activity, R.color.grey_unselected)
        timeSlotButton.visibility = View.VISIBLE
        if (a.viewModel.reservationDate == null) {
            timeSlotButton.visibility = View.INVISIBLE
            return
        }

        //checking timeslot if today is selected
       if (checkIsTodayForTimeSlots(a.viewModel.reservationDate, startH)) return

        if (a.viewModel.reservationTimeSlots.contains(u)) {
            timeSlotButton.setBackgroundColor(orange)
            timeSlotButton.setTextColor(Color.WHITE)
        } else if (a.viewModel.reservationsByDateMap[a.viewModel.reservationDate] != null) {
            if (a.viewModel.reservationsByDateMap[a.viewModel.reservationDate]!!.contains(u)) {
                timeSlotButton.setBackgroundColor(Color.TRANSPARENT)
                timeSlotButton.setTextColor(Color.GRAY)
                timeSlotButton.isEnabled = false
                return
            }
            timeSlotButton.isEnabled = true
            timeSlotButton.setBackgroundColor(grey)
            timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"))
        } else {
            timeSlotButton.isEnabled = true
            timeSlotButton.setBackgroundColor(grey)
            timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"))
        }
        timeSlotButton.setOnClickListener {
            if (a.viewModel.reservationTimeSlots.contains(u)) {
                a.viewModel.reservationTimeSlots.remove(u)
                timeSlotButton.setBackgroundColor(grey)
                timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"))
            } else {
                a.viewModel.reservationTimeSlots.add(u)
                timeSlotButton.setBackgroundColor(orange)
                timeSlotButton.setTextColor(Color.WHITE)
            }
        }

    }

    private fun checkIsTodayForTimeSlots(reservationDate: String?, startH: Long) : Boolean {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val dateStr = today.format(df)
        if(reservationDate == dateStr){
            Log.v("xpxpxp", dateStr)
            val df = DateTimeFormatter.ofPattern("hh")
            val timeStr = today.format(df)
            Log.v("xpxpxp", "Hours: $timeStr")
            if(startH <= timeStr.toLong()){
                Log.v("xpxpxp", "true")
                this.timeSlotButton.setBackgroundColor(Color.TRANSPARENT)
                this.timeSlotButton.setTextColor(Color.GRAY)
                this.timeSlotButton.isEnabled = false
                return true
            }

        }
        return false
    }
}