package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.*
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity

class ReviewsFragment : Fragment() {

    var position: Int = -1
    private var type:Int = 0
    lateinit var viewModel: SportCenterViewModel
    private lateinit var sportCenterWithCourtsAndServices: SportCenterWithCourtsAndServices
    lateinit var sportCenterWithCourtsAndReviews: SportCenterWIthCourtsAndReviewsAndUsers
    private var reviews: MutableList<Pair<ReviewWithUser, Court>> = mutableListOf()
    private lateinit var courtsWithReviews: List<CourtWithReviews>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("ReviewsFragment", "OnCreate")
        viewModel = (activity as MainActivity).sportCenterViewModel
        position = requireArguments().getInt("position", -1)
        type = requireArguments().getInt("type")
//        sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices[position]
        sportCenterWithCourtsAndReviews = viewModel.sportCentersWithCourtsAndReviewsAndUsers[position]
        if(type == 2) {
            sportCenterWithCourtsAndReviews = viewModel.sportCentersWithCourtsAndReviewsAndUsers.first(){center -> center.sportCenter == viewModel.popularSportCenters[position]}
        }
//        courtsWithReviews = viewModel.sportCentersWithCourtsAndReviewsAndUsers[position].courtsWithReviews
//        users = viewModelUser.

        sportCenterWithCourtsAndReviews.courtsWithReviewsAndUsers.forEach{courtWithReviewsAndUsers ->
            courtWithReviewsAndUsers.reviewsWithUser.forEach{review ->
                reviews.add(Pair(review, courtWithReviewsAndUsers.court))
            }
        }
        Log.i("ReviewsFragment", "reviews: $reviews")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.i("ReviewsFragment", "OnCreateView")
        return inflater.inflate(R.layout.fragment_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("ReviewsFragment", "OnViewCreated")
        serviceInitialize()
    }

    private fun serviceInitialize() {
        val recyclerView: RecyclerView? = view?.findViewById(R.id.reviewRecycler)
        val adapter = ReviewAdapter(reviews)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class ReviewAdapter(private val reviews: MutableList<Pair<ReviewWithUser, Court>>): RecyclerView.Adapter<ReviewViewHolder>() {

        override fun getItemCount()=reviews.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
            return ReviewViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            Log.i("ReviewAdapter", "Reviews to bind ${reviews.size}: $reviews")
            holder.bind(reviews[position])
            Log.i("ReviewAdapter", "-------------------------")
        }
    }

    class ReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val username: TextView = itemView.findViewById(R.id.userReviewTV)
        private val reviewText: TextView = itemView.findViewById(R.id.reviewTextTV)
        private val rating: RatingBar = itemView.findViewById(R.id.ratingBar3)
        private val courtTv: TextView = itemView.findViewById(R.id.courtNameTV)
        fun bind(pair: Pair<ReviewWithUser, Court>){
            val reviewWithUser = pair.first
            val court = pair.second
            Log.i("ReviewViewHolder", "Setting: $reviewWithUser")
            username.text=reviewWithUser.user.username
            reviewText.text=reviewWithUser.review.text
            rating.rating=reviewWithUser.review.rating.toFloat()
            courtTv.text = "${court.sportName} Court"
        }
    }

    companion object {
        fun newInstance(position: Int, type: Int): ReviewsFragment {
            val fragment = ReviewsFragment()
            val args = Bundle()
            args.putInt("position", position)
            args.putInt("type", type)
            fragment.arguments = args
            return fragment
        }

    }
}