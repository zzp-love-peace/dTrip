package com.zzp.dtrip.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Base64
import android.widget.Toast
import java.io.ByteArrayOutputStream

//图片压缩
fun compressImage(image : Bitmap) : Bitmap {
    val matrix = Matrix()
    val w = image.width
    val h = image.height
    val true_width = 120.0f
    val true_height = true_width * h / w
    if (true_width >w) return image
    val wsx = true_height/w
    matrix.setScale(wsx,wsx)
    return Bitmap.createBitmap(image,0,0,w,h,matrix,true)
}

//bitmap转base64
fun bitmap2Base64(image: Bitmap): String{
    val byteArrayOutputStream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100,
        byteArrayOutputStream)
    byteArrayOutputStream.flush()
    byteArrayOutputStream.close()
    val imageByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(imageByteArray, Base64.DEFAULT)
}

fun showUserWrong(s: String, context: Context) {
    Toast.makeText(context, s, Toast.LENGTH_SHORT).show()
}
