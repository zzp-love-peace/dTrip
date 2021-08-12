package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.huawei.hms.mlplugin.asr.MLAsrCaptureActivity
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.MsgAdapter

class SocialActivity : AppCompatActivity() {

    companion object {
        val msgList = ArrayList<String>()
    }

    private lateinit var floatButton: FloatingActionButton

    private lateinit var msgRecycler: RecyclerView

    private lateinit var msgAdapter: MsgAdapter

    val REQUEST_CODE_ASR : Int = 100

    private var text = ""
    private val TAG = "SocialActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        floatButton = findViewById(R.id.floating_Button)
        msgRecycler = findViewById(R.id.message_recycler)

        initRecyclerView()
        // 通过intent进行识别设置。
        val intent = Intent(this, MLAsrCaptureActivity::class.java)
            // 设置识别语言为英语，若不设置，则默认识别英语。支持设置："zh-CN":中文；"en-US":英语；"fr-FR":法语；"es-ES":西班牙语；"de-DE":德语；"it-IT":意大利语；"ar": 阿拉伯语；"ru-RU":俄语。
            .putExtra(MLAsrCaptureConstants.LANGUAGE, "zh-CN")
            // 设置拾音界面是否显示识别结果，MLAsrCaptureConstants.FEATURE_ALLINONE为不显示，MLAsrCaptureConstants.FEATURE_WORDFLUX为显示。
            .putExtra(MLAsrCaptureConstants.FEATURE, MLAsrCaptureConstants.FEATURE_WORDFLUX)
            // 设置使用场景，MLAsrConstants.SCENES_SHOPPING：表示购物，仅支持中文，该场景对华为商品名识别进行了优化。
            .putExtra(MLAsrConstants.SCENES, MLAsrConstants.SCENES_SHOPPING)

        floatButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    arrayOf( Manifest.permission.RECORD_AUDIO) ,1)
            }else {
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
                MLAsrCaptureConstants.ASR_SUCCESS -> if (data != null) {
                    val bundle = data.extras
                    // 获取语音识别得到的文本信息。
                    if (bundle!!.containsKey(MLAsrCaptureConstants.ASR_RESULT)) {
                        text = bundle.getString(MLAsrCaptureConstants.ASR_RESULT).toString()
                        // 识别得到的文本信息处理。
                        Log.d(TAG, "onActivityResult: $text")
                        if (text.trim().isNotEmpty()) {
                            msgList.add(text)
                            msgAdapter.notifyItemInserted(msgList.size - 1)
                            msgRecycler.scrollToPosition(msgList.size - 1)
                        }
                    }
                }
                MLAsrCaptureConstants.ASR_FAILURE ->                     // 识别失败处理。
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

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        msgRecycler.layoutManager = layoutManager
        msgAdapter = MsgAdapter(msgList)
        msgRecycler.adapter = msgAdapter
        if (msgList.isNotEmpty()) {
            msgRecycler.scrollToPosition(msgList.size - 1)
        }
    }
}