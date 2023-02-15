package com.tabour.hospitality.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tabour.hospitality.R
import com.tabour.hospitality.utils.AppCommonUtils

/**
 * A simple [Fragment] subclass.
 * Use the [Termsandpolicies.newInstance] factory method to
 * create an instance of this fragment.
 */
class Termsandpolicies : Fragment() {

    lateinit var root: View
    lateinit var toolbar: Toolbar
    lateinit var tv_page_name: TextView
    lateinit var bottomNav: BottomNavigationView
    lateinit var lyt_back: LinearLayout
    lateinit var webview_terms_and_privacy_policies: WebView
    var pagename: String = ""
    var pageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_tersandpolicies, container, false)
        pagename = arguments?.getString("pagename").toString()
        pageUrl = arguments?.getString("pageurl").toString()
        initViews()

        webview_terms_and_privacy_policies.setWebViewClient(object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        })

        startWebView(pageUrl)

        return root
    }

    private fun initViews() {
        toolbar = requireActivity().findViewById(R.id.toolbar)
        toolbar.visibility = View.GONE
        bottomNav = requireActivity().findViewById(R.id.bottom_nav_view)
        bottomNav.visibility = View.GONE
        tv_page_name = root.findViewById(R.id.tv_page_name)
        tv_page_name.text = pagename
        lyt_back = root.findViewById(R.id.lyt_back)

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

        webview_terms_and_privacy_policies = root.findViewById(R.id.webview_terms_and_privacy_policies)
    }

    private fun startWebView(url: String) {
        webview_terms_and_privacy_policies.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            //Show loader on url load
            override fun onLoadResource(view: WebView, url: String) {}
            override fun onPageFinished(view: WebView, url: String) {

                Log.d("trackPageLink", url)
            }
        })

        //Load url in webView
        webview_terms_and_privacy_policies.loadUrl(url)
    }

    fun onBackPressed() {
        AppCommonUtils.hideKeyboard(requireActivity())
        AppCommonUtils.loadFragment(requireActivity(), HomeFragment())
    }


}