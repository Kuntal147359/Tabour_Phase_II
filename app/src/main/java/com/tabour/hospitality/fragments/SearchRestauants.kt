package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.EshtablishmentAdapter
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.ItemClickListener
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.HomeViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search_restauants.*
import kotlinx.android.synthetic.main.include_drawer_content.*

/**
 * A simple [Fragment] subclass.
 * Use the [SearchRestauants.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchRestauants : Fragment(), ItemClickListener {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var menu: Menu
    lateinit var menuItem: MenuItem
    lateinit var txt_nearBy: TextView
    lateinit var tv_srchbykeyword: EditText
    lateinit var eshtblsh_progressbar: ProgressBar
    lateinit var lyt_back: LinearLayout
    lateinit var search_restaurants_recy: RecyclerView
    lateinit var eshtablishmentAdapter: EshtablishmentAdapter
    lateinit var searchViewModel: SearchViewModel
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    var restro_type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_search_restauants, container, false)
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        restro_type = arguments?.getString("restro_type").toString()
        EshtblshmntViewModel.userisinqueueData.value = null

        initViews()

        searchViewModel.getEshtablshmnt(requireContext(), "", "", "")

        SearchViewModel.eshtblshmntListData.observe(viewLifecycleOwner) {

            val number_of_items = it.size.toString()
            val text: String = String.format(getString(R.string.count_result_found), it.size)

            val fcs = ForegroundColorSpan(Color.rgb(244, 162, 97))
            val bss = StyleSpan(Typeface.BOLD)
            val spannable: Spannable = SpannableString(text)

            spannable.setSpan(
                fcs,
                9,
                9 + number_of_items.length + 8,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannable.setSpan(
                bss,
                9,
                number_of_items.length + 8,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            txt_nearBy.setText(spannable)

//            txt_nearBy.text =
//                String.format(resources.getString(R.string.count_reasult_found), it.size)
            initSearchResRecyclerView()
        }

        EshtblshmntViewModel.userisinqueueData.observe(viewLifecycleOwner)
        {
            EshtblshmntViewModel.userisinqueueData.value?.let {

                when(it.user_is_in_queue)
                {
                    "1" -> {
                        val bundle = Bundle()
                        bundle.putString("mode","user_in_queue")
                        bundle.putString("category", "1")
                        bundle.putString("restro_id",it.res_id)
                        bundle.putString("dinning_area_id","")
                        bundle.putString("user_queue_id",it.user_queue_id)
                        val viewQueueBooking = ViewQueueBooking()
                        viewQueueBooking.arguments = bundle
                        AppCommonUtils.loadFragment(requireActivity(),viewQueueBooking)
                    }
                    "0" -> {
                        val bundle = Bundle()
                        bundle.putString("restro_id",it.res_id)
                        val chooseseatingQueueFragment = ChooseseatingQueueFragment()
                        chooseseatingQueueFragment.arguments = bundle
                        AppCommonUtils.loadFragment(requireActivity(),chooseseatingQueueFragment)
                    }
                    else ->{

                        Toast.makeText(context, "Restaurant is currently closed for queue booking", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }


        SearchViewModel.eshtb_progressbar.observe(viewLifecycleOwner)
        {
            if (it) {
                eshtblsh_progressbar.visibility = View.VISIBLE
            } else {
                eshtblsh_progressbar.visibility = View.GONE
            }
        }

        return root
    }

    fun initViews() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.VISIBLE
//        lyt_back = root.findViewById(R.id.lyt_back)
        txt_nearBy = root.findViewById(R.id.txt_nearBy)
        tv_srchbykeyword = root.findViewById(R.id.tv_srchbykeyword)
        eshtblsh_progressbar = root.findViewById(R.id.eshtblsh_progressbar)
        search_restaurants_recy = root.findViewById(R.id.search_restaurants_recy)

        menu = bottomNav.getMenu()
        menuItem = menu.getItem(1)
        menuItem.setChecked(true)

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

        tv_srchbykeyword.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                AppCommonUtils.hideKeyboard(requireActivity())
                searchViewModel.getEshtablshmnt(
                    requireContext(),
                    tv_srchbykeyword.text.toString(),
                    "",
                    ""
                )
                return@OnEditorActionListener true
            }
            false
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    fun initSearchResRecyclerView() {
        eshtablishmentAdapter = EshtablishmentAdapter(
            requireActivity(),
            requireContext(),
            SearchViewModel.eshtblshmntListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        search_restaurants_recy.smoothScrollToPosition(0)
        search_restaurants_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        search_restaurants_recy.setAdapter(eshtablishmentAdapter)
        eshtablishmentAdapter.notifyDataSetChanged()
        eshtablishmentAdapter.setClickListener(this)
    }

    fun onBackPressed() {

        requireActivity().finish()

    }

    override fun onClick(view: View?, module: String, position: Int) {

        when(module)
        {
            "checkqueueAvail" -> {
                if(AppPreferenceStorage.getUserid().equals(""))
                {
                    AppCommonUtils.showCancelBookingDialog(requireActivity())
                }
                else{
                    eshtblshmntViewModel.checkUserInQueue(requireContext(), SearchViewModel.eshtblshmntListData.value!!.get(position).id.toString())
                }
            }
        }
    }


}