package com.tabour.hospitality.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.onesignal.OSSubscriptionObserver
import com.onesignal.OSSubscriptionStateChanges
import com.onesignal.OneSignal
import com.tabour.hospitality.R
import com.tabour.hospitality.utils.AppCommonUtils
import com.tabour.hospitality.utils.AppPreferenceStorage
import java.util.*


class Splash : AppCompatActivity(), OSSubscriptionObserver {

    lateinit var player_id: String
    lateinit var videoView: VideoView
    val time_in_ms: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        AppCommonUtils.getCurrentActivity(this@Splash)


        // lock orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // OneSignal configure
        OneSignal.addSubscriptionObserver(this)

        // Lock default Layout Direction is ltr
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val configuration: Configuration = resources.configuration
            configuration.setLayoutDirection(Locale("en"))
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }

        videoView = findViewById(R.id.video_app_intro)
//        setContentView(videoView)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else
        {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val video: Uri = Uri.parse("android.resource://${packageName}/${R.raw.tabour_splash_screen}")
        videoView.setVideoURI(video)

        videoView.setOnCompletionListener(OnCompletionListener{
            jumpToAnotherActivity()
        })
        videoView.start()

//        Handler(Looper.getMainLooper()).postDelayed({
//
//            val userid: String = AppPreferenceStorage.getUserid()
//            if(userid == null || userid.equals("", ignoreCase = true))
//            {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//            else
//            {
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }
//
//        }, time_in_ms)

    }

    @SuppressLint("SuspiciousIndentation")
    fun jumpToAnotherActivity()
    {
        if(isFinishing)
        return

        val userid: String = AppPreferenceStorage.getUserid()
        if(userid == null || userid.equals("", ignoreCase = true))
        {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("mode","Splash main")
            startActivity(intent)
            finish()
        }

    }

    override fun onOSSubscriptionChanged(stateChanges: OSSubscriptionStateChanges?) {
        if (!stateChanges!!.from.isSubscribed &&
            stateChanges.to.isSubscribed
        ) {
            // get player ID
            player_id = stateChanges.to.userId
            AppPreferenceStorage.savePlayerid(player_id)
            Log.d("playerID", "playerID is :$player_id")
        }
        Log.i("Debug", "onOSPermissionChanged: $stateChanges")
    }

}