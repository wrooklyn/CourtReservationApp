package it.polito.mad.courtreservationapp.views.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.view_model.FriendListViewModel
import it.polito.mad.courtreservationapp.view_model.LeaveRatingViewModel
import it.polito.mad.courtreservationapp.views.AchievementSection
import it.polito.mad.courtreservationapp.views.MainActivity

class ShowSocialPageFragment : Fragment() {
    private lateinit var viewModel : FriendListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).friendListViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.social_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val composeView = view.findViewById<ComposeView>(R.id.composeContainer) //TODO check
        composeView.setContent {
            FriendList(activity as MainActivity)
        }
    }
}