package com.zzp.dtrip.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

class TakePictureActivity:AppCompatActivity() {
    private val REQUEST_CODE_TAKE_PICTURE = 1 //请求码，可以自己定义
    private lateinit var bytes:ByteArray
    private var base64:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startFunction()
    }


    private fun startFunction(){
        if(ContextCompat.checkSelfPermission(this,//动态申请权限
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1)
        }else{
            takePictureToBytes()
        }
    }

   private fun takePictureToBytes(){
        val openCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(openCameraIntent,REQUEST_CODE_TAKE_PICTURE)//开启拍照事件
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_TAKE_PICTURE->{//将拍照的结果转为字节串
                val photo: Bitmap = data?.getParcelableExtra("data")!!//尚未清楚原理
                val baos = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.JPEG,100,baos)//将bitmap以清晰度为100的jpg格式，输出到baos中
//                image.setImageBitmap(photo)//可以设置将某个ImageView改为拍照所发图片
                bytes = baos.toByteArray()
                base64  = Base64.encodeToString(bytes, DEFAULT)
            }
        }
    }


    fun getBase64String():String{
        return base64
    }


}