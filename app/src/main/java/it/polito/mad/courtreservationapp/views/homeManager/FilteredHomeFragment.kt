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
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviewsAndUsers
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWIthCourtsAndReviewsAndUsers
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

class FilteredHomeFragment : Fragment() {

//    var position: Int = -1
    lateinit var viewModel: SportCenterViewModel
    lateinit var sportCentersWithCourtsAndReviews: List<SportCenterWIthCourtsAndReviewsAndUsers>
    private var courts: MutableList<CourtWithReviewsAndUsers> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).sportCenterViewModel

        sportCentersWithCourtsAndReviews = viewModel.sportCentersWithCourtsAndReviewsAndUsers
        sportCentersWithCourtsAndReviews.forEach { sportCenter ->
            sportCenter.courtsWithReviewsAndUsers.forEach {court ->
                if(viewModel.sportFilters.contains(court.court.sportName)){
                    courts.add(court)
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
        val adapter = FilteredCourtsAdapter(courts)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class FilteredCourtsAdapter(private val courts: MutableList<CourtWithReviewsAndUsers>): RecyclerView.Adapter<FilteredCourtsViewHolder>() {

        override fun getItemCount()=courts.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilteredCourtsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.reserve_card_item, parent, false)
            return FilteredCourtsViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: FilteredCourtsViewHolder, position: Int) {
            val currentCourt = courts[position]
            val averageRating = currentCourt.reviewsWithUser.map { it.review.rating }.average().run { if (isNaN()) 0.0 else this}
            val ratingTxt = if (currentCourt.reviewsWithUser.size == 1) "review" else "reviews"
            val currentReview = "$averageRating (${currentCourt.reviewsWithUser.size} $ratingTxt)"

            holder.bind(currentCourt.court.image, "${currentCourt.court.sportName} Court", currentReview)

            holder.itemView.findViewById<Button>(R.id.reserveButton).setOnClickListener{

                val createReservationIntent: Intent = Intent(holder.itemView.context, CreateReservationActivity::class.java)
                createReservationIntent.putExtra("sportCenterId",currentCourt.court.sportCenterId)
                createReservationIntent.putExtra("courtId",currentCourt.court.courtId)
                createReservationIntent.putExtra("rating", averageRating)
                createReservationIntent.putExtra("reviews", currentReview)
                (holder.itemView.context as MainActivity).registerForReservationActivityResult.launch(createReservationIntent)
//                holder.itemView.context.startActivity(createReservationIntent)
            }
        }
    }
    class FilteredCourtsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.court_image)
        private val courtType: TextView = itemView.findViewById(R.id.court_type)
        private val reviewCourt: TextView = itemView.findViewById(R.id.review_description)
        fun bind(imageSrc: String?, cType: String, rCourt: String){
            ImageUtils.setImage("courts", imageSrc, titleImage)
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