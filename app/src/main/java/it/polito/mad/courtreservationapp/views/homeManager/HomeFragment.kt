package it.polito.mad.courtreservationapp.views.homeManager

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
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.ShowProfileFragment

class HomeFragment : Fragment() {

    private lateinit var viewModel: SportCenterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        viewModel.sportCentersLiveData.observe(this){
            viewModel.loadSportCenters(it)
//            Log.i("Home", "SportNames: $sportNames")
            sportInitialize()
            availableInitialize()
            popularInitialize()
        }

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
    }

    private fun sportInitialize(){

        val recyclerView: RecyclerView? = view?.findViewById(R.id.sports_recycler)
        val adapter = SportsAdapter(viewModel.sportIconsId, viewModel.allSports)
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
        val centers = viewModel.sportCentersWithCourtsAndServices.map { center ->
            center.sportCenter
        }/*.filter{
            it.centerId < 2
        }*/


        val adapter=AvailableAdapter(centers, viewModel.sportCenterImages)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.available_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : AvailableAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = CenterDetailFragment.newInstance(position)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })


    }
    private fun popularInitialize(){
        val popularCentersList = viewModel.sportCentersWithCourtsAndServices.map { center ->
            center.sportCenter
        }

        val adapter=PopularAdapter(viewModel.sportCenterImages, popularCentersList)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.popular_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : PopularAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = CenterDetailFragment.newInstance(position)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })
    }

    //Sports ScrollView
    class SportsAdapter(private val imagesList: Map<String, Int>, private val namesList: List<String>): RecyclerView.Adapter<SportsViewHolder>() {

        private lateinit var sListener: onItemClickListener
        interface onItemClickListener{
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: onItemClickListener){
            sListener=listener
        }
        override fun getItemCount()=namesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_list_item, parent, false)
            return SportsViewHolder(itemView, sListener)
        }
        override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
            val currentName = namesList[position]
            val currentImage = imagesList[currentName] ?: R.drawable.gesu
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

    class AvailableAdapter(private val centersList: List<SportCenter>, private val imagesList: Map<Long, Int>): RecyclerView.Adapter<AvailableViewHolder>() {

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
            val currentCenter = centersList[position]
            val currentImage = imagesList[currentCenter.centerId] ?: R.drawable.gesu
            val currentLocation = currentCenter.address


            holder.bind(currentImage, currentLocation, currentCenter.name)
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
    class PopularAdapter(private val imagesList: Map<Long, Int>, private val popularCentersList: List<SportCenter>): RecyclerView.Adapter<PopularViewHolder>() {

        private lateinit var popularListener: onItemClickListener
        interface onItemClickListener{
            fun onItemClick(position: Int)
        }
        fun setOnItemClickListener(listener: onItemClickListener){
            popularListener=listener
        }
        override fun getItemCount()=popularCentersList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return PopularViewHolder(itemView, popularListener)
        }
        override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
            Log.i("PopularAdapter", "position $position")
            Log.i("PopularAdapter", "populars $popularCentersList")
            val currentCenter = popularCentersList[position]
            val currentImage = imagesList[currentCenter.centerId] ?: R.drawable.gesu
            val currentLocation = popularCentersList[position].address
            holder.bind(currentImage, currentLocation, currentCenter.name)
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