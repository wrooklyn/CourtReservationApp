package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.homeManager.HomeFragment

class DescriptionFragment : Fragment() {

    lateinit var imageId: Array<Int>
    lateinit var serviceName: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceInitialize()
    }

    private fun serviceInitialize(){
        imageId = arrayOf(
            R.drawable.swimming,
            R.drawable.wifi,
            R.drawable.safety_shower,
            R.drawable.cafe

        )
        serviceName= arrayOf(
            "Pool",
            "Wifi",
            "Shower",
            "Cafeteria"
        )
//        Log.i("serviceInitialize", serviceName[0])
        val recyclerView: RecyclerView? = view?.findViewById(R.id.service_description_recycler)
        val adapter = ServiceDescriptionAdapter(imageId, serviceName)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class ServiceDescriptionAdapter(private val imagesList: Array<Int>, private val namesList: Array<String>): RecyclerView.Adapter<ServiceDescriptionViewHolder>() {

        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceDescriptionViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.service_description_item, parent, false)
//            Log.i("serviceAdapter", namesList[0])
            return ServiceDescriptionViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: ServiceDescriptionViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentName = namesList[position]
//            Log.i("onBindViewHolder", position.toString())
            holder.bind(currentImage, currentName)
        }
    }
    class ServiceDescriptionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.service_description_image)
        private val serviceName: TextView = itemView.findViewById(R.id.service_description_name)
        fun bind(imageSrc: Int, activityName: String){
//            Log.i("bindViewHolder", activityName)
            titleImage.setImageResource(imageSrc)
            serviceName.text=activityName
        }
    }
}