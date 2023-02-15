package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.*
import com.tabour.hospitality.models.EshtablishmentDetails
import com.tabour.hospitality.models.ResvtnChsDt
import com.tabour.hospitality.models.TimeSlot
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.DateToStringConversion
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.LoginViewModel
import com.tabour.hospitality.viewmodels.SharedViewModel
import kotlinx.android.synthetic.main.fragment_resdetails.*
import kotlinx.android.synthetic.main.fragment_reservation.*
import kotlinx.android.synthetic.main.fragment_reservation.view.*
import kotlinx.android.synthetic.main.fragment_search_availability.*
import kotlinx.android.synthetic.main.lyt_cntrycodes.*
import kotlinx.android.synthetic.main.lyt_months.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [ReservationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReservationFragment : Fragment(), ItemClickListener {
    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var btnNext: AppCompatTextView
    lateinit var lyt_chooseMonth: LinearLayout
    lateinit var tv_chooseMonth: TextView
    lateinit var tv_no_slots_available: TextView
    lateinit var tmslt_progressbar: ProgressBar
    lateinit var rvChooseDate: RecyclerView
    lateinit var rvChooseTime: RecyclerView
    lateinit var rvChooseSeating: RecyclerView
    lateinit var lyt_back: LinearLayout
    lateinit var adapter: MonthsAdapter
    lateinit var resvtnChsDtAdapter: ResvtnChsDtAdapter
    lateinit var timeSlotByDateAdapter: TimeSlotByDateAdapter
    lateinit var seatingOptAdapter: SeatingOptAdapter
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var daysArr: ArrayList<ResvtnChsDt>
    var restro_id = ""
    var restro_name = ""
    var restro_address = ""
    var selectedDate = ""
    var selectedTime = ""
    var selectedTimeSlot = ""
    var selectedDinningArea = ""
    var currMonth = ""
    var sldt = ""
    var dsplyDate = ""
    var dsplyDay = ""
    var selectedPos = -1
    var selectedtmslPos = -1
    var defaultSelection = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_reservation, container, false)

        //Aurguments from previous page
        restro_id = arguments?.getString("restro_id").toString()
        restro_name = arguments?.getString("restro_name").toString()
        restro_address = arguments?.getString("restro_address").toString()
        sldt = arguments?.getString("sldt").toString()
        selectedDate = arguments?.getString("selectedDate").toString()
        selectedTime = arguments?.getString("selectedTime").toString()

        currMonth = "${DateToStringConversion.getCurrentMonth().first} ${DateToStringConversion.getCurrentMonth().second}"

        Log.d("selectedDate","2. ${selectedDate} ${selectedTime}")

        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        initViews()

        // Choose date
        daysArr = DateToStringConversion.getAllWeekDaysForResvtn()
//        selectedDate = daysArr.get(1).display_date

        for(i in 0 until daysArr.size)
        {
            val comp_a = "${daysArr.get(i).display_day}, ${daysArr.get(i).display_date} ${daysArr.get(i).month}"
            if(selectedDate.contains(comp_a))
            {
                selectedPos = i
            }
        }

        resvtnChsDtAdapter = ResvtnChsDtAdapter(
            requireContext(),
            DateToStringConversion.getAllWeekDaysForResvtn(),
            selectedDate
        )

        rvChooseDate.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        rvChooseDate.smoothScrollToPosition(selectedPos + 2)

        rvChooseDate.setAdapter(resvtnChsDtAdapter)
        resvtnChsDtAdapter.setClickListener(this)
//        eshtblshmntViewModel.getRestroSlotsByDate(requireContext(), restro_id, selectedDate)

        // Choose time
        EshtblshmntViewModel.timeSlotsData.observe(viewLifecycleOwner) {

            if(!selectedDate.equals(""))
            {
                if(it.size > 0)
                {
                    tv_no_slots_available.visibility = View.GONE
                    inittimeslotRecyclerview(it)
                }
                else
                {
                    tv_no_slots_available.visibility = View.VISIBLE
                    rvChooseTime.visibility = View.GONE
                }
            }
        }

        // Choose seating
        EshtblshmntViewModel.seatingoptListData.observe(viewLifecycleOwner) {
            if(!selectedDate.equals(""))
            {
                Log.d("checkdata","RecyclerView initiated... at ${selectedDate}")
                initSeatingAreasRecyclerView()
            }
        }

        EshtblshmntViewModel.restroslotsProgressbar.observe(viewLifecycleOwner){
            if(it)
            {
//                tmslt_progressbar.visibility = View.VISIBLE
                rvChooseTime.visibility = View.GONE
            }
            else
            {
//                tmslt_progressbar.visibility = View.GONE
                rvChooseTime.visibility = View.VISIBLE
            }
        }

        return root
    }

    fun initViews() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        lyt_back = root.findViewById(R.id.lyt_back)
        lyt_chooseMonth = root.findViewById(R.id.lyt_chooseMonth)
        tv_chooseMonth = root.findViewById(R.id.tv_chooseMonth)
        tv_no_slots_available = root.findViewById(R.id.tv_no_slots_available)
        rvChooseDate = root.findViewById(R.id.rvChooseDate)
        rvChooseTime = root.findViewById(R.id.rvChooseTime)
        rvChooseSeating = root.findViewById(R.id.rvChooseSeating)
        tmslt_progressbar = root.findViewById(R.id.tmslt_progressbar)
        tmslt_progressbar.visibility = View.GONE

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

