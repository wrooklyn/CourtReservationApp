package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Service
import it.polito.mad.courtreservationapp.utils.IconUtils
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity

class DescriptionFragment : Fragment() {


    private var services: MutableList<Service> = mutableListOf()
    private var serviceIds: MutableList<Long> = mutableListOf()
    var position: Int = -1
    lateinit var viewModel: SportCenterViewModel
    lateinit var sportCenterWithCourtsAndServices: SportCenterWithCourtsAndServices
    private var type:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        position = requireArguments().getInt("position", -1)
        type = requireArguments().getInt("type")
        sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices[position]
        if(type == 2) {
            sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices.first(){center -> center.sportCenter == viewModel.popularSportCenters[position]}
        }
        sportCenterWithCourtsAndServices.courtsWithServices.forEach(){courtWithServices ->
            courtWithServices.services.forEach(){service ->
                if(!serviceIds.contains(service.serviceId)){
                    services.add(service)
                    serviceIds.add(service.serviceId)
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.descriptionView).text = sportCenterWithCourtsAndServices.sportCenter.description
        serviceInitialize()
        view.findViewById<TextView>(R.id.location_info).text = sportCenterWithCourtsAndServices.sportCenter.address

        view.findViewById<TextView>(R.id.phone_info).text = "+(555) 123-123"
        view.findViewById<TextView>(R.id.time_info).text = "9:00AM - 6.00PM"
    }

    private fun serviceInitialize(){

        val recyclerView: RecyclerView? = view?.findViewById(R.id.service_description_recycler)
        val constraintContainer = view?.findViewById<ConstraintLayout>(R.id.recycler_services_container_description)
        val params: ViewGroup.LayoutParams? = constraintContainer?.layoutParams

        if (services.isEmpty()) {
                params?.height = 0
                view?.findViewById<ConstraintLayout>(R.id.recycler_services_container_description)?.layoutParams=params
        } else {
            val adapter = ServiceDescriptionAdapter(services)
            recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView?.isNestedScrollingEnabled=false
            recyclerView?.adapter = adapter
        }

    }

    class ServiceDescriptionAdapter(private val services: List<Service>): RecyclerView.Adapter<ServiceDescriptionViewHolder>() {

        override fun getItemCount()=services.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceDescriptionViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.service_description_item, parent, false)
            return ServiceDescriptionViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: ServiceDescriptionViewHolder, position: Int) {
            holder.bind(services[position])
        }
    }
    class ServiceDescriptionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.service_description_image)
        private val serviceName: TextView = itemView.findViewById(R.id.service_description_name)
        fun bind(service: Service){
            var drawable: Drawable? = ContextCompat.getDrawable(itemView.context, IconUtils.getServiceIcon(service.serviceId))
            drawable?.setColorFilter(
                ContextCompat.getColor(itemView.context, R.color.white),
                PorterDuff.Mode.SRC_IN
            )
            titleImage.setImageDrawable(drawable)
            serviceName.text= service.description
        }
    }

    companion object{
        fun newInstance(position: Int, type: Int): DescriptionFragment {
            val fragment = DescriptionFragment()
            val args = Bundle()
            args.putInt("position", position)
            args.putInt("type", type)
            fragment.arguments = args
            return fragment
        }
    }
}