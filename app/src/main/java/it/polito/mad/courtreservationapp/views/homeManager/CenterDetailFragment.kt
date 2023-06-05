package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWIthCourtsAndReviewsAndUsers
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Review
import it.polito.mad.courtreservationapp.utils.ImageUtils
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.views.MainActivity


class CenterDetailFragment : Fragment() {
    private var sportCenterPosition: Int = -1
    lateinit var centerName: String
    lateinit var location: String


    lateinit var myTab: TabLayout
    lateinit var myViewPager: ViewPager2

    private lateinit var viewModel: SportCenterViewModel
    private lateinit var sportCenterWithCourtsAndServices: SportCenterWithCourtsAndServices
    private lateinit var sportCenterWithCourtsAndReviewsAndUsers: SportCenterWIthCourtsAndReviewsAndUsers
    private var reviews= MutableLiveData<List<Review>>()
    private var type:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        sportCenterPosition = requireArguments().getInt("position", -1)
        type = requireArguments().getInt("type")
        viewModel = (activity as MainActivity).sportCenterViewModel
        if(type == 1){
            sportCenterWithCourtsAndServices =
                viewModel.sportCentersWithCourtsAndServices[sportCenterPosition]
        } else if(type == 2) {
            sportCenterWithCourtsAndServices = viewModel.sportCentersWithCourtsAndServices.first(){center -> center.sportCenter == viewModel.popularSportCenters[sportCenterPosition]}
        }


        centerName = sportCenterWithCourtsAndServices.sportCenter.name
        location = sportCenterWithCourtsAndServices.sportCenter.address
        viewModel.sportCentersWithReviewsAndUsersLiveData.observe(this){
            val newList = mutableListOf<Review>()
            println("number of sportcenters ${it.size}")
            if(type == 1){
                sportCenterWithCourtsAndReviewsAndUsers = it[sportCenterPosition]
            } else if(type == 2){
                sportCenterWithCourtsAndReviewsAndUsers = it.first(){center -> center.sportCenter == viewModel.popularSportCenters[sportCenterPosition]}
            }

            sportCenterWithCourtsAndReviewsAndUsers.courtsWithReviewsAndUsers.forEach() { court ->
                println("   number of courts ${court.court.courtId}")
                court.reviewsWithUser.forEach { r->
                    println("       rating ${r.review.rating}")
                    newList.add(r.review)
                }
            }
            println(newList)
            reviews.postValue(newList)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_center_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myTab = view.findViewById(R.id.tab_layout)
        myViewPager = view.findViewById(R.id.view_pager)

        view.findViewById<TextView>(R.id.centerTitle).text = centerName
        view.findViewById<TextView>(R.id.centerAddress).text = location
        view.findViewById<RatingBar>(R.id.ratingBar).rating = 0.0F
        reviews.observe(this.viewLifecycleOwner){
            val data = it.map { r-> r.rating }.average();
            if(data!=null){
                view.findViewById<RatingBar>(R.id.ratingBar).rating = data.run { if (isNaN()) 0.0F else this.toFloat() }
            }
            val reviewTxt = if (it.size == 1) "review" else "reviews"
            view.findViewById<TextView>(R.id.numRating).text = "(${it.size} $reviewTxt)"
        }

        println("data : $reviews")
        val banner = view.findViewById<ImageView>(R.id.bannerImage)
        val imageSrc = sportCenterWithCourtsAndServices.sportCenter.image
        ImageUtils.setImage("centers", imageSrc, banner)

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, sportCenterPosition, type)

        myViewPager?.adapter = adapter

        myTab.let {
            myViewPager.let { it1 ->
                TabLayoutMediator(
                    it, it1
                ) { tab, position ->
                    tab.text =
                        (myViewPager.adapter as ViewPagerAdapter).mFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.
                    (myViewPager.adapter as ViewPagerAdapter).createFragment(position)
                }.attach()
            }
        }

        myTab.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                myViewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance(position: Int, type: Int): CenterDetailFragment {
            val fragment = CenterDetailFragment()
            val args = Bundle()
            args.putInt("position", position)
            args.putInt("type", type)
            fragment.arguments = args
            return fragment
        }
    }

}