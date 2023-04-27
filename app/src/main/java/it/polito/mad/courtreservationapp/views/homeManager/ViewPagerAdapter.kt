package it.polito.mad.courtreservationapp.views.homeManager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.CourtFragment
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.DescriptionFragment
import it.polito.mad.courtreservationapp.views.homeManager.tabFragments.ReviewsFragment


class ViewPagerAdapter(fragmentActivity: FragmentManager, lifecycle:Lifecycle, description: String) : FragmentStateAdapter(fragmentActivity, lifecycle) {
    val description = description
    val mFragmentNames = arrayOf( //Tabs names array
        "Description",
        "Courts",
        "Reviews"
    )

    override fun getItemCount(): Int {
        return mFragmentNames.size //Number of fragments displayed
    }

    override fun getItemId(position: Int): Long{
        return super.getItemId(position)
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> DescriptionFragment.newInstance(description)
            1 -> CourtFragment()
            2 -> ReviewsFragment()
            else -> throw AssertionError()
        }
    }
}