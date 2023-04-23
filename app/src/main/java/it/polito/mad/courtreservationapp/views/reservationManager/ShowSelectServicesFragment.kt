package it.polito.mad.courtreservationapp.views.reservationManager

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.Reservation
import it.polito.mad.courtreservationapp.models.Service


class ShowSelectServicesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_show_select_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpServicesPicker(view, (activity as CreateReservationActivity))
        val editText=view.findViewById<EditText>(R.id.sv_text_field)
        editText.setText((activity as CreateReservationActivity).reservationRequests)
        //todo set the right button
        view.findViewById<Button>(R.id.f1_confirm_button).setOnClickListener{
            (activity as CreateReservationActivity).reservationRequests=editText.text.toString()
            (activity as CreateReservationActivity).ggNEXT();
        }
        view.findViewById<Button>(R.id.f1_back_button).setOnClickListener{
            (activity as CreateReservationActivity).reservationRequests=editText.text.toString()
            (activity as CreateReservationActivity).ggBack();
        }
        view.findViewById<ImageView>(R.id.close_button).setOnClickListener{
            activity?.finish();
        }
        //view.findViewById<RecyclerView>(R.id.services_recycler).adapter?.notifyDataSetChanged()
        //TODO dispose?
    }
    fun getReservationOfCourts(Cid: String): List<Reservation> {
        //does stuff
        return listOf();
    }

    private fun setUpServicesPicker(view: View, activity: CreateReservationActivity) {
        val recyclerView: RecyclerView =
            view.findViewById(R.id.services_recycler)


        //Show items as a simple linear list
        recyclerView.layoutManager =
            GridLayoutManager(activity, 2)

        //Populate recyclerView with data
        recyclerView.adapter = ServicesAdapter(activity.courtServ.services, activity)

    }
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
        private val layout : ConstraintLayout = v.findViewById(R.id.sv_layout)
        private val text: TextView = v.findViewById(R.id.sv_text)
        private val a = activity
        fun bind(u: Service) {
            text.text=u.description



            val blue = ContextCompat.getColor(a, R.color.deep_blue)
            val grey = Color.parseColor("#EFEFEF")
            var drawable : Drawable? = null
            when (u.serviceId.toInt()){
                0->drawable= ContextCompat.getDrawable(a, R.drawable.shower_sv)
                1->drawable= ContextCompat.getDrawable(a, R.drawable.equipment_sv)
                2->drawable= ContextCompat.getDrawable(a, R.drawable.personal_trainer_sv)
                3->drawable= ContextCompat.getDrawable(a, R.drawable.food_sv)
            }


            if (a.reservationServices.contains(u.serviceId)){
                layout.backgroundTintList = ColorStateList.valueOf(blue)
                icon.setBackgroundColor(blue)
                drawable?.setColorFilter(ContextCompat.getColor(a, R.color.white), PorterDuff.Mode.SRC_IN)
                icon.setImageDrawable(drawable)
                Log.v("gabri", a.reservationServices.toString())
                Log.v("gabri", u.serviceId.toString())
            }else{
                layout.backgroundTintList = ColorStateList.valueOf(grey)
                icon.setBackgroundColor(grey)
                drawable?.setColorFilter(ContextCompat.getColor(a, R.color.black), PorterDuff.Mode.SRC_IN)
                icon.setImageDrawable(drawable)
            }


            layout.setOnClickListener {
                if (a.reservationServices.contains(u.serviceId)) {
                    a.reservationServices.remove(u.serviceId);
                    layout.backgroundTintList = ColorStateList.valueOf(grey)
                    icon.setBackgroundColor(grey)
                    drawable?.setColorFilter(ContextCompat.getColor(a, R.color.black), PorterDuff.Mode.SRC_IN)
                    icon.setImageDrawable(drawable)
                } else {
                    a.reservationServices.add(u.serviceId);
                    layout.backgroundTintList = ColorStateList.valueOf(blue)
                    icon.setBackgroundColor(blue)
                    drawable?.setColorFilter(ContextCompat.getColor(a, R.color.white), PorterDuff.Mode.SRC_IN)
                    icon.setImageDrawable(drawable)
                }
            }
        }
    }

}