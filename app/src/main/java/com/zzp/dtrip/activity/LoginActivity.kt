package com.zzp.dtrip.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.zzp.dtrip.R
import com.zzp.dtrip.body.LoginBody
import com.zzp.dtrip.data.LoginResult
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import com.zzp.dtrip.util.UserInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var registerText: TextView
    private lateinit var entryButton: MaterialButton
    private lateinit var rememberPass: CheckBox
    private lateinit var prefs: SharedPreferences
    private lateinit var loginFace:TextView

    private var username = ""
    private var password = ""

    private val TAG = "LoginActivity"

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

        loginFace.setOnClickListener { //开启人脸登录活动
            val intent = Intent(this,FaceLoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun findViewById() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        registerText = findViewById(R.id.register_text)
        entryButton = findViewById(R.id.entry_button)
        rememberPass = findViewById(R.id.remember_pass)
        loginFace = findViewById(R.id.login_by_face)
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
                        UserInformation.password = password
                        UserInformation.username = username
                        UserInformation.ID = this.user.id
                        UserInformation.sex = this.user.sex
                        Log.d(TAG, "onResponse: ${this.user.sex}")
                        UserInformation.isLogin = true
                        val intent = Intent("com.zzp.LOGIN_SUCCESS")
                        sendBroadcast(intent)
                        Toast.makeText(this@LoginActivity, "登录成功",
                            Toast.LENGTH_SHORT).show()
                        onBackPressed()
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
}