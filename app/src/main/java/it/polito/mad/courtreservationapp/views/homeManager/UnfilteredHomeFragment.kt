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
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWIthCourtsAndReviewsAndUsers
import it.polito.mad.courtreservationapp.models.SportCenter
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity

class UnfilteredHomeFragment : Fragment() {

    private lateinit var viewModel: SportCenterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        viewModel.sportCentersLiveData.observe(this){
//            viewModel.loadSportCenters(it)
            availableInitialize()

        }
        viewModel.sportCentersWithReviewsAndUsersLiveData.observe(this){
            popularInitialize(it)
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


        val adapter = AvailableAdapter(centers)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.available_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : AvailableAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = CenterDetailFragment.newInstance(position)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })


    }
    private fun popularInitialize(sportCenterWIthCourtsAndReviewsAndUsers: List<SportCenterWIthCourtsAndReviewsAndUsers>) {
        Log.i("UnfilteredHome", "$sportCenterWIthCourtsAndReviewsAndUsers")
        val popularCentersList = sportCenterWIthCourtsAndReviewsAndUsers.filter { center ->
            Log.i("UnfilteredHome", "center: ${center.sportCenter.name}")

            val courtSum = center.courtsWithReviewsAndUsers.fold(0.0){ acc, court ->
                Log.i("UnfilteredHome", "court: ${court.court.sportName}")
//                Log.i("UnfilteredHome", "Acc: $acc")
                Log.i("UnfilteredHome", "reviews: ${court.reviewsWithUser}")
                if(court.reviewsWithUser.isNotEmpty()){
                    acc + court.reviewsWithUser.sumOf { reviewWithUser ->
                        Log.i("UnfilteredHome", "rating: ${reviewWithUser.review.rating}")
                        reviewWithUser.review.rating
                    }
                } else {
                    acc
                }

            }
            val numReviews = center.courtsWithReviewsAndUsers.fold(0){ acc, court ->
                acc + court.reviewsWithUser.size
            }
            Log.i("UnfilteredHome", "sum: $courtSum")
            Log.i("UnfilteredHome", "num: $numReviews")
            val centerAvg = courtSum/numReviews
            Log.i("UnfilteredHome", "centerAvg: $centerAvg")
            centerAvg >= 4
        }.map { center ->
            Log.i("UnfilteredHome", "$center")
            center.sportCenter
        }

        val adapter= PopularAdapter(popularCentersList)
        val recyclerView: RecyclerView? = view?.findViewById(R.id.popular_recycler)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        recyclerView?.isNestedScrollingEnabled = false
        adapter.setOnItemClickListener(object : PopularAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
                val fragmentB = CenterDetailFragment.newInstance(position)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainer, fragmentB, "fragmentId")
                    ?.commit();
            }

        })
    }

    class AvailableAdapter(private val centersList: List<SportCenter>): RecyclerView.Adapter<AvailableViewHolder>() {

        private lateinit var availableListener: OnItemClickListener
        interface OnItemClickListener{
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: OnItemClickListener){
            availableListener=listener
        }
        override fun getItemCount()=centersList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return AvailableViewHolder(itemView, availableListener)
        }
        override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
            val currentCenter = centersList[position]

//            val image = it.polito.mad.courtreservationapp.db.RemoteRepository.FireSportCenterRepository
//            val currentImage =
                //imagesList[currentCenter.centerId] ?:
//            R.drawable.gesu
            val currentLocation = currentCenter.address


            holder.bind(currentCenter.image, currentLocation, currentCenter.name)
        }
    }
    class AvailableViewHolder(itemView: View, listener: AvailableAdapter.OnItemClickListener):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: String?, lName: String, cName:String){
            ImageUtils.setImage("centers", imageSrc, titleImage)
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
    class PopularAdapter(private val popularCentersList: List<SportCenter>): RecyclerView.Adapter<PopularViewHolder>() {

        private lateinit var popularListener: OnItemClickListener
        interface OnItemClickListener{
            fun onItemClick(position: Int)
        }
        fun setOnItemClickListener(listener: OnItemClickListener){
            popularListener=listener
        }
        override fun getItemCount()=popularCentersList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_center_card_item, parent, false)
            return PopularViewHolder(itemView, popularListener)
        }
        override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
            val currentCenter = popularCentersList[position]
            val currentLocation = popularCentersList[position].address
            holder.bind(currentCenter.image, currentLocation, currentCenter.name)
        }
    }
    class PopularViewHolder(itemView: View, listener: PopularAdapter.OnItemClickListener):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_center1)
        private val locationName: TextView = itemView.findViewById(R.id.location_name)
        private val centerName: TextView = itemView.findViewById(R.id.center_name)
        fun bind(imageSrc: String?, lName: String, cName:String){
            ImageUtils.setImage("centers", imageSrc, titleImage)
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