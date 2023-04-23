package it.polito.mad.courtreservationapp.views.homeManager.tabFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R

class CourtFragment : Fragment() {

    private lateinit var imageId: Array<Int>
    private lateinit var courtType: Array<String>
    private lateinit var reviewCourt: Array<String>
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        serviceInitialize()
    }

    private fun serviceInitialize(){
        imageId = arrayOf(
            R.drawable.tennis_court,
            R.drawable.basket_court,
            R.drawable.football_court,
            R.drawable.volley_court

        )
        courtType= arrayOf(
            "Tennis Court",
            "Basketball Court",
            "Football Field",
            "Volleyball Court"
            )
        reviewCourt=arrayOf(
            "5.0 (124 review)",
            "5.0 (124 review)",
            "5.0 (124 review)",
            "5.0 (124 review)",
            )

        val recyclerView: RecyclerView? = view?.findViewById(R.id.service_court_recycler)
        val adapter = CourtDescriptionAdapter(imageId, courtType, reviewCourt)

        val llm : LinearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = llm
        recyclerView?.isNestedScrollingEnabled=false
        recyclerView?.adapter = adapter
    }

    class CourtDescriptionAdapter(private val imagesList: Array<Int>, private val courtsName: Array<String>, private val cReview: Array<String> ): RecyclerView.Adapter<CourtDescriptionViewHolder>() {

        override fun getItemCount()=imagesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourtDescriptionViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.reserve_card_item, parent, false)
            return CourtDescriptionViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: CourtDescriptionViewHolder, position: Int) {
            val currentImage = imagesList[position]
            val currentType = courtsName[position]
            val currentReview = cReview[position]
            holder.bind(currentImage, currentType, currentReview)
        }
    }
    class CourtDescriptionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.court_image)
        private val courtType: TextView = itemView.findViewById(R.id.court_type)
        private val reviewCourt: TextView = itemView.findViewById(R.id.review_description)
        fun bind(imageSrc: Int, cType: String, rCourt: String){
            titleImage.setImageResource(imageSrc)
            courtType.text=cType
            reviewCourt.text=rCourt
        }
    }
}