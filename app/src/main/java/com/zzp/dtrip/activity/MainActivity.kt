package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.common.MLApplication
import com.zzp.dtrip.R
import com.zzp.dtrip.fragment.MineFragment
import com.zzp.dtrip.fragment.TripFragment
import com.zzp.dtrip.util.TtsUtil

class MainActivity : AppCompatActivity() {


    private lateinit var tripFragment: TripFragment
    private lateinit var mineFragment: MineFragment

    private lateinit var navView: BottomNavigationView

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MLApplication.getInstance().apiKey = "CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J"
        navView = findViewById(R.id.nav_view)
        tripFragment = TripFragment()
        mineFragment = MineFragment()
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, tripFragment)
        transaction.add(R.id.nav_host_fragment, mineFragment)
        transaction.hide(mineFragment)
        transaction.commit()
        navView.setOnNavigationItemSelectedListener {
            it.isChecked = true
            val transaction2 = fragmentManager.beginTransaction()
            when (it.itemId) {
                R.id.navigation_trip -> {
                    transaction2.hide(mineFragment)
                    transaction2.show(tripFragment)
                }
                R.id.navigation_mine -> {
                    transaction2.hide(tripFragment)
                    transaction2.show(mineFragment)
                }
            }
            transaction2.commit()
            false
        }
    }

}