package it.polito.mad.courtreservationapp.views.reservationManager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.views.MainActivity

class BrowseReservationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_reservations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = (activity as MainActivity).userViewModel.user
        val userReservLocations = (activity as MainActivity).userReservationsLocations.sortedByDescending { res -> res.reservation.reservationDate }
        val userReservServices = (activity as MainActivity).userReservationsServices.sortedByDescending { res -> res.reservation.reservationDate }
        val tabReserv = view.findViewById<TabLayout>(R.id.reserv_tab_layout)
        val pager = view.findViewById<ViewPager2>(R.id.reserv_view_pager)
        val adapter = ReservationsAdapter(childFragmentManager, lifecycle)
        pager.adapter = adapter

        tabReserv.let {
            pager.let { it1 ->
                TabLayoutMediator(
                    it, it1
                ) { tab, position ->
                    tab.text =
                        (pager.adapter as ReservationsAdapter).reservFragmentNames[position] //Sets tabs names as mentioned in ViewPagerAdapter fragmentNames array, this can be implemented in many different ways.
                    (pager.adapter as ReservationsAdapter).createFragment(position)
                }.attach()
            }
        }

        tabReserv.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}