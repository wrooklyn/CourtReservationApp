package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.CourtWithReviews
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReviews
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Court
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.models.User
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.view_model.UserViewModel
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.reservationManager.CreateReservationActivity

class ReviewsFragment : Fragment() {

    var position: Int = -1
    lateinit var viewModel: SportCenterViewModel
    private lateinit var sportCenterWithCourtsAndServices: SportCenterWithCourtsAndServices
    lateinit var sportCenterWithCourtsAndReviews: SportCenterWithCourtsAndReviews
    private var reviews: MutableList<Review> = mutableListOf()
    private lateinit var courtsWithReviews: List<CourtWithReviews>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as MainActivity).sportCenterViewModel
        position = requireArguments().getInt("position", -1)
        sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices[position]
        sportCenterWithCourtsAndReviews = viewModel.sportCentersWithCourtsAndReviews[position]
        courtsWithReviews = viewModel.sportCentersWithCourtsAndReviews[position].courtsWithReviews
//        users = viewModelUser.

        sportCenterWithCourtsAndReviews.courtsWithReviews.forEach(){courtWithReviews ->
            reviews.addAll(courtWithReviews.reviews)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceInitialize()
    }

    private fun serviceInitialize() {
        val recyclerView: RecyclerView? = view?.findViewById(R.id.reviewRecycler)
        val adapter =
            ReviewsFragment.ReviewAdapter(reviews)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class ReviewAdapter(private val reviews: List<Review>): RecyclerView.Adapter<ReviewViewHolder>() {

        override fun getItemCount()=reviews.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.review_item, parent, false)
            return ReviewViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            reviews.forEach(){review ->
                holder.bind(review.reviewUserId.toString(), review.text.toString(), review.rating.toFloat())
            }
        }
    }

    class ReviewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val username: TextView = itemView.findViewById(R.id.userReviewTV)
        private val reviewText: TextView = itemView.findViewById(R.id.reviewTextTV)
        private val rating: RatingBar = itemView.findViewById(R.id.ratingBar3)
        fun bind(usernameData:String, reviewData:String, ratingValue:Float){
            username.text=usernameData
            reviewText.text=reviewData
            rating.rating=ratingValue
        }
    }

    companion object {
        fun newInstance(position: Int): ReviewsFragment {
            val fragment = ReviewsFragment()
            val args = Bundle()
            args.putInt("position", position)

            fragment.arguments = args
            return fragment
        }

    }
}