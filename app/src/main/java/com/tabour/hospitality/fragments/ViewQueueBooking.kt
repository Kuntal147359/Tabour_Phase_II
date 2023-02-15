package com.tabour.hospitality.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.databinding.FragmentViewBookingBinding
import com.tabour.hospitality.databinding.FragmentViewQueueBookingBinding
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel.Companion.restro_id

/**
 * A simple [Fragment] subclass.
 * Use the [ViewQueueBooking.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewQueueBooking : Fragment() {
    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_back: LinearLayout
    lateinit var lyt_back_to_home: LinearLayout
    lateinit var lyt_btn: LinearLayout
    lateinit var tv_eshtimated_time: TextView
    lateinit var tv_queue_position: TextView
    lateinit var qr_image: ImageView
    lateinit var btn_on_way: AppCompatTextView
    lateinit var btn_not_coming: AppCompatTextView
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var binding: FragmentViewQueueBookingBinding
    lateinit var mWaitDialog: AppWaitDialog
    var category = ""
    var mode = ""
    var restro_id = ""
    var dinning_area_id = ""
    var user_queue_id = ""
    var qr_url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_view_queue_booking, container, false)
        root = binding.root
        binding.lifecycleOwner = this

        category = arguments?.getString("category").toString()
        mode = arguments?.getString("mode").toString()
        restro_id = arguments?.getString("restro_id").toString()
        dinning_area_id = arguments?.getString("dinning_area_id").toString()
        user_queue_id = arguments?.getString("user_queue_id").toString()

        EshtblshmntViewModel.onthewayStatus.value = false
        EshtblshmntViewModel.queueLeaveStatus.value = "0"

        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        initViews()

        if(mode.equals("user_in_queue"))
        {
            eshtblshmntViewModel.getViewQueueDtls(
                requireContext(),
                user_queue_id
            )
        }
        else
        {
            eshtblshmntViewModel.getViewBookingqueue(
                requireContext(),
                restro_id,
                AppPreferenceStorage.getCount(),
                dinning_area_id
            )
        }

        EshtblshmntViewModel.viewBooknQueueData.observe(viewLifecycleOwner){

            qr_image.setImageBitmap(AppCommonUtils.getQrCodeBitmap(it.qr_url))
            qr_url = it.qr_url

            if(it.queueStatus)
            {
                tv_eshtimated_time.text = String.format(getString(R.string.estimated_time),"${it.initial_waiting_time} minutes")
            }
            else
            {
                showQueueDialog(it.statusMsz)
            }

            if(category.equals("1"))
            {
                if(EshtblshmntViewModel.viewBooknQueueData.value!!.timeout.equals("1"))
                {
                    lyt_btn.visibility = View.GONE
                }
                else{
                    lyt_btn.visibility = View.VISIBLE
                }
            }
            else
            {
                lyt_btn.visibility = View.GONE
            }
        }

        EshtblshmntViewModel.onthewayStatus.observe(viewLifecycleOwner){
            if(it)
            {
                showQueueMsgDialog(getString(R.string.notifiedt_to_restaurant))
            }
        }

        EshtblshmntViewModel.queueLeaveStatus.observe(viewLifecycleOwner){
            if(it.equals("1"))
            {
                AppCommonUtils.loadFragment(requireActivity(),HomeFragment())
            }
        }

        EshtblshmntViewModel.progressbar.observe(viewLifecycleOwner)
        {
            if (it) {
                mWaitDialog.setMessage("please wait...")
                mWaitDialog.show()
            } else {
                mWaitDialog.dismiss()
            }
        }


        return root
    }

    @SuppressLint("CutPasteId")
    fun initViews() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        lyt_back = root.findViewById(R.id.lyt_back)
        tv_eshtimated_time = root.findViewById(R.id.tv_eshtimated_time)
        tv_queue_position = root.findViewById(R.id.tv_queue_position)
        btn_on_way = root.findViewById(R.id.btn_on_way)
        btn_not_coming = root.findViewById(R.id.btn_not_coming)
        qr_image = root.findViewById(R.id.qr_image)
        lyt_back_to_home = root.findViewById(R.id.lyt_back_to_home)
        lyt_btn = root.findViewById(R.id.lyt_btn)

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

        lyt_back_to_home.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
        }

        btn_on_way.setOnClickListener {
            eshtblshmntViewModel.ontheWay(requireContext(), EshtblshmntViewModel.viewBooknQueueData.value!!.restaurant_id, EshtblshmntViewModel.viewBooknQueueData.value!!.user_queue_id)
        }

        btn_not_coming.setOnClickListener {
            showLeaveQueueDialog(EshtblshmntViewModel.viewBooknQueueData.value!!.user_queue_id)
        }

        lyt_back.setOnClickListener {
            onBackPressed()
        }

    }

    fun showQueueDialog(message: String) {
        // Initialize dialog
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.queue_fail_dialog)

        // set transparent background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val tv_queue_status: TextView = dialog.findViewById(R.id.tv_queue_status)
        tv_queue_status.text = message
        val btn_okay: AppCompatTextView = dialog.findViewById(R.id.btn_okay)
        btn_okay.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(),HomeFragment())
            dialog.dismiss()
        }

        // show dialog
        dialog.show()

    }

    fun showQueueMsgDialog(message: String) {
        // Initialize dialog
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.queue_msg_dialog)

        // set transparent background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val tv_queue_status: TextView = dialog.findViewById(R.id.tv_queue_status)
        tv_queue_status.text = message
        val btn_okay: AppCompatTextView = dialog.findViewById(R.id.btn_okay)
        btn_okay.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(),HomeFragment())
            dialog.dismiss()
        }

        // show dialog
        dialog.show()

    }

    fun showLeaveQueueDialog(queue_id: String) {
        // Initialize dialog
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.leave_queue_dialog)

        // set transparent background
        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()!!.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        val tv_queue_status: TextView = dialog.findViewById(R.id.tv_queue_status)
        tv_queue_status.text = String.format(getString(R.string.leave_queue),EshtblshmntViewModel.viewBooknQueueData.value!!.restaurant_name)

        val img_close: AppCompatImageView = dialog.findViewById(R.id.img_close)
        img_close.setOnClickListener {
            dialog.dismiss()
        }

        val btn_yes: AppCompatTextView = dialog.findViewById(R.id.btn_yes)
        btn_yes.setOnClickListener {
            eshtblshmntViewModel.leaveQueue(requireContext(),queue_id)
            dialog.dismiss()
        }

        val btn_no: AppCompatTextView = dialog.findViewById(R.id.btn_no)
        btn_no.setOnClickListener {
            dialog.dismiss()
        }
        // show dialog
        dialog.show()

    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
    }
}