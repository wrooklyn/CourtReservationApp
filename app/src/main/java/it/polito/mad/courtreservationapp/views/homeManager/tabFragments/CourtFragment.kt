package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviewsAndUsers
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity


class CourtFragment : Fragment() {

    var position: Int = -1
    lateinit var viewModel: SportCenterViewModel
    lateinit var sportCenterWithCourtsAndServices: SportCenterWithCourtsAndServices
//    lateinit var sportCenterWithCourtsAndReviews: SportCenterWithCourtsAndReviews
    private var courts: MutableList<Court> = mutableListOf()
    private lateinit var courtsWithReviewsAndUsers: List<CourtWithReviewsAndUsers>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        position = requireArguments().getInt("position", -1)
        sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices[position]
        courtsWithReviewsAndUsers = viewModel.sportCentersWithCourtsAndReviewsAndUsers[position].courtsWithReviewsAndUsers

        sportCenterWithCourtsAndServices.courtsWithServices.forEach{courtWithServices ->
            if(!courts.contains(courtWithServices.court)){
                courts.add(courtWithServices.court)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_court, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceInitialize()
    }

    private fun serviceInitialize(){

        val recyclerView: RecyclerView? = view?.findViewById(R.id.service_court_recycler)
        val adapter = CourtDescriptionAdapter(courtsWithReviewsAndUsers)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class CourtDescriptionAdapter(private val courts: List<CourtWithReviewsAndUsers>): RecyclerView.Adapter<CourtDescriptionViewHolder>() {

        override fun getItemCount()=courts.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtDescriptionViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.reserve_card_item, parent, false)
            return CourtDescriptionViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: CourtDescriptionViewHolder, position: Int) {

            val currentCourt = courts[position]

            val currentImage = currentCourt.court.image
            val averageRating = currentCourt.reviewsWithUser.map { it.review.rating }.average().run { if (isNaN()) 0.0 else this}
            val ratingTxt = if (currentCourt.reviewsWithUser.size == 1) "review" else "reviews"
            val currentReview = "$averageRating (${currentCourt.reviewsWithUser.size} $ratingTxt)"

            Log.i("CourtFragment", "$currentCourt")

            holder.bind(currentImage, "${currentCourt.court.sportName} Court", currentReview)
            holder.itemView.findViewById<Button>(R.id.reserveButton).setOnClickListener{
                Log.i("CourtFragment", "ReserveButtonPressed for: SC:${currentCourt.court.sportCenterId}, C: ${currentCourt.court} ")
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
    class CourtDescriptionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
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
        fun newInstance(position: Int): CourtFragment {
            val fragment = CourtFragment()
            val args = Bundle()
            args.putInt("position", position)

            fragment.arguments = args
            return fragment
        }
    }
}