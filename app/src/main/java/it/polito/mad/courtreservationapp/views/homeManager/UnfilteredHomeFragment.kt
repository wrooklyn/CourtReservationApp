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

class UnfilteredHomeFragment : Fragment() {

    private lateinit var viewModel: SportCenterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        viewModel.sportCentersLiveData.observe(this){
            viewModel.loadSportCenters(it)
            availableInitialize()
            popularInitialize()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_unfiltered_home, container, false)
    }

    private fun availableInitialize(){
        val centers = viewModel.sportCentersWithCourtsAndServices.map { center ->
            center.sportCenter
        }/*.filter{
            it.centerId < 2
        }*/


        val adapter= AvailableAdapter(centers, viewModel.sportCenterImages)
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

        val adapter= PopularAdapter(viewModel.sportCenterImages, popularCentersList)
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