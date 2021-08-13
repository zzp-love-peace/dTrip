package com.zzp.dtrip.activity

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zzp.dtrip.R
import com.zzp.dtrip.service.SoundService

import java.util.*

class SoundActivity : AppCompatActivity() {

    private lateinit var myBinder: SoundService.MyBinder

    private lateinit var soundService: SoundService

    private lateinit var myReceiver: BroadcastReceiver

    private lateinit var notifyText: TextView

    private lateinit var switchMaterial: SwitchMaterial

    private lateinit var prefs: SharedPreferences

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

    companion object {
        var swichFlag = false
    }

    private val TAG = "SoundActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)
        findViewById()
        bindService()
        startService()
        doRegisterReceiver()
        initPrefAndSwitch()
        initButtonAndText()

        stopButton.setOnClickListener {
            myBinder.stopVibrate()
            stopButton.visibility = View.GONE

        }

        switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            swichFlag = isChecked
        }

        floatButton.setOnClickListener {

            if (!SoundService.buttonFlag) {
                if (ActivityCompat.checkSelfPermission(this@SoundActivity, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                    myBinder.start()
                }
                ActivityCompat.requestPermissions(this, perms,RC_RECORD_CODE)
            }
            else {
                myBinder.stop()
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
        switchMaterial = findViewById(R.id.switch_material)
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

    private fun initPrefAndSwitch() {
        prefs = getPreferences(Context.MODE_PRIVATE)
        swichFlag = prefs.getBoolean("switch", false)
        switchMaterial.isChecked = swichFlag
        Log.d(TAG, "initPrefAndSwitch: ")
    }

    private fun saveSwitchFlag() {
        val edit = prefs.edit()
        edit.putBoolean("switch", swichFlag)
        edit.apply()
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
                    if (swichFlag) {
                        notifyText.setTextColor(resources.getColor(R.color.black))
                    }
                    else {
                        notifyText.setTextColor(resources.getColor(R.color.red))
                    }
                }
                "com.zzp.SOUND_EVENT_TYPE_DOOR_BELL" -> {
                    if (swichFlag) {
                        stopButton.visibility = View.VISIBLE
                        notifyText.setTextColor(resources.getColor(R.color.red))
                    }
                    else {
                        notifyText.setTextColor(resources.getColor(R.color.black))
                    }
                }
                "com.zzp.SOUND_EVENT_TYPE_KNOCK" -> {
                    if (swichFlag) {
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

    override fun onDestroy() {
        super.onDestroy()
        saveSwitchFlag()
    }
}
