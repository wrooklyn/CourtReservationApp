package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.polito.mad.courtreservationapp.R

class ReviewsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviews, container, false)
    }

    companion object {

        var POSITION_ARG = "position_arg"
        @JvmStatic
        fun newInstance(position:Int) = ReviewsFragment().apply {
            arguments=Bundle().apply {
                putInt(POSITION_ARG, position)
            }
        }

    }
}