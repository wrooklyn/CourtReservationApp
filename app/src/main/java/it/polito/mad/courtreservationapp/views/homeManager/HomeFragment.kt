package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R

class HomeFragment : Fragment() {

    lateinit var imageId: Array<Int>
    lateinit var sportName: Array<String>
    lateinit var locationName: Array<String>
    lateinit var centerName: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sportInitialize()
        availableInitialize()
        popularInitialize()
    }

    private fun sportInitialize(){
        imageId = arrayOf(
            R.drawable.soccer_ball,
            R.drawable.ice_skate,
            R.drawable.basketball_icon,
            R.drawable.hockey,
            R.drawable.tennis,
            R.drawable.volleyball,
            R.drawable.rudgby
        )
        sportName=arrayOf(
            getString(R.string.soccer),
            getString(R.string.iceskate),
            getString(R.string.basketball),
            getString(R.string.hockey),
            getString(R.string.tennis),
            getString(R.string.volleyball),
            getString(R.string.rugby),
        )
        val recyclerView: RecyclerView? = view?.findViewById(R.id.sports_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = SportsAdapter(imageId, sportName)
    }
    private fun availableInitialize(){
        imageId = arrayOf(
            R.drawable.basket_center,
            R.drawable.golf_center,
            R.drawable.run_center,
            R.drawable.volley_center
        )
        locationName=arrayOf(
            getString(R.string.location_center),
            getString(R.string.location_center),
            getString(R.string.location_center),
            getString(R.string.location_center)
        )
        centerName=arrayOf(
            getString(R.string.sportsplex),
            getString(R.string.sportsplex),
            getString(R.string.sportsplex),
            getString(R.string.sportsplex)
        )
        val recyclerView: RecyclerView? = view?.findViewById(R.id.available_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = AvailableAdapter(imageId, locationName, centerName)
        recyclerView?.isNestedScrollingEnabled = false


    }
    private fun popularInitialize(){
        imageId = arrayOf(
            R.drawable.basket_center,
            R.drawable.golf_center,
            R.drawable.run_center,
            R.drawable.volley_center
        )
        locationName=arrayOf(
            getString(R.string.location_center),
            getString(R.string.location_center),
            getString(R.string.location_center),
            getString(R.string.location_center)
        )
        centerName=arrayOf(
            getString(R.string.sportsplex),
            getString(R.string.sportsplex),
            getString(R.string.sportsplex),
            getString(R.string.sportsplex)
        )
        val recyclerView: RecyclerView? = view?.findViewById(R.id.popular_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = PopularAdapter(imageId, locationName, centerName)
        recyclerView?.isNestedScrollingEnabled = false
    }

    //Sports ScrollView
    class SportsAdapter(private val imagesList: Array<Int>, private val namesList: Array<String>): RecyclerView.Adapter<SportsViewHolder>() {

        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_list_item, parent, false)
            return SportsViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentName = namesList[position]
            holder.bind(currentImage, currentName)
        }
    }
    class SportsViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_image1)
        private val sportName: TextView = itemView.findViewById(R.id.sport_name)
        fun bind(imageSrc: Int, activityName: String){
            titleImage.setImageResource(imageSrc)
            sportName.text=activityName
        }
    }

    //Available Now
    class AvailableAdapter(private val imagesList: Array<Int>, private val locationList: Array<String>, private val centerList: Array<String>): RecyclerView.Adapter<AvailableViewHolder>() {

        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return AvailableViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentLocation = locationList[position]
            val currentCenter = centerList[position]
            holder.bind(currentImage, currentLocation, currentCenter)
        }
    }
    class AvailableViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: Int, lName: String, cName:String){
            titleImage.setImageResource(imageSrc)
            locationName.text=lName
            centerName.text = cName
        }
    }

    //Popular Centers
    class PopularAdapter(private val imagesList: Array<Int>, private val locationList: Array<String>, private val centerList: Array<String>): RecyclerView.Adapter<PopularViewHolder>() {

        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return PopularViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentLocation = locationList[position]
            val currentCenter = centerList[position]
            holder.bind(currentImage, currentLocation, currentCenter)
        }
    }
    class PopularViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: Int, lName: String, cName:String){
            titleImage.setImageResource(imageSrc)
            locationName.text=lName
            centerName.text = cName
        }
    }
}