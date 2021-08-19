package com.zzp.dtrip.fragment

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.InformationActivity
import com.zzp.dtrip.activity.LoginActivity
import com.zzp.dtrip.activity.ReplaceActivity
import com.zzp.dtrip.activity.TripDataActivity
import com.zzp.dtrip.body.FaceBody
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.util.AppService
import com.zzp.dtrip.util.RetrofitManager
import com.zzp.dtrip.util.UserInformation
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

class MineFragment : Fragment() {

    private lateinit var informationLayout: LinearLayout
    private lateinit var faceLayout: LinearLayout
    private lateinit var tripLayout: LinearLayout
    private lateinit var replaceLayout: LinearLayout

    private lateinit var switchMaterial: SwitchMaterial

    private lateinit var controlButton: MaterialButton

    private lateinit var prefs: SharedPreferences

    private lateinit var unLogin: TextView
    private lateinit var usernameText: TextView
    private lateinit var idText: TextView
    private lateinit var headImageView: CircleImageView

    private lateinit var refreshReceiver: MyBroadcastReceiver

    private lateinit var pref: SharedPreferences

    private var imageUri: Uri? = null

    private var imageBase64: String = ""

    private var flag = -1

    private val TAG = "MineFragment"

    private val ADD_DATA = 3

    companion object {
        var switchFlag = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_mine, container, false)
        findViewById(root)
        initPrefAndSwitch()
        doRegisterReceiver()
        initData()
        pref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        informationLayout.setOnClickListener {
            if (UserInformation.isLogin) {
                val intent = Intent(requireContext(), InformationActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        faceLayout.setOnClickListener {
            if (UserInformation.isLogin) {
                val intent = Intent("android.media.action.IMAGE_CAPTURE")
                startActivityForResult(intent, ADD_DATA)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        tripLayout.setOnClickListener {
            if (UserInformation.isLogin) {
                val intent = Intent(requireContext(), TripDataActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        replaceLayout.setOnClickListener {
            if (UserInformation.isLogin) {
                val intent = Intent(requireContext(), ReplaceActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        controlButton.setOnClickListener {
            if (UserInformation.isLogin) {
                controlButton.setTextColor(resources.getColor(R.color.blue))
                controlButton.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
                controlButton.text = "登录"
                UserInformation.setDataNull()
                Toast.makeText(requireContext(), "您已退出登录", Toast.LENGTH_SHORT).show()
                usernameText.visibility = View.GONE
                idText.visibility = View.GONE
                unLogin.visibility = View.VISIBLE
                headImageView.setImageResource(R.drawable.ic_head_image)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        switchMaterial.setOnCheckedChangeListener { buttonView, isChecked ->
            switchFlag = isChecked
            saveSwitchFlag()
        }
        return root
    }

    private fun findViewById(root: View) {
        informationLayout = root.findViewById(R.id.information_layout)
        faceLayout = root.findViewById(R.id.face_layout)
        tripLayout= root.findViewById(R.id.trip_layout)
        replaceLayout = root.findViewById(R.id.replace_layout)
        switchMaterial = root.findViewById(R.id.switch_material)
        controlButton = root.findViewById(R.id.control_button)
        unLogin = root.findViewById(R.id.unlogin_text)
        usernameText = root.findViewById(R.id.username_text)
        idText = root.findViewById(R.id.id_text)
        headImageView = root.findViewById(R.id.head_image)
    }

    private fun initPrefAndSwitch() {
        prefs = requireActivity().getPreferences(Context.MODE_PRIVATE)
        switchFlag = prefs.getBoolean("switch", false)
        switchMaterial.isChecked = switchFlag
        Log.d(TAG, "initPrefAndSwitch: ")
    }

    private fun saveSwitchFlag() {
        val edit = prefs.edit()
        edit.putBoolean("switch", switchFlag)
        edit.apply()
    }

    private fun doRegisterReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.zzp.LOGIN_SUCCESS")
        refreshReceiver = MyBroadcastReceiver()
        requireContext().registerReceiver(refreshReceiver, intentFilter)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.zzp.LOGIN_SUCCESS" -> {
                    initData()
                }
            }
        }
    }

    private fun initData() {
        if (UserInformation.isLogin) {
            controlButton.setTextColor(resources.getColor(R.color.red))
            controlButton.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.red))
            controlButton.text = "退出登录"
            unLogin.visibility = View.GONE
            usernameText.visibility = View.VISIBLE
            idText.visibility = View.VISIBLE
            usernameText.text = "用户名:   ${UserInformation.username}"
            idText.text = "ID:   ${UserInformation.ID}"
        }
    }
    
    override fun onResume() {
        super.onResume()
        initData()
        initImageUri()
        initHeadImage()
    }

    private fun initImageUri() {
        val string =  pref.getString("imageUri", "")
        string?.apply {
            imageUri = this.toUri()
        }
        flag = pref.getInt("flag", -1)
    }

    private fun initHeadImage() {
        if (UserInformation.isLogin) {
            Log.d(TAG, "initHeadImage: $imageUri")
            imageUri?.apply {
                if (flag == 1) {
                    val bitmap = BitmapFactory.decodeStream(requireContext().
                    contentResolver.openInputStream(this))
                    headImageView.setImageBitmap(rotateIfRequired(bitmap))
                }
                else if(flag == 2 ) {
                    val bitmap = getBitmapFromUri(this)
                    headImageView.setImageBitmap(bitmap)
                }
            }
            Log.d(TAG, "initHeadImage: $imageUri")
        }
    }

    private fun getBitmapFromUri(uri: Uri) = requireContext().contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val headImage = File(requireContext().externalCacheDir, "head_image.jpg")
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    faceRecognition()
                } else {
                    Toast.makeText(requireContext(), "您需要开启权限",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun faceRecognition() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, ADD_DATA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_DATA  && resultCode == Activity.RESULT_OK) {
            var imageBitmap = data?.extras?.get("data") as Bitmap
            imageBitmap = compressImage(imageBitmap)
            val byteArrayOutputStream = ByteArrayOutputStream()
            byteArrayOutputStream.use {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                val imageByteArray = it.toByteArray()
                imageBase64 = Base64.encodeToString(imageByteArray, Base64.DEFAULT)
                Log.d(TAG, imageBase64)
            }
            postFaceData()
        }
    }

    private fun postFaceData() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.postFaceData(FaceBody(imageBase64, UserInformation.ID))
        task.enqueue(object : Callback<FaceResult> {
            override fun onResponse(call: Call<FaceResult>,
                response: Response<FaceResult>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                response.body()?.apply {
                    if (isError) {
                        Snackbar.make(faceLayout, errorMsg, Snackbar.LENGTH_SHORT).show()
//                        Toast.makeText(requireContext()
//                            , errorMsg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $errorMsg")
                        
                    } else {
                        Toast.makeText(requireContext()
                            , "人脸录入成功",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(TAG, "onResponse: success")
                    }
                }
            }

            override fun onFailure(call: Call<FaceResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
                Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun compressImage(image :Bitmap) : Bitmap{
        val matrix = Matrix()
        val w = image.width
        val h = image.height
        val true_width = 540.0f
        val true_height = true_width * h / w
        if (true_width >w) return image
        val wsx = true_height/w
        matrix.setScale(wsx,wsx)
        return Bitmap.createBitmap(image,0,0,w,h,matrix,true)
    }
}