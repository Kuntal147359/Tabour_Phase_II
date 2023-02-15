package com.tabour.hospitality.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.databinding.FragmentViewBookingBinding
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_restauants.*
import kotlinx.android.synthetic.main.fragment_view_booking.*
import kotlinx.android.synthetic.main.leave_queue_dialog.*

/**
 * A simple [Fragment] subclass.
 * Use the [ViewBooking.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewBooking : Fragment() {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_back: LinearLayout
    lateinit var lyt_invite_friends: LinearLayout
    lateinit var lyt_back_to_home: LinearLayout
    lateinit var lyt_btn: LinearLayout
    lateinit var btn_cancel: AppCompatTextView
    lateinit var qr_image: ImageView
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var binding: FragmentViewBookingBinding
    lateinit var mWaitDialog: AppWaitDialog
    var qr_url = ""
    var reservation_id = ""
    var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_booking, container, false)
        root = binding.root
        binding.lifecycleOwner = this

        reservation_id = arguments?.getString("reservation_id").toString()
        category = arguments?.getString("category").toString()
        EshtblshmntViewModel.bookingcancel_status.value = false

        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)

        initViews()

        eshtblshmntViewModel.getViewReservation(requireContext(),reservation_id)

        EshtblshmntViewModel.viewReservationData.observe(viewLifecycleOwner){
            qr_image.setImageBitmap(AppCommonUtils.getQrCodeBitmap(it.qr_url))
            qr_url = it.qr_url

            if(category.equals("1"))
            {
                if(EshtblshmntViewModel.viewReservationData.value!!.timeout.equals("1"))
                {
                    lyt_btn.visibility = View.VISIBLE
                    binding.lytBackToHome.visibility = View.VISIBLE
                    binding.btnCancel.visibility = View.GONE
                }
                else{
                    lyt_btn.visibility = View.VISIBLE
                    binding.lytBackToHome.visibility = View.VISIBLE
                    binding.btnCancel.visibility = View.VISIBLE
                }
            }
            else
            {
                lyt_btn.visibility = View.VISIBLE
                binding.lytBackToHome.visibility = View.VISIBLE
                binding.btnCancel.visibility = View.GONE
            }
        }

        EshtblshmntViewModel.bookingcancel_status.observe(viewLifecycleOwner){
            if(it)
            {
                AppCommonUtils.hideKeyboard(requireActivity())
                AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
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

    fun initViews()
    {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        lyt_back = root.findViewById(R.id.lyt_back)
        qr_image = root.findViewById(R.id.qr_image)
        lyt_invite_friends = root.findViewById(R.id.lyt_invite_friends)
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

        lyt_back.setOnClickListener {
            onBackPressed()
        }

        lyt_invite_friends.setOnClickListener {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,qr_url)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        lyt_back_to_home.setOnClickListener {
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
        }

        btn_cancel = root.findViewById(R.id.btn_cancel)
        btn_cancel.setOnClickListener {
            showCancelBookingDialog(reservation_id)
        }
    }

    fun showCancelBookingDialog(reservation_id: String) {
        // Initialize dialog
        val dialog = Dialog(requireActivity())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.cancel_booking_dialog)

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
        tv_queue_status.text = String.format(getString(R.string.cancel_booking_text),EshtblshmntViewModel.viewReservationData.value!!.res_name)

        val img_close: AppCompatImageView = dialog.findViewById(R.id.img_close)
        img_close.setOnClickListener {
            dialog.dismiss()
        }

        val btn_yes: AppCompatTextView = dialog.findViewById(R.id.btn_yes)
        btn_yes.setOnClickListener {
            eshtblshmntViewModel.cancelReservation(requireContext(),reservation_id)
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