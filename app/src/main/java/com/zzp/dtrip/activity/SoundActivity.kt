package com.zzp.dtrip.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BlendMode
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zzp.dtrip.R
import com.zzp.dtrip.service.SoundService

import java.util.*

class SoundActivity : AppCompatActivity() {

    private lateinit var myBinder: SoundService.MyBinder

    private lateinit var soundService: SoundService

    private lateinit var myReceiver: BroadcastReceiver

    private lateinit var notifyText: TextView

    private val connection =  object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            myBinder = service as SoundService.MyBinder
            soundService = myBinder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    private lateinit var stopButton: Button

    private lateinit var floatButton: FloatingActionButton

    private val perms = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val RC_RECORD_CODE = 0x123



    private val TAG = "SoundActivity"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)
        findViewById()
        bindService()
        startService()
        doRegisterReceiver()
        initButtonAndText()

        stopButton.setOnClickListener {
            myBinder.stopVibrate()
            stopButton.visibility = View.GONE

        }

        floatButton.setOnClickListener {

            if (!SoundService.buttonFlag) {
                if (ActivityCompat.checkSelfPermission(this@SoundActivity, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                    myBinder.start()
//                    floatButton.background = resources.getDrawable(R.color.gray)
                }
                ActivityCompat.requestPermissions(this, perms,RC_RECORD_CODE)
            }
            else {
                myBinder.stop()
//                floatButton.background = resources.getDrawable(R.color.blue)
            }
            SoundService.buttonFlag = !SoundService.buttonFlag
            SoundService.text = ""
            floatButton.isSelected = SoundService.buttonFlag
            notifyText.text = "声音报警系统"
            notifyText.setTextColor(resources.getColor(R.color.black))
        }

    }

    private fun findViewById() {
        stopButton = findViewById(R.id.stop_button)
        floatButton = findViewById(R.id.floating_Button)
        notifyText = findViewById(R.id.notify_text)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "onRequestPermissionsResult ")
        if (requestCode == RC_RECORD_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            myBinder.start()
        }
    }

    private fun bindService() {
        val intent = Intent(this, SoundService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun startService() {
        val intent = Intent(this, SoundService::class.java)
        startService(intent)
    }

    private fun initButtonAndText() {
        floatButton.isSelected = SoundService.buttonFlag
//        if (SoundService.buttonFlag) {
//            floatButton.background = resources.getDrawable(R.color.gray)
//        }
//        else {
//            floatButton.background = resources.getDrawable(R.color.blue)
//        }
        if (SoundService.isVibrate) {
            stopButton.visibility = View.VISIBLE
            notifyText.setTextColor(resources.getColor(R.color.red))
        }
        if (SoundService.text.isNotEmpty()) notifyText.text = SoundService.text
    }

    private fun doRegisterReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_LAUGHTER")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_BABY_CRY")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_SNORING")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_SNEEZE")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_SCREAMING")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_BARK")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_WATER")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_MEOW")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_CAR_ALARM")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_DOOR_BELL")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_KNOCK")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_ALARM")
        intentFilter.addAction("com.zzp.SOUND_EVENT_TYPE_STEAM_WHISTLE")
        myReceiver = MyBroadcastReceiver()
        registerReceiver(myReceiver, intentFilter)
    }


    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.zzp.SOUND_EVENT_TYPE_LAUGHTER" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_BABY_CRY" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_SNORING" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_SNEEZE" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_SCREAMING" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_MEOW" -> {
//                    stopButton.visibility = View.VISIBLE
//                    notifyText.setTextColor(resources.getColor(R.color.red))
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_BARK" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_WATER" -> {
                    notifyText.setTextColor(resources.getColor(R.color.black))
                }
                "com.zzp.SOUND_EVENT_TYPE_CAR_ALARM" -> {
                    stopButton.visibility = View.VISIBLE
                    if (SettingsActivity.switchFlag) {
                        notifyText.setTextColor(resources.getColor(R.color.black))
                    }
                    else {
                        notifyText.setTextColor(resources.getColor(R.color.red))
                    }
                }
                "com.zzp.SOUND_EVENT_TYPE_DOOR_BELL" -> {
                    if (SettingsActivity.switchFlag) {
                        stopButton.visibility = View.VISIBLE
                        notifyText.setTextColor(resources.getColor(R.color.red))
                    }
                    else {
                        notifyText.setTextColor(resources.getColor(R.color.black))
                    }
                }
                "com.zzp.SOUND_EVENT_TYPE_KNOCK" -> {
                    if (SettingsActivity.switchFlag) {
                        stopButton.visibility = View.VISIBLE
                        notifyText.setTextColor(resources.getColor(R.color.red))
                    }
                    else {
                        notifyText.setTextColor(resources.getColor(R.color.black))
                    }
                }
                "com.zzp.SOUND_EVENT_TYPE_ALARM" -> {
                    stopButton.visibility = View.VISIBLE
                    notifyText.setTextColor(resources.getColor(R.color.red))
                }
                "com.zzp.SOUND_EVENT_TYPE_STEAM_WHISTLE" -> {
                    stopButton.visibility = View.VISIBLE
                    notifyText.setTextColor(resources.getColor(R.color.red))
                }
            }
            notifyText.text = SoundService.text
        }

    }
}
