package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textfield.TextInputLayout
import com.zzp.dtrip.R
import com.zzp.dtrip.body.PasswordBody
import com.zzp.dtrip.body.SexBody
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import com.zzp.dtrip.util.UserInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplaceActivity : AppCompatActivity() {

    private lateinit var newPasswordLayout: TextInputLayout
    private lateinit var oldPasswordLayout: TextInputLayout
    private lateinit var againPasswordLayout: TextInputLayout
    private lateinit var replaceButton: MaterialButton

    private var newPassword = ""
    private var oldPassword = ""
    private var againPassword = ""

    private val TAG = "ReplaceActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_replace)
        findViewById()
        setFocus()
        replaceButton.setOnClickListener {
            initText()
            if (isNotEmpty()) {
                if (oldPassword != UserInformation.password) {
                    oldPasswordLayout.error = "密码错误"
                }
                else {
                    if (newPassword != againPassword) {
                        againPasswordLayout.error = "密码不一致"
                    }
                    else {
                        postPassword()
                    }
                }
            }
        }
    }

    private fun findViewById() {
        newPasswordLayout = findViewById(R.id.new_password_layout)
        oldPasswordLayout = findViewById(R.id.old_password_layout)
        againPasswordLayout = findViewById(R.id.again_password_layout)
        replaceButton = findViewById(R.id.replace_button)
    }

    private fun initText() {
        newPassword = newPasswordLayout.editText?.text.toString()
        oldPassword = oldPasswordLayout.editText?.text.toString()
        againPassword = againPasswordLayout.editText?.text.toString()
    }

    private fun postPassword() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postPassword(
            PasswordBody(UserInformation.username,
                UserInformation.password, newPassword)
        )
        task.enqueue(object : Callback<NormalResult> {
            override fun onResponse(call: Call<NormalResult>,
                                    response: Response<NormalResult>
            ) {
                response.body()?.apply {
                    if (errorCode == 0) {
                        UserInformation.password = newPassword
                        Toast.makeText(this@ReplaceActivity, "修改成功",
                            Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    else {
                        Toast.makeText(this@ReplaceActivity,
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
        newPasswordLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            newPasswordLayout.error = ""
        }
        oldPasswordLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            oldPasswordLayout.error = ""
        }
        againPasswordLayout.editText?.setOnFocusChangeListener { v, hasFocus ->
            againPasswordLayout.error = ""
        }
    }

    private fun isNotEmpty() : Boolean {
        var flag = true
        if (newPassword.trim().isEmpty()) {
            newPasswordLayout.error = "用户名为空"
            flag = false
        }
        else {
            newPasswordLayout.error = ""
        }
        if (oldPassword.trim().isEmpty()) {
            oldPasswordLayout.error = "密码为空"
            flag = false
        }
        else {
            oldPasswordLayout.error = ""
        }
        if (againPassword.trim().isEmpty()) {
            againPasswordLayout.error = "密码为空"
            flag = false
        }
        else {
            againPasswordLayout.error = ""
        }
        return flag
    }
}