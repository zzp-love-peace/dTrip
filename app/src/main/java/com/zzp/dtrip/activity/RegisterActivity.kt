package com.zzp.dtrip.activity

import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputLayout
import com.zzp.dtrip.R
import com.zzp.dtrip.body.RegisterBody
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var passwordAgainLayout: TextInputLayout
    private lateinit var registerButton: MaterialButton
    private lateinit var manButton: MaterialRadioButton
    private lateinit var womanButton: MaterialRadioButton
    private lateinit var radioGroup: RadioGroup

    private var username = ""
    private var password = ""
    private var passwordAgain = ""
    private var sex = ""

    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        findViewById()
        setFocus()

        registerButton.setOnClickListener {
            username = usernameLayout.editText?.text.toString()
            password = passwordLayout.editText?.text.toString()
            passwordAgain = passwordAgainLayout.editText?.text.toString()
            if (isNotEmpty()) {
                if (password != passwordAgain) {
                    passwordAgainLayout.error = "密码不一致"
                }
                else {
                    postRegister()
                }
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.register_man -> {
                    manButton.setTextColor(resources.getColor(R.color.white))
                    womanButton.setTextColor(resources.getColor(R.color.gray))
                    sex = "0"
                }
                R.id.register_woman -> {
                    womanButton.setTextColor(resources.getColor(R.color.white))
                    manButton.setTextColor(resources.getColor(R.color.gray))
                    sex = "1"
                }
            }
        }
    }

    private fun findViewById() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        passwordAgainLayout = findViewById(R.id.password2_layout)
        registerButton = findViewById(R.id.register_button)
        manButton = findViewById(R.id.register_man)
        womanButton = findViewById(R.id.register_woman)
        radioGroup = findViewById(R.id.radio_group)
    }

    private fun postRegister() {
        val appService = RetrofitManager.create<AppService>()
        val sexNum = if (sex == "男") 0 else 1
        val task = appService.postRegister(RegisterBody(username, password, sexNum.toString()))
        task.enqueue(object : Callback<NormalResult>{
            override fun onResponse(call: Call<NormalResult>,
                                    response: Response<NormalResult>) {
                Log.d(TAG, "onResponse: ")
                response.body()?.apply {
                    if (errorCode == 0) {
                        Toast.makeText(this@RegisterActivity,
                            "注册成功", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    else {
                        Toast.makeText(this@RegisterActivity,
                            errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
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
        passwordAgainLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            passwordAgainLayout.error = ""
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
        if (passwordAgain.trim().isEmpty()) {
            passwordAgainLayout.error = "密码为空"
            flag = false
        }
        else {
            passwordAgainLayout.error = ""
        }
        return flag
    }
}