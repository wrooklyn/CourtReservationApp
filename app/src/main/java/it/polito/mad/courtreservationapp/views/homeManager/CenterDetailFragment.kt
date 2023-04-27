package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithServices
import it.polito.mad.courtreservationapp.db.relationships.ReservationWithSportCenter
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.DescriptionFragment
import it.polito.mad.courtreservationapp.views.reservationManager.ReservationDetailsFragment


class CenterDetailFragment : Fragment() {

    lateinit var myTab :TabLayout
    lateinit var myViewPager : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

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
        fun newInstance(centerName:String): CenterDetailFragment {
            val fragment = CenterDetailFragment()
            val args = Bundle()
            args.putString("centerName", centerName)
            fragment.arguments = args
            return fragment
        }
    }

}