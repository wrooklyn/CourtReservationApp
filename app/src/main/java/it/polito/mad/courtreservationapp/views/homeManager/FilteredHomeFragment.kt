package it.polito.mad.courtreservationapp.views.homeManager

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.CourtFragment
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

class FilteredHomeFragment : Fragment() {

//    var position: Int = -1
    lateinit var viewModel: SportCenterViewModel
    lateinit var sportCentersWithCourtsAndServices: List<SportCenterWithCourtsAndServices>
    private var courts: MutableList<Court> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).sportCenterViewModel
//        position = requireArguments().getInt("position", -1)
        sportCentersWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices
        sportCentersWithCourtsAndServices.forEach{ sportCenter ->
            sportCenter.courtsWithServices.forEach { court ->
                if(viewModel.sportFilters.contains(court.court.sportName)){
                    courts.add(court.court)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filtered_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceInitialize()
    }

    private fun serviceInitialize(){

        val recyclerView: RecyclerView? = view?.findViewById(R.id.home_filtered_court_recycler)
        val adapter = FilteredCourtsAdapter(viewModel.courtImages, courts)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class FilteredCourtsAdapter(private val imageMap: Map<String, Int>, private val courts: MutableList<Court>): RecyclerView.Adapter<FilteredCourtsViewHolder>() {

        override fun getItemCount()=courts.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilteredCourtsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.reserve_card_item, parent, false)
            return FilteredCourtsViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: FilteredCourtsViewHolder, position: Int) {
            val currentCourt = courts[position]
            val currentImage = imageMap[currentCourt.sportName] ?: R.drawable.gesu
            val currentReview = "5.0 (124 reviews)"
            holder.bind(currentImage, "${currentCourt.sportName} Court", currentReview)
            holder.itemView.findViewById<Button>(R.id.reserveButton).setOnClickListener{
                val createReservationIntent: Intent = Intent(holder.itemView.context, CreateReservationActivity::class.java)
                createReservationIntent.putExtra("sportCenterId",currentCourt.courtCenterId)
                createReservationIntent.putExtra("courtId",currentCourt.courtId)
                holder.itemView.context.startActivity(createReservationIntent)
            }
        }
    }
    class FilteredCourtsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.court_image)
        private val courtType: TextView = itemView.findViewById(R.id.court_type)
        private val reviewCourt: TextView = itemView.findViewById(R.id.review_description)
        fun bind(imageSrc: Int, cType: String, rCourt: String){
            titleImage.setImageResource(imageSrc)
            courtType.text=cType
            reviewCourt.text=rCourt
        }
    }
    companion object{
        fun newInstance(): FilteredHomeFragment {
            val fragment = FilteredHomeFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}