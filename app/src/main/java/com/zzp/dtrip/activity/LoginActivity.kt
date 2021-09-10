package com.zzp.dtrip.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult
import com.lusfold.spinnerloading.SpinnerLoading
import com.zzp.dtrip.R
import com.zzp.dtrip.body.CmpFaceBody
import com.zzp.dtrip.body.LoginBody
import com.zzp.dtrip.data.LoginResult
import com.zzp.dtrip.data.User
import com.zzp.dtrip.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var registerText: TextView
    private lateinit var entryButton: MaterialButton
    private lateinit var rememberPass: CheckBox
    private lateinit var prefs: SharedPreferences
    private lateinit var faceEntryButton: MaterialButton
    private lateinit var spinnerLoading: SpinnerLoading

    private var username = ""
    private var password = ""

    private var isPermissionRequested = false
    private var bitmapCurrent: Bitmap? = null

    private val TAG = "LoginActivity"

    private val liveFaceCallback: MLLivenessCapture.Callback = object : MLLivenessCapture.Callback {
        override fun onSuccess(result: MLLivenessCaptureResult) {
            //检测成功的处理逻辑，检测结果可能是活体或者非活体。
            if (!result.isLive) {
                showUserWrong("未检测出人脸", this@LoginActivity)
                if (spinnerLoading.visibility == View.VISIBLE) {
                    spinnerLoading.visibility = View.GONE
                }
                return
            }
            bitmapCurrent = result.bitmap
            Log.e(
                "TAG",
                "拍照获取人脸照片" + bitmapCurrent?.width.toString() + "   " + bitmapCurrent?.height
            )
            if (bitmapCurrent == null) {
                showUserWrong("failed to get picture!",this@LoginActivity)
                if (spinnerLoading.visibility == View.VISIBLE) {
                    spinnerLoading.visibility = View.GONE
                }
                return
            }
            compareFace(bitmapCurrent!!)

        }

        override fun onFailure(errorCode: Int) {
            //检测未完成，如相机异常CAMERA_ERROR,添加失败的处理逻辑。
            showUserWrong("检测失败 errorCode = $errorCode", this@LoginActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById()
        setFocus()
        initData()
        entryButton.setOnClickListener {
            username = usernameLayout.editText?.text.toString()
            password = passwordLayout.editText?.text.toString()
            if (isNotEmpty()) {
                postLogin()
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        faceEntryButton.setOnClickListener { //开启人脸登录活动
            // Checking Camera Permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                val capture = MLLivenessCapture.getInstance()
                capture.startDetect(this, liveFaceCallback)
                thread {
                    Thread.sleep(1000)
                    runOnUiThread {
                        spinnerLoading.visibility = View.VISIBLE
                    }
                }
            } else {
                checkPermission()
            }
        }
    }

    private fun findViewById() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        registerText = findViewById(R.id.register_text)
        entryButton = findViewById(R.id.entry_button)
        rememberPass = findViewById(R.id.remember_pass)
        faceEntryButton = findViewById(R.id.face_entry_button)
        spinnerLoading = findViewById(R.id.spinner_loading)
    }

    private fun initData() {
        prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember) {
            password = prefs.getString("password", "") ?: ""
            passwordLayout.editText?.setText(password)
            rememberPass.isChecked = true
        }
        username = prefs.getString("username", "") ?: ""
        usernameLayout.editText?.setText(username)
    }

    private fun savePassword() {
        val editor = prefs.edit()
        if (rememberPass.isChecked) {
            editor.putBoolean("remember_password", true)
            editor.putString("password", password)
        }
        else {
            editor.clear()
        }
        editor.putString("username", username)
        editor.apply()
    }

    private fun postLogin() {
        Log.d(TAG, "postLogin: ")
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postLogin(LoginBody(username, password))
        task.enqueue(object : Callback<LoginResult>{
            override fun onResponse(call: Call<LoginResult>,
                                    response: Response<LoginResult>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (errorCode == 0) {
                        savePassword()
                        loginSuccess(this.user)
                    }
                    else {
                        passwordLayout.error = "用户名或密码错误"
                    }
                }
            }

            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    }

    private fun setFocus() {
        usernameLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            usernameLayout.error = ""
        }
        passwordLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            passwordLayout.error = ""
        }
    }

    private fun isNotEmpty() : Boolean {
        var flag = true
        if (username.trim().isEmpty()) {
            usernameLayout.error = "用户名为空"
            flag = false
        }
        else {
            usernameLayout.error = ""
        }
        if (password.trim().isEmpty()) {
            passwordLayout.error = "密码为空"
            flag = false
        }
        else {
            passwordLayout.error = ""
        }
        return flag
    }

//    登录成功后对UserInformation的赋值
    private fun loginSuccess(user: User) {
        UserInformation.username = user.username
        UserInformation.password = user.password
        UserInformation.ID = user.id
        UserInformation.sex = user.sex
        Log.d(TAG, "onResponse: ${user.sex}")
        UserInformation.isLogin = true
        val intent = Intent("com.zzp.LOGIN_SUCCESS")
        sendBroadcast(intent)
        Toast.makeText(this, "登录成功",
            Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun compareFace(image :Bitmap) {
        val appService= RetrofitManager.create<AppService>()
        val task = appService.compareFace(CmpFaceBody(bitmap2Base64(compressImage(image))))
        task.enqueue(object : Callback<LoginResult> {
            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (this.errorCode == 0) {
                        loginSuccess(this.user)
                    }
                    else {
                        showUserWrong(errorMsg, this@LoginActivity)
                        Log.d(TAG, "onResponse: --> $errorMsg")
                    }
                    spinnerLoading.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                if (spinnerLoading.visibility == View.VISIBLE) {
                    spinnerLoading.visibility = View.GONE
                }
            }
        })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true
            val permissionsList = ArrayList<String>()
            for (perm in LiveHandGestureAnalyseActivity.getAllPermission()) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm)
                }
            }
            if (permissionsList.isNotEmpty()) {
                requestPermissions(permissionsList.toTypedArray(), 0)
            }
        }
    }
}