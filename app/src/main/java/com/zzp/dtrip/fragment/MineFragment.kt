package com.zzp.dtrip.fragment

import android.Manifest
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.zzp.dtrip.R
import com.zzp.dtrip.activity.*
import com.zzp.dtrip.body.DeleteFaceBody
import com.zzp.dtrip.body.FaceBody
import com.zzp.dtrip.data.FaceResult
import com.zzp.dtrip.data.NormalResult
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

    private lateinit var navPersonView: NavigationView

    private lateinit var controlButton: MaterialButton

    private lateinit var usernameText: TextView
    private lateinit var headImageView: CircleImageView

    private lateinit var refreshReceiver: MyBroadcastReceiver

    private lateinit var pref: SharedPreferences

    private var imageUri: Uri? = null

    private var imageBase64: String = ""

    private var flag = -1

    private val TAG = "MineFragment"

    private val ADD_DATA = 3

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        val root: View = inflater.inflate(R.layout.fragment_mine, container, false)
        findViewById(root)
        doRegisterReceiver()
        initData()
        pref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)

        navPersonView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_personal_info -> {
                    if (UserInformation.isLogin) {
                        val intent = Intent(requireContext(), InformationActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_face -> {
                    if (UserInformation.isLogin) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 1)
                        } else {
                            faceRecognition()
                        }
//                        val intent = Intent("android.media.action.IMAGE_CAPTURE")
//                        startActivityForResult(intent, ADD_DATA)
                    }
                    else {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_delete -> {
                    if (UserInformation.isLogin) {
                        deleteFaceData()
                    }
                    else {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_data -> {
                    if (UserInformation.isLogin) {
                        val intent = Intent(requireContext(), TripDataActivity::class.java)
                        startActivity(intent)
                    }
                    else {
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                    }
                }
                R.id.action_personal_setting -> {
                    val intent = Intent(requireContext(), SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            false
        }

        controlButton.setOnClickListener {
            if (UserInformation.isLogin) {
                controlButton.setTextColor(resources.getColor(R.color.blue))
                controlButton.strokeColor = ColorStateList.valueOf(resources.getColor(R.color.blue))
                controlButton.text = "登录"
                UserInformation.setDataNull()
                Toast.makeText(requireContext(), "您已退出登录", Toast.LENGTH_SHORT).show()
                usernameText.text = "未登录"
                headImageView.setImageResource(R.drawable.ic_head_image)
            }
            else {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }

        return root
    }

    private fun findViewById(root: View) {
        navPersonView = root.findViewById(R.id.nav_person_view)
        controlButton = root.findViewById(R.id.control_button)
        usernameText = root.findViewById(R.id.username_text)
        headImageView = root.findViewById(R.id.head_image)
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
            usernameText.text = UserInformation.username
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
                        Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_SHORT).show()
//                        Toast.makeText(requireContext()
//                            , errorMsg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "onResponse: $errorMessage")
                        
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

    private fun deleteFaceData() {
        val appService = RetrofitManager.create<AppService>()
        val task = appService.deleteFace(DeleteFaceBody(UserInformation.ID))
        task.enqueue(object : Callback<NormalResult> {
            override fun onResponse(call: Call<NormalResult>, response: Response<NormalResult>) {
                response.body()?.apply {
                    Log.d(TAG, "onResponse: ${response.code()} $errorCode")
                    if (isError) {
                        Toast.makeText(requireContext(), "无人脸数据,删除人脸失败!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "删除人脸成功!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<NormalResult>, t: Throwable) {
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