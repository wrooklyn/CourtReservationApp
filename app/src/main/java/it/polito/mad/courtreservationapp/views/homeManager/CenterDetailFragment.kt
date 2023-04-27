package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.SportCenterWithCourtsAndServices
import it.polito.mad.courtreservationapp.views.MainActivity


class CenterDetailFragment : Fragment() {
    lateinit var centerName: String
    lateinit var location: String
    var imageId: Int = 0
    lateinit var description: String

    lateinit var myTab :TabLayout
    lateinit var myViewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = requireArguments().getInt("position", 0)
        imageId = requireArguments().getInt("imageId", 0)
        val sportCenterWithCourtsAndServices = (activity as MainActivity).sportCenters[position]

        centerName = sportCenterWithCourtsAndServices.sportCenter.name
        location = sportCenterWithCourtsAndServices.sportCenter.address
        description = sportCenterWithCourtsAndServices.sportCenter.description
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_center_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        myTab=view.findViewById<TabLayout>(R.id.tab_layout)
        myViewPager=view.findViewById<ViewPager2>(R.id.view_pager)

        view.findViewById<TextView>(R.id.centerTitle).text = centerName
        view.findViewById<TextView>(R.id.textView15).text = location
        view.findViewById<ImageView>(R.id.imageView16).setImageResource(imageId)

        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, description)

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
                Log.v("gabri", tab.position.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        fun newInstance(
            centerWithCourtsAndServices: SportCenterWithCourtsAndServices,
            id: Int,
            position: Int
        ): CenterDetailFragment {
            val fragment = CenterDetailFragment()
            val args = Bundle()
            args.putInt("imageId", id)
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

}