//        tv_chooseMonth.text = currMonth
        tv_chooseMonth.text = sldt
        lyt_chooseMonth.setOnClickListener {
            showMonthsDialog()
        }

        lyt_back.setOnClickListener {
            onBackPressed()
        }

        btnNext = root.findViewById(R.id.btnNext)
        btnNext.setOnClickListener {
            EshtblshmntViewModel.booking_date.value = selectedDate
            EshtblshmntViewModel.time_slot.value = selectedTimeSlot
            EshtblshmntViewModel.dining_area.value = selectedDinningArea
            EshtblshmntViewModel.restro_id.value = restro_id
            EshtblshmntViewModel.restro_name.value = restro_name
            EshtblshmntViewModel.restro_address.value = restro_address
            EshtblshmntViewModel.no_guest.value = AppPreferenceStorage.getCount()
            EshtblshmntViewModel.bookingdate_time.value = "${DateToStringConversion.converteddate(selectedDate)}, ${selectedTimeSlot}"
            EshtblshmntViewModel.booking_time_seating.value = "${DateToStringConversion.converteddate(selectedDate)}, ${selectedTimeSlot} • ${selectedDinningArea}"

            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(),OccasionFormFragment())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun inittimeslotRecyclerview(it: ArrayList<TimeSlot>) {
        Log.d("checkslctn","Time slot called...")

        rvChooseTime.visibility = View.VISIBLE

        if(defaultSelection == 0)
        {
//            Log.d("checktmslcalled","Time slot called...")
            for(i in 0 until it.size)
            {
                val comp_a = "${it.get(i).dsply_time} ${it.get(i).dsply_format}"
                if(selectedTime.equals(comp_a,ignoreCase = true))
                {
                    selectedtmslPos = i
                }
            }
            defaultSelection = 1
        }

        Log.d("checkdata","Selected timeslot is: ${selectedTime}")
        timeSlotByDateAdapter = TimeSlotByDateAdapter(
            requireContext(),
            it,
            selectedTime
        )

        rvChooseTime.setLayoutManager(
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )

        Log.d("checkslctn","${selectedtmslPos}, Time slot is ${selectedTime}")

//        rvChooseTime.smoothScrollToPosition(selectedtmslPos + 1)
        rvChooseTime.setAdapter(timeSlotByDateAdapter)
        rvChooseTime.smoothScrollToPosition(selectedtmslPos + 1)
        timeSlotByDateAdapter.setClickListener(this)
        timeSlotByDateAdapter.notifyDataSetChanged()

    }

    fun initSeatingAreasRecyclerView() {

        rvChooseSeating.visibility = View.VISIBLE

        seatingOptAdapter = SeatingOptAdapter(
            requireContext(),
            EshtblshmntViewModel.seatingoptListData.value!!
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

    fun enableBtnNext()
    {
        if (!selectedDate.equals("", ignoreCase = true) &&
            !selectedTimeSlot.equals(
                "",
                ignoreCase = true
            ) && !selectedDinningArea.equals("", ignoreCase = true)
        )
        {
            btnNext.isEnabled = true
            btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorAccent))
            Log.d("checkenable",btnNext.isEnabled.toString())
        }
        else
        {
            btnNext.isEnabled = false
            btnNext.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.faintsandybrown))
            Log.d("checkenable",btnNext.isEnabled.toString())
        }
    }

    override fun onClick(view: View?, module: String, position: Int) {
        when (module) {
            "choosedefaultdate" -> {
                dsplyDate = daysArr.get(position).display_date
                dsplyDay = daysArr.get(position).display_day
                selectedDate = daysArr.get(position).param_date
                enableBtnNext()
                eshtblshmntViewModel.getRestroSlotsByDate(requireContext(), restro_id, selectedDate)
                Log.d("checkdata", selectedDate)
            }
            "choosedate" -> {
                selectedTime = ""
                selectedtmslPos = -1
                selectedTimeSlot = ""
                selectedDinningArea = ""
                rvChooseSeating.visibility = View.GONE
                dsplyDate = daysArr.get(position).display_date
                dsplyDay = daysArr.get(position).display_day
                selectedDate = daysArr.get(position).param_date
                enableBtnNext()
                eshtblshmntViewModel.getRestroSlotsByDate(requireContext(), restro_id, selectedDate)
                Log.d("checkdata", selectedDate)
            }
            "timeslot" -> {
                val timeslot = EshtblshmntViewModel.timeSlotsData.value!!.get(position)
                selectedTimeSlot = timeslot.slot
                enableBtnNext()
                eshtblshmntViewModel.getRestroDinningArea(requireContext(), restro_id, selectedDate, selectedTimeSlot)
                Log.d("checkdata", selectedTimeSlot)
            }
            "seating" -> {
                val seating = EshtblshmntViewModel.seatingoptListData.value!!.get(position)
                selectedDinningArea = seating.seating_option
                EshtblshmntViewModel.dining_area_id.value = seating.id
                enableBtnNext()
                Log.d("checkdata", seating.seating_option)
            }
        }
    }

    fun scrollToPosition(position: Int) {
        rvChooseSeating.scrollToPosition(position)
    }

    fun showMonthsDialog() {
        // Initialize dialog
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.lyt_months)

        // set transparent background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val months_list_view: ListView = dialog.findViewById(R.id.months_list_view)

        adapter = MonthsAdapter(
            requireContext(),
            R.layout.lyt_month_row,
            DateToStringConversion.getNextMonths(5)
        )

        months_list_view.adapter = adapter

        months_list_view.setOnItemClickListener { parent, view, position, id ->

            val month_name = view.findViewById<TextView>(R.id.month_name)
            tv_chooseMonth.setText(
                month_name.text.toString()
            )

            if(month_name.text.toString().equals(currMonth, ignoreCase = true))
            {
                daysArr = DateToStringConversion.getAllWeekDaysForResvtn()
            }
            else
            {
                daysArr = DateToStringConversion.getAllDaysInMonth(month_name.text.toString())
            }

            resvtnChsDtAdapter = ResvtnChsDtAdapter(
                requireContext(),
                daysArr,
                ""
            )

            rvChooseDate.setLayoutManager(
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )

            rvChooseDate.smoothScrollToPosition(0)

            rvChooseDate.setAdapter(resvtnChsDtAdapter)
            resvtnChsDtAdapter.setClickListener(this)

            selectedDate = ""
            selectedTimeSlot = ""
            selectedDinningArea = ""
            enableBtnNext()
            rvChooseTime.visibility = View.GONE
            rvChooseSeating.visibility = View.GONE

            dialog.dismiss()
        }

        // show dialog
        dialog.show()

    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        val maneger: FragmentManager = requireActivity().supportFragmentManager
        maneger.beginTransaction()
            .remove(this)
            .commit()
        maneger.popBackStack()
    }

}