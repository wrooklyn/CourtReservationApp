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
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.views.ShowProfileFragment

class HomeFragment : Fragment() {

    lateinit var imageId: Array<Int>
    lateinit var sportName: Array<String>
    private lateinit var locationName: Array<String>
    private lateinit var centerName: Array<String>
    private lateinit var centersList: Array<SportCenter>
    private lateinit var popularCentersList: Array<SportCenter>


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
        val adapter = SportsAdapter(imageId, sportName)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        adapter.setOnItemClickListener(object : SportsAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
               /* val name=sportName[position]
                Toast.makeText(context, "You clicked on item no. $name", Toast.LENGTH_SHORT).show()*/
            }

        })
    }
    private fun availableInitialize(){
        centersList = emptyArray()
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
        for (i in centerName.indices){
            val sportCenter = SportCenter(centerName[i], locationName[i])
            centersList=centersList.plus(sportCenter)
        }
        val adapter=AvailableAdapter(centersList, imageId)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.available_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : AvailableAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = CenterDetailFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })


    }
    private fun popularInitialize(){
        popularCentersList = emptyArray()

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
        for (i in centerName.indices){
            val sportCenter = SportCenter(centerName[i], locationName[i])
            popularCentersList=popularCentersList.plus(sportCenter)
        }
        val adapter=PopularAdapter(imageId, popularCentersList)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.popular_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : PopularAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = ShowProfileFragment()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })
    }

    //Sports ScrollView
    class SportsAdapter(private val imagesList: Array<Int>, private val namesList: Array<String>): RecyclerView.Adapter<SportsViewHolder>() {

        private lateinit var sListener: onItemClickListener
        interface onItemClickListener{
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: onItemClickListener){
            sListener=listener
        }
        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_list_item, parent, false)
            return SportsViewHolder(itemView, sListener)
        }
        override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentName = namesList[position]
            holder.bind(currentImage, currentName)
        }
    }
    class SportsViewHolder(itemView: View, listener: SportsAdapter.onItemClickListener):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_image1)
        private val sportName: TextView = itemView.findViewById(R.id.sport_name)
        fun bind(imageSrc: Int, activityName: String){
            titleImage.setImageResource(imageSrc)
            sportName.text=activityName
        }
        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

    class AvailableAdapter(private val centersList: Array<SportCenter>, private val imagesList: Array<Int>): RecyclerView.Adapter<AvailableViewHolder>() {

        private lateinit var availableListener: onItemClickListener
        interface onItemClickListener{
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: onItemClickListener){
            availableListener=listener
        }
        override fun getItemCount()=centersList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return AvailableViewHolder(itemView, availableListener)
        }
        override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentLocation = centersList[position].address
            val currentCenter = centersList[position].name

            holder.bind(currentImage, currentLocation, currentCenter)
        }
    }
    class AvailableViewHolder(itemView: View, listener: AvailableAdapter.onItemClickListener):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: Int, lName: String, cName:String){
            titleImage.setImageResource(imageSrc)
            locationName.text=lName
            centerName.text = cName
        }
        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

    //Popular Centers
    class PopularAdapter(private val imagesList: Array<Int>, private val popularCentersList: Array<SportCenter>): RecyclerView.Adapter<PopularViewHolder>() {

        private lateinit var popularListener: onItemClickListener
        interface onItemClickListener{
            fun onItemClick(position: Int)
        }
        fun setOnItemClickListener(listener: onItemClickListener){
            popularListener=listener
        }
        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return PopularViewHolder(itemView, popularListener)
        }
        override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentLocation = popularCentersList[position].address
            val currentCenter = popularCentersList[position].name
            holder.bind(currentImage, currentLocation, currentCenter)
        }
    }
    class PopularViewHolder(itemView: View, listener: PopularAdapter.onItemClickListener):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: Int, lName: String, cName:String){
            titleImage.setImageResource(imageSrc)
            locationName.text=lName
            centerName.text = cName
        }
        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }
}