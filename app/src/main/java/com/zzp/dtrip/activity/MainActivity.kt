package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.common.MLApplication
import com.zzp.dtrip.R
import com.zzp.dtrip.data.Voice
import com.zzp.dtrip.fragment.MineFragment
import com.zzp.dtrip.fragment.TripFragment
import com.zzp.dtrip.util.UserInformation

class MainActivity : AppCompatActivity() {


    private lateinit var tripFragment: TripFragment
    private lateinit var mineFragment: MineFragment

    private lateinit var navView: BottomNavigationView

    private lateinit var navFab: FloatingActionButton

    val REQUEST_CODE_ASR: Int = 100

    private lateinit var mineLayout: View

    private var text = ""

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MLApplication.getInstance().apiKey =
            "CgF6e3x9L8tbJ7yLqpxTYQQhmiVvF4tdvG5CEqxrxMnm5EHxq2uBjzork9ye1W6tllgzBiZPHx1NxDQlD+B5fy3J"
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

        // ??????MineFragment?????????,?????????????????????
        mineLayout = View.inflate(this, R.layout.fragment_mine, null)

        // ??????intent?????????????????????
        val intent = Intent(this, MLAsrCaptureActivity::class.java)
            // ????????????????????????????????????????????????????????????????????????????????????"zh-CN":?????????"en-US":?????????"fr-FR":?????????"es-ES":???????????????"de-DE":?????????"it-IT":???????????????"ar": ???????????????"ru-RU":?????????
            .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
            // ?????????????????????????????????????????????MLAsrCaptureConstants.FEATURE_ALLINONE???????????????MLAsrCaptureConstants.FEATURE_WORDFLUX????????????
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
            // ?????????????????????MLAsrConstants.SCENES_SHOPPING???????????????????????????????????????????????????????????????????????????????????????
            .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)

        navFab = findViewById(R.id.nav_fab)
        navFab.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO), 1
                )
            } else {
                Toast.makeText(this, "fab clicked", Toast.LENGTH_SHORT).show()
                // ?????????????????????
                // REQUEST_CODE_ASR????????????Activity???????????????Activity????????????????????????????????????????????????Activity???????????????????????????????????????
                startActivityForResult(intent, REQUEST_CODE_ASR)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // REQUEST_CODE_ASR??????3?????????????????????Activity???????????????Activity?????????????????????
        if (requestCode == REQUEST_CODE_ASR) {
            when (resultCode) {
                // ??????????????????
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    // ??????????????????????????????????????????
                    if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        // ????????????????????????????????????
                        Log.d(TAG, "onActivityResult: $text")
                        // ???????????????????????????????????????
                        if (text.trim().isNotEmpty()) {
                            when (text) {
                                Voice.LOGIN.text -> {
                                    if (UserInformation.isLogin) {
                                        Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                Voice.REGISTER.text -> {
                                    val intent = Intent(this, RegisterActivity::class.java)
                                    startActivity(intent)
                                }
                                Voice.FACE_LOGIN.text -> {

                                }
                                Voice.FACE_RECORD.text -> {
                                }
                                Voice.REPLACE_PASSWORD.text -> {

                                }
                                Voice.OPEN_USER_INFO.text -> {
                                    if (UserInformation.isLogin) {
                                        val intent = Intent(this, InformationActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                Voice.OPEN_TRIP_DATA.text -> {
                                    if (UserInformation.isLogin) {
                                        val intent = Intent(this, TripDataActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                                Voice.OPEN_SWITCH_MATERIAL.text -> {

                                }
                                Voice.OPEN_SAYING.text -> {
                                    val intent = Intent(this, SocialActivity::class.java)
                                    startActivity(intent)
                                }
                                Voice.OPEN_HEARING.text -> {
                                    val intent = Intent(this, SoundActivity::class.java)
                                    startActivity(intent)
                                }
                                Voice.OPEN_NEARBY_SEARCH.text -> {
                                    val intent = Intent(this, NearbyActivity::class.java)
                                    startActivity(intent)
                                }
                                Voice.OPEN_GESTURE.text -> {
                                    val intent = Intent(this, LiveHandGestureAnalyseActivity::class.java)
                                    startActivity(intent)
                                }
                                Voice.OPEN_SYNTHESIS.text -> {
                                    val intent = Intent(this, SynthesisActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
                // ?????????????????????
                MLAsrCaptureConstants.ASR_FAILURE ->
                    if (data != null) {
                        val bundle = data.extras
                        // ??????????????????????????????
                        if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                            // ???????????????????????????
                        }
                        // ?????????????????????????????????
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                            val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                            // ??????????????????????????????
                        }
                        //?????????????????????????????????
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            val subErrorCode =
                                bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)
                            // ??????????????????????????????
                        }
                    }
                else -> {
                }
            }
        }
    }

}