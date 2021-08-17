package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.DEFAULT
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zzp.dtrip.R
import java.io.ByteArrayOutputStream

class TakePictureActivity:AppCompatActivity() {
    private val REQUEST_CODE_TAKE_PICTURE = 1 //请求码，可以自己定义
    private lateinit var bytes:ByteArray
    private var base64:String = ""


    /**
     * 现阶段主要需要解决，如何把该活动巧妙的修改为，可以类似于工具类的存在
      */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_takepicture_layout)//用于测试的布局，最后请记得删除
        findViewById<Button>(R.id.get_picture).setOnClickListener {//获取刚才拍取图片
            val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)//将拍得的图片转为字节后再转成bitmap,再放置出来，目的是为了说明在图片在转字节过程中图片并没有发生损坏
            findViewById<ImageView>(R.id.decode_picture).setImageBitmap(bitmap)
        }
        findViewById<Button>(R.id.get_base64).setOnClickListener {//获取base64编码
            Toast.makeText(this@TakePictureActivity,base64,Toast.LENGTH_SHORT).show()
        }
        startFunction()
    }


    private fun startFunction():String?{
        if(ContextCompat.checkSelfPermission(this,//动态申请权限
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
            return null
        }else{
            takePictureToBytes()
            return base64
        }
    }

   private fun takePictureToBytes(){
        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(openCameraIntent,REQUEST_CODE_TAKE_PICTURE)//开启拍照活动
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_TAKE_PICTURE->{//将拍照的结果转为字节串
                val photo: Bitmap = data?.getParcelableExtra("data")!!//尚未清楚原理
                val baos = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.JPEG,100,baos)//将bitmap以清晰度为100的jpg格式，输出到baos中
                findViewById<ImageView>(R.id.last_picture).setImageBitmap(photo)//可以设置将某个ImageView改为拍照所发图片
                bytes = baos.toByteArray()
                base64  = Base64.encodeToString(bytes, DEFAULT)
            }
        }
    }


    fun getBase64String():String{
        return base64
    }

}