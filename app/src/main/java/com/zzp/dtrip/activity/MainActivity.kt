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

        // 获取MineFragment的布局,使用里面的控件
        mineLayout = View.inflate(this, R.layout.fragment_mine, null)

        // 通过intent进行识别设置。
        val intent = Intent(this, MLAsrCaptureActivity::class.java)
            // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar": 阿拉伯语；"ru-RU":俄语。
            .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
            // 设置拾音界面是否显示识别结果，MLAsrCaptureConstants.FEATURE_ALLINONE为不显示，MLAsrCaptureConstants.FEATURE_WORDFLUX为显示。
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
            // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
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
                // 启动语音识别。
                // REQUEST_CODE_ASR表示当前Activity和拾音界面Activity之间的请求码，通过该码可以在当前Activity中获取拾音界面的处理结果。
                startActivityForResult(intent, REQUEST_CODE_ASR)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // REQUEST_CODE_ASR是第3步中定义的当前Activity和拾音界面Activity之间的请求码。
        if (requestCode == REQUEST_CODE_ASR) {
            when (resultCode) {
                // 识别成功处理
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    // 获取语音识别得到的文本信息。
                    if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        // 识别得到的文本信息处理。
                        Log.d(TAG, "onActivityResult: $text")
                        // 通过用户的语音打开目标功能
                        if (text.trim().isNotEmpty()) {
                            when (text) {
                                Voice.LOGIN.text -> {
                                    if (UserInformation.isLogin) {
                                        Toast.makeText(this, "您已经登录", Toast.LENGTH_SHORT).show()
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
                // 识别失败处理。
                MLAsrCaptureConstants.ASR_FAILURE ->
                    if (data != null) {
                        val bundle = data.extras
                        // 判断是否包含错误码。
                        if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_ERROR_CODE)) {
                            val errorCode = bundle.getInt(MLAsrCaptureConstants.ASR_ERROR_CODE)
                            // 对错误码进行处理。
                        }
                        // 判断是否包含错误信息。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)) {
                            val errorMsg = bundle.getString(MLAsrCaptureConstants.ASR_ERROR_MESSAGE)
                            // 对错误信息进行处理。
                        }
                        //判断是否包含子错误码。
                        if (bundle.containsKey(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)) {
                            val subErrorCode =
                                bundle.getInt(MLAsrCaptureConstants.ASR_SUB_ERROR_CODE)
                            // 对子错误码进行处理。
                        }
                    }
                else -> {
                }
            }
        }
    }

}