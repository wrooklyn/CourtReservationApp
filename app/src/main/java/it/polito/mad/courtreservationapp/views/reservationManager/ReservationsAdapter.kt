package it.polito.mad.courtreservationapp.views.reservationManager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.polito.mad.courtreservationapp.views.reservationManager.ReservListFragment


class ReservationsAdapter(fragmentActivity: FragmentManager, lifecycle:Lifecycle) : FragmentStateAdapter(fragmentActivity, lifecycle) {

    val reservFragmentNames = arrayOf( //Tabs names array
        "Upcoming",
        "Past"
    )

    override fun getItemCount(): Int {
        return reservFragmentNames.size //Number of fragments displayed
    }

    override fun getItemId(position: Int): Long{
        return super.getItemId(position)
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> ReservListFragment.newInstance(true)
            1 -> ReservListFragment.newInstance(false)
            else -> throw AssertionError()
        }
    }
}