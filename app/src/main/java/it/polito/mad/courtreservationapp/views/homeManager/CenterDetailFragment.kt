package it.polito.mad.courtreservationapp.views.homeManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.DescriptionFragment


class CenterDetailFragment : Fragment() {

    private var myTab = view?.findViewById<TabLayout>(R.id.tab_layout)
    private var myViewPager = view?.findViewById<ViewPager2>(R.id.view_pager)
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
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        myViewPager?.adapter = adapter

        myTab?.let {
            myViewPager?.let { it1 ->
                TabLayoutMediator(
                    it, it1
                ) { tab, position ->
                    tab.text =
                        (myViewPager!!.adapter as ViewPagerAdapter).mFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.
                    (myViewPager!!.adapter as ViewPagerAdapter).createFragment(position)
                }.attach()
            }
        }

        myTab?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                myViewPager?.currentItem = tab.position

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        super.onViewCreated(view, savedInstanceState)

    }

}