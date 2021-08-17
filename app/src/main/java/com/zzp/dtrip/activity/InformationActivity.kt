package com.zzp.dtrip.activity

import android.app.Activity
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.cocosw.bottomsheet.BottomSheet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.zzp.dtrip.R
import com.zzp.dtrip.body.SexBody
import com.zzp.dtrip.body.UsernameBody
import com.zzp.dtrip.data.NormalResult
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import com.zzp.dtrip.util.UserInformation
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class InformationActivity : AppCompatActivity() {

    private val sexList = arrayListOf("男", "女")

    private lateinit var sexLayout: ConstraintLayout
    private lateinit var headLayout: ConstraintLayout
    private lateinit var usernameEdit: TextInputEditText
    private lateinit var sexText: TextView
    private lateinit var idText: TextView
    private lateinit var circleImage: CircleImageView
    private lateinit var changeButton: MaterialButton

    private var imageUri: Uri? = null
    private lateinit var headImage: File

    private lateinit var pref: SharedPreferences

    private var username = ""
    private var sex = ""

    private val takePhoto = 1
    private val fromAlbum = 2
    private var flag = -1

    private val TAG = "InformationActivity"

    private lateinit var pvOptions: OptionsPickerView<Any>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        findViewById()
        pref = getSharedPreferences("data", Context.MODE_PRIVATE)
        initData()
        initImageUri()
        initHeadImage()
        initpvOptions()

        sexLayout.setOnClickListener { pvOptions.show() }

        headLayout.setOnClickListener {
            BottomSheet.Builder(this).sheet(R.menu.bottom_camera_menu)
                .listener { _, which ->
                    when (which) {
                        R.id.album -> {
                            startAlbumActivity()
                        }
                        R.id.camera -> {
                            startCameraActivity()
                        }
                    }
                }.show()
        }

        changeButton.setOnClickListener {
            username = usernameEdit.text.toString()
            sex = sexText.text.toString()
            if (username.isEmpty()) {
                usernameEdit.error = "用户名为空"
            }
            else {
                if (username != UserInformation.username) {
                    postUsername()
                }
                else {
                    if (sex != UserInformation.sex) {
                        postSex()
                    }
                }
            }
        }
    }

    private fun findViewById() {
        sexLayout = findViewById(R.id.sex_layout)
        headLayout = findViewById(R.id.head_layout)
        usernameEdit = findViewById(R.id.username_edit)
        sexText = findViewById(R.id.sex_text)
        idText = findViewById(R.id.id_text)
        circleImage = findViewById(R.id.circle_image)
        changeButton = findViewById(R.id.change_button)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    imageUri?.apply {
                        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(this))
                        circleImage.setImageBitmap(rotateIfRequired(bitmap))
                        saveImageUri(takePhoto)
                    }
                }
            }
            fromAlbum -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    data.data?.let {
                        val bitmap = getBitmapFromUri(it)
                        circleImage.setImageBitmap(bitmap)
                        imageUri = it
                        saveImageUri(fromAlbum)
                    }
                }
            }
        }

    }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(headImage.path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
            bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun initHeadImage() {
        imageUri?.apply {
            if (flag == takePhoto) {
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(this))
                circleImage.setImageBitmap(rotateIfRequired(bitmap))
            }
            else if(flag == fromAlbum ) {
                val bitmap = getBitmapFromUri(this)
                circleImage.setImageBitmap(bitmap)
            }
        }
        Log.d(TAG, "initHeadImage: $imageUri")
    }

    private fun initImageUri() {
        val string =  pref.getString("imageUri", "")
        string?.apply {
            imageUri = this.toUri()
        }
        flag = pref.getInt("flag", -1)
    }

    private fun saveImageUri(flag: Int) {
        val edit = pref.edit()
        edit.putString("imageUri", imageUri.toString())
        edit.putInt("flag", flag)
        edit.apply()
        Log.d(TAG, "saveImageUri: $imageUri")
    }

    private fun initpvOptions() {
        pvOptions = OptionsPickerBuilder(this) {
                options1, _, _, v -> //点击确定按钮后触发
            val str = sexList[options1]
            sexText.text = str
            sex = str
        }.build()
        pvOptions.setPicker(sexList as List<Any>?)
    }

    private fun startCameraActivity() {
        headImage = File(externalCacheDir, "head_image.jpg")
        if (headImage.exists()) {
            headImage.delete()
        }
        headImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "com.zzp.dtrip." +
                    "fileprovider", headImage)
        } else {
            Uri.fromFile(headImage)
        }
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, takePhoto)
    }

    private fun startAlbumActivity() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, fromAlbum)
    }

    private fun initData() {
        Log.d(TAG, "initData: ${UserInformation.sex}")
        sexText.text = UserInformation.sex
        usernameEdit.setText(UserInformation.username)
        idText.text = UserInformation.ID.toString()
    }

    private fun postUsername() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postUsername(
            UsernameBody(UserInformation.username,
            UserInformation.password, username)
        )
        task.enqueue(object : Callback<NormalResult>{
            override fun onResponse(call: Call<NormalResult>,
                                    response: Response<NormalResult>) {
                response.body()?.apply {
                    if (errorCode == 0) {
                        Log.d(TAG, "onResponse:aaa $sex")
                        Log.d(TAG, "onResponse:bbb ${UserInformation.sex}")
                        UserInformation.username = username
                        if (sex != UserInformation.sex) {
                            postSex()
                        }
                        else {
                            Toast.makeText(this@InformationActivity,
                                "修改成功", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(this@InformationActivity,
                            errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    }

    private fun postSex() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postSex(
            SexBody(UserInformation.username,
                    UserInformation.password, sex)
        )
        Log.d(TAG, "postSex: $sex")
        task.enqueue(object : Callback<NormalResult>{
            override fun onResponse(call: Call<NormalResult>,
                                    response: Response<NormalResult>) {
                response.body()?.apply {
                    if (errorCode == 0) {
                        UserInformation.sex = sex
                        Toast.makeText(this@InformationActivity, "修改成功",
                            Toast.LENGTH_SHORT).show()                    }
                    else {
                        Toast.makeText(this@InformationActivity,
                            errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }
        })
    }
}