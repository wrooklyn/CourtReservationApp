package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import it.polito.mad.courtreservationapp.R

class DescriptionFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_description, container, false)
    }

    companion object {

        var POSITION_ARG = "position_arg"
        @JvmStatic
        fun newInstance(position:Int) = DescriptionFragment().apply {
            arguments=Bundle().apply {
                putInt(POSITION_ARG, position)
            }
        }

    }
}