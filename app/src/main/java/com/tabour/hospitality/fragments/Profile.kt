package com.tabour.hospitality.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.mikhaellopez.circularimageview.CircularImageView
import com.tabour.hospitality.R
import com.tabour.hospitality.adapters.CountryWithFlagAdapter
import com.tabour.hospitality.databinding.FragmentProfileBinding
import com.tabour.hospitality.models.Countries
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import com.tabour.hospitality.utils.AppWaitDialog
import com.tabour.hospitality.viewmodels.EshtblshmntViewModel
import com.tabour.hospitality.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_otpverify.*
import kotlinx.android.synthetic.main.activity_otpverify.txt_pin_entry
import kotlinx.android.synthetic.main.edit_email.*
import kotlinx.android.synthetic.main.edit_login_details.*
import kotlinx.android.synthetic.main.edit_personal_details.*
import kotlinx.android.synthetic.main.edit_profile_view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.tv_username
import kotlinx.android.synthetic.main.fragment_search_availability.*
import kotlinx.android.synthetic.main.include_drawer_content.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment() {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var bottomNav: BottomNavigationView
    lateinit var menu: Menu
    lateinit var menuItem: MenuItem
    lateinit var bottom_sheet: View
    lateinit var tv_edit_profile: TextView
    lateinit var testView: TextView
    lateinit var country_phn_code: TextView
    lateinit var phone_edt: EditText
    lateinit var txt_pin_entry: PinEntryEditText
    lateinit var tv_send_otp: TextView
    lateinit var tv_resend_otp: TextView
    lateinit var tv_resend: TextView
    lateinit var tv_timer: TextView
    lateinit var tv_username: TextView
    lateinit var tv_mobile_number: TextView
    lateinit var imageViews_userprofile: ShapeableImageView
    lateinit var imageView_profile: ShapeableImageView
    lateinit var mBottomSheetDialog: BottomSheetDialog
    lateinit var lyt_back: AppCompatImageView
    lateinit var lyt_edit_profile_image: LinearLayout
    lateinit var binding: FragmentProfileBinding
    lateinit var mBehavior: BottomSheetBehavior<View>
    lateinit var eshtblshmntViewModel: EshtblshmntViewModel
    lateinit var loginViewModel: LoginViewModel
    lateinit var adapter: CountryWithFlagAdapter
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var country_codes: ArrayList<Countries>
    lateinit var mWaitDialog: AppWaitDialog
    var startTimer: Boolean = false
    var timer: CountDownTimer? = null
    private var FILECHOOSER_RESULTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        root = binding.root
        binding.lifecycleOwner = this

        eshtblshmntViewModel = ViewModelProvider(this).get(EshtblshmntViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        EshtblshmntViewModel.profileData.value = null

        initViews()
        eshtblshmntViewModel.getProfileDetails(requireContext())

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK)
            {
                try {
                    val data:Intent = result.data!!
                    val uri = data.data
                    val imageUri = uri.toString()
                    var imageBitmap: Bitmap? = null
                    if(imageUri.contains("content://"))
                    {
                        imageBitmap = BitmapFactory.decodeStream(requireContext().contentResolver.openInputStream(Uri.parse(imageUri)))
                    }

                    val matrix = Matrix()
                    matrix.postRotate(getCameraPhotoOrientation(requireContext(),uri,imageUri).toFloat())
                    val rotatedBitmap = Bitmap.createBitmap(
                        imageBitmap!!, 0, 0, imageBitmap.getWidth(),
                        imageBitmap.getHeight(), matrix, true
                    )
//                    val resizeimage: Bitmap = getResizedBitmapcamera(rotatedBitmap)

//                    val rotatedBitmap = getCameraPhotoOrientation(requireContext(),uri,imageUri)

                    imageView_profile.setImageBitmap(rotatedBitmap)

                    val imageBase64 = "data:image/jpeg;base64,${encodeImage(rotatedBitmap!!)}"
                    Log.d("checkimageBase64", imageBase64)
                    eshtblshmntViewModel.updateProfileImage(requireContext(), imageBase64)

                }catch (e: java.lang.Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        EshtblshmntViewModel.profilepicData.observe(viewLifecycleOwner){
            if(it.equals(""))
            {
                imageView_profile.setImageResource(R.drawable.user_icon_filled)
                imageViews_userprofile.setImageResource(R.drawable.user_icon_filled)
            }
            else
            {
                Glide.with(requireContext())
                    .load(it)
                    .into(imageView_profile)

                Glide.with(requireContext())
                    .load(it)
                    .into(imageViews_userprofile)

            }
        }

        EshtblshmntViewModel.profileData.observe(viewLifecycleOwner){

            it?.let {
                AppPreferenceStorage.saveUsername(it.firstname)
                AppPreferenceStorage.saveMobileNum(it.mobile_no)
                AppPreferenceStorage.saveProfilePic(it.profile_pic)
                tv_username.text = "Hi, ${it.firstname}"
                tv_mobile_number.text = it.mobile_no
                Log.d("checkprofiledetails", "Total reservation is: ${it.total_reservations}")

                Log.d("checkpopup","Status is ${it.phn_number}")

                if(it.phn_number.equals("3", ignoreCase = true))
                {
                    showSuccessMszDialog()
                }
            }
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

    fun initViews() {
        mWaitDialog = AppWaitDialog(requireContext())
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottom_sheet = requireActivity().findViewById(R.id.bottom_sheet)
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.VISIBLE
        lyt_back = root.findViewById(R.id.iv_back)
        mBehavior = BottomSheetBehavior.from(bottom_sheet)
        tv_edit_profile = root.findViewById(R.id.tv_edit_profile)
        imageView_profile = root.findViewById(R.id.imageView_profile)
        tv_username = requireActivity().findViewById(R.id.tv_username)
        tv_mobile_number = requireActivity().findViewById(R.id.tv_mobile_number)
        imageViews_userprofile = requireActivity().findViewById(R.id.imageViews_userprofile)
        lyt_edit_profile_image = root.findViewById(R.id.lyt_edit_profile_image)

        menu = bottomNav.getMenu()
        menuItem = menu.getItem(3)
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

        lyt_back.setOnClickListener {
            onBackPressed()
        }

        tv_edit_profile.setOnClickListener {
            showBottomSheetDialog()
        }

        lyt_edit_profile_image.setOnClickListener {
            openImagefromGallery()
        }

    }

    fun showBottomSheetDialog() {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.edit_profile_view, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)
        val btn_close: TextView = inflate.findViewById(R.id.btn_close)
        val tv_username: TextView = inflate.findViewById(R.id.tv_username)
        val tv_mobile_number: TextView = inflate.findViewById(R.id.tv_mobile_number)
        val tv_email: TextView = inflate.findViewById(R.id.tv_email)
        val edt_username: LinearLayout = inflate.findViewById(R.id.edt_username)
        val edt_mobnum: LinearLayout = inflate.findViewById(R.id.edt_mobnum)
        val edt_email: LinearLayout = inflate.findViewById(R.id.edt_email)

        EshtblshmntViewModel.profileData.value!!.fullname.let {
            tv_username.text = it
        }

        EshtblshmntViewModel.profileData.value!!.mobile_no.let {
            tv_mobile_number.text = it
        }

        EshtblshmntViewModel.profileData.value!!.email.let {
            tv_email.text = it
        }

        edt_username.setOnClickListener {
            this.mBottomSheetDialog.dismiss()
            showEditpersonalDetailsDialog()
        }
        edt_mobnum.setOnClickListener {
            this.mBottomSheetDialog.dismiss()
            showEditloginDtlsDialog(EshtblshmntViewModel.profileData.value!!.country_phonecode,EshtblshmntViewModel.profileData.value!!.only_num)
        }

        edt_email.setOnClickListener {
            this.mBottomSheetDialog.dismiss()
            showEditEmailDialog()
        }

        inflate.findViewById<TextView>(R.id.btn_close).setOnClickListener {
            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun showEditpersonalDetailsDialog() {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.edit_personal_details, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog

//        bottomSheetDialog.setOnShowListener { dialogInterface ->
//
//            val bottomSheetDialog = dialogInterface as BottomSheetDialog
//            val parentLayout =
//                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//            parentLayout?.let { it1 ->
//                val behaviour = BottomSheetBehavior.from(it1)
//                it1.layoutParams.also {
//                    it.height =
//                        context?.resources?.displayMetrics?.heightPixels?.times(0.9)?.toInt()!!
//                }
//                behaviour.peekHeight =
//                    context?.resources?.displayMetrics?.heightPixels?.times(0.6)?.toInt()!!
//            }
//        }

        bottomSheetDialog.setContentView(inflate)
        val edt_fstname: EditText = inflate.findViewById(R.id.edt_fstname)
        val edt_lstname: EditText = inflate.findViewById(R.id.edt_lstname)
        val btn_prsnl_dtls_save: AppCompatButton = inflate.findViewById(R.id.btn_prsnl_dtls_save)

        EshtblshmntViewModel.profileData.value!!.firstname.let {
            edt_fstname.setText(it)
        }

        EshtblshmntViewModel.profileData.value!!.lastname.let {
            edt_lstname.setText(it)
        }

        btn_prsnl_dtls_save.setOnClickListener {
            if(TextUtils.isEmpty(edt_fstname.text.toString()))
            {
                Toast.makeText(requireContext(),"Please enter firstname...",Toast.LENGTH_LONG).show()
            }
            else if(TextUtils.isEmpty(edt_lstname.text.toString()))
            {
                Toast.makeText(requireContext(),"Please enter lastname...",Toast.LENGTH_LONG).show()
            }
            else
            {
                this.mBottomSheetDialog.dismiss()
                eshtblshmntViewModel.updateProfileDtls(
                    requireContext(),
                    edt_fstname.text.toString(),
                    edt_lstname.text.toString(),
                    "",
                    "",
                    "",
                    ""
                )
            }
        }


        inflate.findViewById<TextView>(R.id.btn_close).setOnClickListener {
            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun showEditEmailDialog() {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.edit_email, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)
        val edt_email: EditText = inflate.findViewById(R.id.edt_email)
        val btn_email_save: AppCompatButton = inflate.findViewById(R.id.btn_email_save)

        EshtblshmntViewModel.profileData.value!!.email.let {
            edt_email.setText(it)
        }

        btn_email_save.setOnClickListener {
            if(TextUtils.isEmpty(edt_email.text.toString()))
            {
                Toast.makeText(requireContext(),"Please enter email...",Toast.LENGTH_LONG).show()
            }
            else
            {
                this.mBottomSheetDialog.dismiss()
                eshtblshmntViewModel.updateProfileDtls(
                    requireContext(),
                    "",
                    "",
                    "",
                    "",
                    edt_email.text.toString(),
                    ""
                )
            }
        }


        inflate.findViewById<TextView>(R.id.btn_close).setOnClickListener {
            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun showEditloginDtlsDialog(country_phonecode: String, mobile_number: String) {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.edit_login_details, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)
        testView = inflate.findViewById(R.id.testView)
        country_phn_code = inflate.findViewById(R.id.country_phn_code)
        phone_edt = inflate.findViewById(R.id.phone_edt)
        txt_pin_entry = inflate.findViewById(R.id.txt_pin_entry)
        tv_send_otp = inflate.findViewById(R.id.tv_send_otp)
        tv_resend_otp = inflate.findViewById(R.id.tv_resend_otp)
        tv_resend = inflate.findViewById(R.id.tv_resend)
        tv_timer = inflate.findViewById(R.id.tv_timer)
        val btn_login_dtls_submit: AppCompatButton = inflate.findViewById(R.id.btn_login_dtls_submit)
        var selected_item = ""
        var selected_code = ""

        phone_edt.setText(mobile_number)

        loginViewModel.getCountriesList(requireContext())
        LoginViewModel.otp.value = ""
        LoginViewModel.otp.observe(viewLifecycleOwner) {

            if(!it.equals(""))
            {
                showtimer()
                Log.d("checkotp", it)
                tv_send_otp.visibility = View.GONE
                tv_resend.visibility = View.VISIBLE
                tv_timer.visibility = View.VISIBLE
                startTimer = false

            }
            else
            {
                tv_resend.visibility = View.GONE
                tv_timer.visibility = View.GONE
            }
//            tv_otp.text = LoginViewModel.otp.value!!
        }

        tv_send_otp.setOnClickListener {
            loginViewModel.sendOtp(requireContext(), "${country_phn_code.text}${phone_edt.text}")
        }

        tv_resend_otp.setOnClickListener {
            loginViewModel.sendOtp(requireContext(), "${country_phn_code.text}${phone_edt.text}")
        }

        LoginViewModel.countriesData.observe(viewLifecycleOwner) {
            // Default flag
            country_codes = ArrayList<Countries>()
            for (i in 0 until it.size) {
                if (it.get(i).phonecode.equals(country_phonecode, ignoreCase = true)) {
                    selected_item = LoginViewModel.countriesData.value!!.get(i).iso2
                    selected_code = LoginViewModel.countriesData.value!!.get(i).phonecode
//                countryCode_id = signInViewModel.countriesList.value!!.get(i).id
                }
            }

            testView.setText(
                AppCommonUtils.localeToEmoji(selected_item)
            )
            country_phn_code.text = selected_code
        }

        testView.setOnClickListener {
            showCountryCodesDialog()
        }

        btn_login_dtls_submit.setOnClickListener {
            val entered_pin = txt_pin_entry.text.toString()

            if(!entered_pin.equals(""))
            {
                if (entered_pin.equals(LoginViewModel.otp.value!!)) {
                    this.mBottomSheetDialog.dismiss()
                    eshtblshmntViewModel.updateProfileDtls(
                        requireContext(),
                        "",
                        "",
                        country_phn_code.text.toString(),
                        phone_edt.text.toString(),
                        "",
                        "3"
                    )
                } else {
                    Toast.makeText(requireContext(), "Invalid Otp", Toast.LENGTH_SHORT).show()
                    txt_pin_entry.setText(null)
                }
            }
            else
            {
                Toast.makeText(requireContext(), "Please enter Otp", Toast.LENGTH_SHORT).show()
            }
        }


        inflate.findViewById<TextView>(R.id.btn_close).setOnClickListener {

            if(timer != null)
            {
                timer!!.cancel()
            }
            this.mBottomSheetDialog.dismiss()
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun showSuccessMszDialog() {

        if (this.mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            this.mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        val inflate: View =
            layoutInflater.inflate(R.layout.phn_number_update_success_msz, null as ViewGroup?)
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        this.mBottomSheetDialog = bottomSheetDialog
        bottomSheetDialog.setContentView(inflate)

        val btn_Book_now: AppCompatButton = inflate.findViewById(R.id.btn_Book_now)

        btn_Book_now.setOnClickListener {
            this.mBottomSheetDialog.dismiss()
            AppCommonUtils.hideKeyboard(requireActivity())
            AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
        }

        if (Build.VERSION.SDK_INT >= 21) {
            this.mBottomSheetDialog.getWindow()!!.addFlags(67108864)
        }
        this.mBottomSheetDialog.show()
        this.mBottomSheetDialog.setOnDismissListener {

        }
    }

    fun openImagefromGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        resultLauncher.launch(Intent.createChooser(intent, "File Browser"))
    }

    fun getImageOrientation(imagePath: String): Int {
        var rotate = 0
        try {
            val imageFile = File(imagePath)

            try {
                val path = imageFile.getAbsolutePath()
            }catch (e: java.lang.Exception)
            {
                Log.d("getexception",e.message!!)
            }

            val exif = ExifInterface(
                imageFile.getAbsolutePath()
            )
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return rotate
    }

    fun getCameraPhotoOrientation(context: Context, imageUri: Uri?, imagePath: String?): Int {
        var rotate = 0
        try {
            context.getContentResolver().notifyChange(imageUri!!, null)
            val imageFile = File(imagePath)
            val path = getPath(requireContext(), imageUri)
            val exif = ExifInterface(imageFile.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            Log.i("RotateImage", "Exif orientation: $orientation")
            Log.i("RotateImage", "Rotate value: $rotate")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rotate
    }

    fun getPath(context: Context, uri: Uri?): String? {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.contentResolver.query(uri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
                result = cursor.getString(column_index)
            }
            cursor.close()
        }
        if (result == null) {
            result = "Not found"
        }
        return result
    }

    fun createFlippedBitmap(source: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap{
        val matrix = Matrix()
        matrix.postScale(
            (if (horizontal) -1 else 1).toFloat(),
            (if (vertical) -1 else 1).toFloat(),
            source.width / 2f,
            source.height / 2f
        )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }


    fun getResizedBitmapcamera(image: Bitmap): Bitmap{
        var width: Int = image.getWidth()
        var height: Int = image.getHeight()

        val bitmapRatio = width.toFloat() / height.toFloat()

        if (bitmapRatio > 1 && width < 1875) {
            width = width
            height = height
        } else {
            height = (1875 / bitmapRatio).toInt()
            width = 1875
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    fun modifyOrientation(bitmap: Bitmap,imagePath: String): Bitmap{
        val imageFile = File(imagePath)
        val ei = ExifInterface(
            imageFile.getAbsolutePath()
        )
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotate(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotate(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotate(bitmap, 270F)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flip(bitmap, true, false)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flip(bitmap, false, true)
            else -> bitmap
        }
    }

    fun rotate(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            matrix,
            true
        )
    }

    fun flip(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if(horizontal) -1 else 1).toFloat(),(if(vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            matrix,
            true
        )
    }

    fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val byteFormat = baos.toByteArray()
        return android.util.Base64.encodeToString(byteFormat, android.util.Base64.DEFAULT)
    }

    fun showCountryCodesDialog() {
        // Initialize dialog
        val dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before

        // set custom dialog
        dialog.setContentView(R.layout.lyt_cntrycodes)

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

        val edt_cntry_code: EditText = dialog.findViewById(R.id.edt_cntry_code)
        val codes_list_view: ListView = dialog.findViewById(R.id.codes_list_view)

        adapter = CountryWithFlagAdapter(
            requireContext(),
            R.layout.country_with_flag_row,
            LoginViewModel.countriesData.value!!
        )

        codes_list_view.adapter = adapter

        edt_cntry_code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.getFilter().filter(s)
            }

            override fun afterTextChanged(editable: Editable?) {
                adapter.getFilter().filter(editable)
            }

        })

        codes_list_view.setOnItemClickListener { parent, view, position, id ->

            val tv_cntry_flag = view.findViewById<TextView>(R.id.country_flag)
            val tv_phn_code = view.findViewById<TextView>(R.id.country_phn_code)
            testView.setText(
                tv_cntry_flag.text.toString()
            )
            country_phn_code.setText(tv_phn_code.text.toString())

            dialog.dismiss()
        }

        // show dialog
        dialog.show()

    }

    private fun showtimer() {
        if(startTimer == false)
        {
            Log.d("checkotp", "Timer status is ${startTimer}")
            timer = object : CountDownTimer(180000,1000){
                override fun onTick(millisUntilFinished: Long) {
                    Log.d("currentThread","${Thread.currentThread()}")
                    tv_resend_otp.visibility = View.GONE
                    showTimeinMinutes(millisUntilFinished)
                }

                override fun onFinish() {
                    LoginViewModel.otp.value = ""
                    tv_resend_otp.visibility = View.VISIBLE
                    timer!!.cancel()
                }
            }
            timer!!.start()
            startTimer = true
        }
        else{
//            timer.cancel()
            startTimer = false
            Log.d("checkotp", "Timer status is ${startTimer}")
        }
    }

    private fun showTimeinMinutes(milliseconds: Long) {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        tv_timer.text = "$minutes:$seconds"

        Log.d("timer","$minutes:$seconds")
    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
    }

}