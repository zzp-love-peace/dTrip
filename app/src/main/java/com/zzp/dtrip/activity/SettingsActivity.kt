package com.zzp.dtrip.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zzp.dtrip.R
import com.zzp.dtrip.util.UserInformation
import kotlin.concurrent.thread

class SettingsActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar

    private lateinit var navSettingsView: NavigationView

    private lateinit var switchMaterial: SwitchMaterial

    private lateinit var prefs: SharedPreferences

    companion object {
        var switchFlag = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById()
        initPrefAndSwitch()
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navSettingsView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_password -> {
                    if (UserInformation.isLogin) {
                        val intent = Intent(this, ReplaceActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_home -> {
                    //switchMaterial.callOnClick()
                    thread {
                        runOnUiThread {
                            switchMaterial.performClick()
                        }
                    }
                }
                R.id.action_personal_about -> {
                    val intent = Intent(this, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            false
        }

        switchMaterial.setOnCheckedChangeListener { _, isChecked ->
            switchFlag = isChecked
            saveSwitchFlag()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun findViewById() {
        toolbar = findViewById(R.id.toolbar)
        navSettingsView = findViewById(R.id.nav_settings_view)
        switchMaterial = findViewById(R.id.switch_material)
    }

    private fun initPrefAndSwitch() {
        prefs = this.getPreferences(Context.MODE_PRIVATE)
        switchFlag = prefs.getBoolean("switch", false)
        switchMaterial.isChecked = switchFlag
        //Log.d(TAG, "initPrefAndSwitch: ")
    }

    private fun saveSwitchFlag() {
        val edit = prefs.edit()
        edit.putBoolean("switch", switchFlag)
        edit.apply()
    }
}