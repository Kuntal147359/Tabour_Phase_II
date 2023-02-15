package com.tabour.hospitality.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.SeatingOptAdapter
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseseatingQueueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseseatingQueueFragment : Fragment(), ItemClickListener {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_back: LinearLayout
    lateinit var tv_queue_position: TextView
    lateinit var tv_eshtimated_time: TextView
    lateinit var btnNext: AppCompatTextView
    lateinit var rvChooseSeating: RecyclerView
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var seatingOptAdapter: SeatingOptAdapter
    var restro_id = ""
    var seating_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chooseseating_queue, container, false)
        restro_id = arguments?.getString("restro_id").toString()
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        initViews()

        eshtblshmntViewModel.getChooseSeatnqueue(
            requireContext(),
            restro_id,
            AppPreferenceStorage.getCount()
        )

        EshtblshmntViewModel.chooseSeatingQueue.observe(viewLifecycleOwner)
        {
            initSeatingAreasRecyclerView()
            tv_queue_position.text = it.postion_in_line
            tv_eshtimated_time.text = String.format(getString(R.string.estimated_time),"${it.waiting_time} minutes")
        }

        return root
    }

    fun initViews() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        lyt_back = root.findViewById(R.id.lyt_back)
        rvChooseSeating = root.findViewById(R.id.rvChooseSeating)
        tv_queue_position = root.findViewById(R.id.tv_queue_position)
        tv_eshtimated_time = root.findViewById(R.id.tv_eshtimated_time)
        btnNext = root.findViewById(R.id.btnNext)

        // system's back events
        root.isFocusableInTouchMode = true
        root.requestFocus()
        root.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            Log.i("Keycheck", "keyCode: $keyCode")
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                onBackPressed()
                return@OnKeyListener true
            }
            false
        })

        lyt_back.setOnClickListener {
            onBackPressed()
        }

        btnNext.setOnClickListener {

            if(seating_id.equals("", ignoreCase = true))
            {
                Toast.makeText(requireContext(),"Please choose seating...", Toast.LENGTH_LONG).show()
            }
            else
            {
                val bundle = Bundle()
                bundle.putString("mode","seating")
                bundle.putString("category", "1")
                bundle.putString("restro_id",restro_id)
                bundle.putString("dinning_area_id",seating_id)
                bundle.putString("user_queue_id","")
                val viewQueueBooking = ViewQueueBooking()
                viewQueueBooking.arguments = bundle
                AppCommonUtils.hideKeyboard(requireActivity())
                AppCommonUtils.loadFragment(requireActivity(), viewQueueBooking)
            }
        }

    }

    fun initSeatingAreasRecyclerView() {
        seatingOptAdapter = SeatingOptAdapter(
            requireContext(),
            EshtblshmntViewModel.chooseSeatingQueue.value!!.dining_area
        )

        rvChooseSeating.smoothScrollToPosition(0)
        rvChooseSeating.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        rvChooseSeating.setAdapter(seatingOptAdapter)
        seatingOptAdapter.setClickListener(this)
    }

    override fun onClick(view: View?, module: String, position: Int) {
        when (module) {
            "seating" -> {
                seating_id = EshtblshmntViewModel.chooseSeatingQueue.value!!.dining_area.get(position).id
                Log.d("checkdata", seating_id)
            }
        }
    }

    fun onBackPressed() {
        val maneger: FragmentManager = requireActivity().supportFragmentManager
        maneger.beginTransaction()
            .remove(this)
            .commit()
        maneger.popBackStack()
    }

}