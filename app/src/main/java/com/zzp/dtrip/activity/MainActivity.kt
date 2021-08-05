package com.zzp.dtrip.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.huawei.hms.maps.MapsInitializer
import com.zzp.dtrip.R
import com.zzp.dtrip.fragment.MineFragment
import com.zzp.dtrip.fragment.TripFragment

class MainActivity : AppCompatActivity() {


    private lateinit var tripFragment: TripFragment
    private lateinit var mineFragment: MineFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置API key
        MapsInitializer.setApiKey("CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J")
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

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