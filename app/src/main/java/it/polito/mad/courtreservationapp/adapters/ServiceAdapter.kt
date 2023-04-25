package it.polito.mad.courtreservationapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

class ServicesAdapter(val data: List<Service>, val activity: CreateReservationActivity) :
    RecyclerView.Adapter<ServicesViewHolder>() {
    override fun getItemCount() = data.size
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicesViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.services_item, parent, false)
        return ServicesViewHolder(v, activity)
    }

    override fun onBindViewHolder(
        holder: ServicesViewHolder,
        position: Int
    ) {
        val u = data[position] //access data item
        // let the holder use data as needed
        holder.bind(u);
    }
}

class ServicesViewHolder(v: View, activity: CreateReservationActivity) :
    RecyclerView.ViewHolder(v) {
    private val icon: ImageView = v.findViewById(R.id.sv_icon)
    private val layout: ConstraintLayout = v.findViewById(R.id.sv_layout)
    private val text: TextView = v.findViewById(R.id.sv_text)
    private val a = activity
    fun bind(u: Service) {
        text.text = u.description


        val blue = ContextCompat.getColor(a, R.color.deep_blue)
        val grey = Color.parseColor("#EFEFEF")
        var drawable: Drawable? = null
        when (u.serviceId.toInt()) {
            0 -> drawable = ContextCompat.getDrawable(a, R.drawable.shower_sv)
            1 -> drawable = ContextCompat.getDrawable(a, R.drawable.equipment_sv)
            2 -> drawable = ContextCompat.getDrawable(a, R.drawable.personal_trainer_sv)
            3 -> drawable = ContextCompat.getDrawable(a, R.drawable.food_sv)
        }


        if (a.viewModel.reservationServices.contains(u.serviceId)) {
            layout.backgroundTintList = ColorStateList.valueOf(blue)
            icon.setBackgroundColor(blue)
            drawable?.setColorFilter(
                ContextCompat.getColor(a, R.color.white),
                PorterDuff.Mode.SRC_IN
            )
            icon.setImageDrawable(drawable)
            Log.v("gabri", a.viewModel.reservationServices.toString())
            Log.v("gabri", u.serviceId.toString())
        } else {
            layout.backgroundTintList = ColorStateList.valueOf(grey)
            icon.setBackgroundColor(grey)
            drawable?.setColorFilter(
                ContextCompat.getColor(a, R.color.black),
                PorterDuff.Mode.SRC_IN
            )
            icon.setImageDrawable(drawable)
        }


        layout.setOnClickListener {
            if (a.viewModel.reservationServices.contains(u.serviceId)) {
                a.viewModel.reservationServices.remove(u.serviceId);
                layout.backgroundTintList = ColorStateList.valueOf(grey)
                icon.setBackgroundColor(grey)
                drawable?.setColorFilter(
                    ContextCompat.getColor(a, R.color.black),
                    PorterDuff.Mode.SRC_IN
                )
                icon.setImageDrawable(drawable)
            } else {
                a.viewModel.reservationServices.add(u.serviceId);
                layout.backgroundTintList = ColorStateList.valueOf(blue)
                icon.setBackgroundColor(blue)
                drawable?.setColorFilter(
                    ContextCompat.getColor(a, R.color.white),
                    PorterDuff.Mode.SRC_IN
                )
                icon.setImageDrawable(drawable)
            }
        }
    }
}