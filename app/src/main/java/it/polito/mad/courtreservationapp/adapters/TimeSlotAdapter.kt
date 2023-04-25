package it.polito.mad.courtreservationapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

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
    fun bind(u: Long) {
        val a = activity as CreateReservationActivity
        val startH = 10 + u
        val endH = startH + 1
        timeSlotButton.text = "$startH:00 - $endH:00"
        val orange = ContextCompat.getColor(activity, R.color.orange_highlight)
        val grey = ContextCompat.getColor(activity, R.color.grey_unselected)
        timeSlotButton.visibility = View.VISIBLE
        if (a.reservationDate == null) {
            timeSlotButton.visibility = View.INVISIBLE
            return
        }
        if (a.reservationTimeSlot.contains(u)) {
            timeSlotButton.setBackgroundColor(orange)
            timeSlotButton.setTextColor(Color.WHITE);
        } else if (a.reservationsByDateString[a.reservationDate] != null) {
            if (a.reservationsByDateString[a.reservationDate]!!.contains(u.toLong())) {
                timeSlotButton.setBackgroundColor(Color.TRANSPARENT)
                timeSlotButton.setTextColor(Color.GRAY);
                timeSlotButton.isEnabled = false
                return
            }
            timeSlotButton.setBackgroundColor(grey);
            timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"));
        } else {
            timeSlotButton.setBackgroundColor(grey);
            timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"));
        }
        timeSlotButton.setOnClickListener {
            if (a.reservationTimeSlot.contains(u)) {
                a.reservationTimeSlot.remove(u);
                timeSlotButton.setBackgroundColor(grey);
                timeSlotButton.setTextColor(Color.parseColor("#4F4F4F"));
            } else {
                a.reservationTimeSlot.add(u);
                timeSlotButton.setBackgroundColor(orange);
                timeSlotButton.setTextColor(Color.WHITE);
            }
        }

    }
}