package it.polito.mad.courtreservationapp.views.homeManager

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.view_model.SportCenterViewModel
import it.polito.mad.courtreservationapp.view_model.SportMasteryViewModel
import it.polito.mad.courtreservationapp.views.MainActivity


class HomeFragment : Fragment() {

    private lateinit var viewModel: SportCenterViewModel
    private lateinit var vm: SportMasteryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as MainActivity).sportCenterViewModel
        viewModel.sportCentersLiveData.observe(this){
            viewModel.loadSportCenters(it)
            sportInitialize()
        }
        viewModel.sportCentersWithReviewsAndUsersLiveData.observe(this){
            Log.i("Test", "$it")
            viewModel.loadReviews(it)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val childFragment: Fragment = UnfilteredHomeFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.child_fragment_container, childFragment).commit()
    }

    private fun sportInitialize(){

        val recyclerView: RecyclerView? = view?.findViewById(R.id.sports_recycler)
        val adapter = SportsAdapter(viewModel.sportIconsId, viewModel.allSports, activity as MainActivity, this)
        recyclerView?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = adapter
        adapter.setOnItemClickListener(object : SportsAdapter.OnItemClickListener{
            override fun onItemClick(position: Int) {
            }

        })
    }

    //Sports ScrollView
    class SportsAdapter(private val imagesList: Map<String, Int>, private val namesList: List<String>, val activity: MainActivity, val fragment: HomeFragment): RecyclerView.Adapter<SportsViewHolder>() {

        private lateinit var sListener: OnItemClickListener
        interface OnItemClickListener{
            fun onItemClick(position: Int)
        }

        fun setOnItemClickListener(listener: OnItemClickListener){
            sListener=listener
        }
        override fun getItemCount()=namesList.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportsViewHolder {
            val itemView=LayoutInflater.from(parent.context).inflate(R.layout.sports_list_item, parent, false)
            return SportsViewHolder(itemView, sListener, activity, fragment)
        }
        override fun onBindViewHolder(holder: SportsViewHolder, position: Int) {
            val currentName = namesList[position]
            val currentImage = imagesList[currentName] ?: R.drawable.gesu
            holder.bind(currentImage, currentName)
        }
    }
    class SportsViewHolder(itemView: View, listener: SportsAdapter.OnItemClickListener, val activity: MainActivity, val fragment: HomeFragment):RecyclerView.ViewHolder(itemView){
        private val titleImage: ImageView = itemView.findViewById(R.id.sport_image1)
        private val sportName: TextView = itemView.findViewById(R.id.sport_name)
        private val a = activity
        private var view = itemView.findViewById<CardView>(R.id.sport_card_container)
        fun bind(imageSrc: Int, sportName: String){
            titleImage.setImageResource(imageSrc)
            this.sportName.text=sportName

            val blue = ContextCompat.getColor(a, R.color.deep_blue)
            val white = Color.parseColor("#FFFFFF")
            val black = Color.parseColor("#000000")

            if(a.sportCenterViewModel.sportFilters.contains(sportName)){
                view.setCardBackgroundColor(ColorStateList.valueOf(blue))
                this.sportName.setTextColor(white)
            } else {
                view.setCardBackgroundColor(ColorStateList.valueOf(white))
                this.sportName.setTextColor(black)
            }

            view.setOnClickListener {
                if(a.sportCenterViewModel.sportFilters.contains(sportName)){
                    a.sportCenterViewModel.sportFilters.remove(sportName)
                    view.setCardBackgroundColor(ColorStateList.valueOf(white))
                    this.sportName.setTextColor(black)
                } else {
                    a.sportCenterViewModel.sportFilters.add(sportName)
                    view.setCardBackgroundColor(ColorStateList.valueOf(blue))
                    this.sportName.setTextColor(white)
                }
                if(a.sportCenterViewModel.sportFilters.size>0){
                    val childFragment: Fragment = FilteredHomeFragment()
                    val transaction: FragmentTransaction = fragment.childFragmentManager.beginTransaction()
                    transaction.replace(R.id.child_fragment_container, childFragment).commit()
                } else {
                    val childFragment: Fragment = UnfilteredHomeFragment()
                    val transaction: FragmentTransaction = fragment.childFragmentManager.beginTransaction()
                    transaction.replace(R.id.child_fragment_container, childFragment).commit()
                }


            }

        }
        init{
            itemView.setOnClickListener {
                listener.onItemClick(bindingAdapterPosition)
            }
        }
    }

}