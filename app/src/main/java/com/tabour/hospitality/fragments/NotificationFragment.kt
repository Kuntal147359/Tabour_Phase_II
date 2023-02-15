package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.BookinHistoryAdapter
import com.tabour.hospitality.adapters.EshtablishmentAdapter
import com.tabour.hospitality.adapters.NotificationAdapter
import com.tabour.hospitality.models.EshtablishmentDetails
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import kotlinx.android.synthetic.main.fragment_booking_history.*
import kotlinx.android.synthetic.main.include_drawer_content.*

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_back: LinearLayout
    lateinit var notification_recy: RecyclerView
    lateinit var notificationAdapter: NotificationAdapter
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var mWaitDialog: AppWaitDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_notification, container, false)
        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        Log.e("OneSignalExample", "Notification fragment is launched..")
        eshtblshmntViewModel.getNotifications(requireContext())

        initViews()

        EshtblshmntViewModel.notificationListData.observe(viewLifecycleOwner){
            initNotificationsRecyclerView()
        }

        EshtblshmntViewModel.progressbar.observe(viewLifecycleOwner){
            if (it) {
                mWaitDialog.setMessage("please wait...")
                mWaitDialog.show()
            } else {
                mWaitDialog.dismiss()
            }
        }


        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initNotificationsRecyclerView() {
        notificationAdapter = NotificationAdapter(
            requireContext(),
            requireActivity(),
            EshtblshmntViewModel.notificationListData.value!!
        )

        //expertListRecyclerView.setHasFixedSize(true)
        notification_recy.smoothScrollToPosition(0)
        notification_recy.setLayoutManager(
            LinearLayoutManager(
                requireContext()
            )
        )
        notification_recy.setAdapter(notificationAdapter)
        notificationAdapter.notifyDataSetChanged()
    }


    private fun initViews() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        lyt_back = root.findViewById(R.id.lyt_back)
        notification_recy = root.findViewById(R.id.notification_recy)

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
    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        val maneger: FragmentManager = requireActivity().supportFragmentManager
        maneger.beginTransaction()
            .remove(this)
            .commit()
        maneger.popBackStack()

        AppCommonUtils.hideKeyboard(requireActivity())
        AppCommonUtils.loadFragment(requireActivity(), HomeFragment())

    }

}