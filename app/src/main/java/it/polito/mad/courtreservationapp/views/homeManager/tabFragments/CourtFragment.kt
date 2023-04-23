package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.polito.mad.courtreservationapp.R

class CourtFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_court, container, false)
    }

    companion object {

        var POSITION_ARG = "position_arg"
        @JvmStatic
        fun newInstance(position:Int) = CourtFragment().apply {
            arguments=Bundle().apply {
                putInt(POSITION_ARG, position)
            }
        }

    }
}