package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndReviews
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.models.Review
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
    private lateinit var sportCenterWithCourtsAndReviews: SportCenterWithCourtsAndReviews
    private var reviews: MutableList<Review> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        sportCenterPosition = requireArguments().getInt("position", -1)
        viewModel = (activity as MainActivity).sportCenterViewModel
        sportCenterWithCourtsAndServices =
            viewModel.sportCentersWithCourtsAndServices[sportCenterPosition]
        sportCenterWithCourtsAndReviews =
            viewModel.sportCentersWithCourtsAndReviews[sportCenterPosition]
        centerName = sportCenterWithCourtsAndServices.sportCenter.name
        location = sportCenterWithCourtsAndServices.sportCenter.address

        sportCenterWithCourtsAndReviews.courtsWithReviews.forEach() { court ->
            court.reviews.forEach {
                reviews.add(it)
            }
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
        view.findViewById<RatingBar>(R.id.ratingBar).rating =
            reviews.map { it.rating }.average().run { if (isNaN()) 0.0F else this.toFloat() }
        val reviewTxt = if (reviews.size == 1) "review" else "reviews"
        view.findViewById<TextView>(R.id.numRating).text = "(${reviews.size} $reviewTxt)"
        val imageRes: Int =
            viewModel.sportCenterImages[sportCenterWithCourtsAndServices.sportCenter.centerId]
                ?: R.drawable.gesu
        view.findViewById<ImageView>(R.id.bannerImage).setImageResource(imageRes)

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, sportCenterPosition)

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
        fun newInstance(
            position: Int
        ): CenterDetailFragment {
            val fragment = CenterDetailFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

}