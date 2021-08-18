package com.zzp.dtrip.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.zzp.dtrip.R
import com.zzp.dtrip.body.CmpFaceBody
import com.zzp.dtrip.body.LoginBody
import com.zzp.dtrip.data.LoginResult
import com.zzp.dtrip.data.User
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import com.zzp.dtrip.util.UserInformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var registerText: TextView
    private lateinit var entryButton: MaterialButton
    private lateinit var rememberPass: CheckBox
    private lateinit var prefs: SharedPreferences
    private lateinit var faceEntryButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var username = ""
    private var password = ""

    private var imageBase64: String = ""

    private lateinit var imageUri: Uri

    private lateinit var outputImage: File

    private val CMP_FACE = 4

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

        faceEntryButton.setOnClickListener { //开启人脸登录活动
            faceLogin()
//            val intent = Intent(this, FaceLoginActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun findViewById() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        registerText = findViewById(R.id.register_text)
        entryButton = findViewById(R.id.entry_button)
        rememberPass = findViewById(R.id.remember_pass)
        faceEntryButton = findViewById(R.id.face_entry_button)
        progressBar = findViewById(R.id.progress_bar)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CMP_FACE  && resultCode == Activity.RESULT_OK) {
            progressBar.visibility = View.VISIBLE
            var imageBitmap = BitmapFactory.decodeStream(contentResolver.
                    openInputStream(imageUri))
            val byteArrayOutputStream = ByteArrayOutputStream()
            imageBitmap = compressImage(imageBitmap)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                byteArrayOutputStream)
            byteArrayOutputStream.flush()
            byteArrayOutputStream.close()

            val imageByteArray = byteArrayOutputStream.toByteArray()
            imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
//            Log.d(TAG+"i", imageBase64)
            println(imageBase64)
            compareFace()
        }
    }

    private fun compressImage(image :Bitmap) : Bitmap{
        val matrix = Matrix()
        val w = image.width
        val h = image.height
        val true_width = 80.0f
        val true_height = true_width * h / w
        if (true_width >w) return image
        val wsx = true_height/w
        matrix.setScale(wsx,wsx)
        return Bitmap.createBitmap(image,0,0,w,h,matrix,true)
    }

    private fun compareFace() {
        val appService= RetrofitManager.create<AppService>()
        Log.d(TAG, "compareFace: ${imageBase64.length}")
        val task = appService.compareFace(CmpFaceBody(imageBase64))
        task.enqueue(object : Callback<LoginResult>{
            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (this.errorCode == 0) {
                        progressBar.visibility = View.GONE
                        loginSuccess(this.user)
                    }
                    else {
                        Toast.makeText(this@LoginActivity, errorMessage,
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
            }
        })
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

    private fun faceLogin() {
        outputImage = File(externalCacheDir, "output_image.jpg")
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.zzp.dtrip." +
                    "fileprovider", outputImage)
        } else {
            Uri.fromFile(outputImage)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CMP_FACE)
    }
}