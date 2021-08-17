package com.zzp.dtrip.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.HardwareRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zzp.dtrip.R;
import com.zzp.dtrip.javabean.LoginResponse;
import com.zzp.dtrip.javabean.compareFaceAsk;
import com.zzp.dtrip.javabean.compareFaceResponse;
import com.zzp.dtrip.util.AppService;
import com.zzp.dtrip.util.UserInformation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FaceLoginActivity extends AppCompatActivity {

    private ImageView facePhoto;
    private Button loginButton;
    private Button takePhoto;
    private Retrofit retrofit;
//    private Uri imageUri;
    private AppService api;

    /**
     * 人脸登录逻辑待修改（控件也可修改），人脸增加细节（如重复录入人脸）可后续增加
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);
        facePhoto= findViewById(R.id.face_image);
        loginButton= findViewById(R.id.face_entry);
        takePhoto=findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File   faceImage=new File(getExternalCacheDir(),"face_image.jpg");
                try {
                    if(faceImage.exists()){
                        faceImage.delete();
                    }

                    faceImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24)
                {
//                    imageUri= Uri.fromFile(faceImage);
                }
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,1);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {


            /**
             * 缺少对照片的判空处理
             * @param v
             */
            @Override
            public void onClick(View v) {
                facePhoto.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(facePhoto.getDrawingCache());
                facePhoto.setDrawingCacheEnabled(false);
                String faceString = bitmapToBase64(bitmap);
                compareFaceAsk ask =new compareFaceAsk();
                ask.setBytes(faceString);
                ask.setId(UserInformation.INSTANCE.getID());
                retrofit=new Retrofit.Builder().baseUrl("http://101.34.85.209:5240/")
                        .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                        .build();
                api = retrofit.create(AppService.class);
                if(UserInformation.INSTANCE.isLogin()) {//已经登录的情况下，绑定人脸
                    api.addFace(ask).enqueue(new Callback<compareFaceResponse>() {
                        @Override
                        public void onResponse(Call<compareFaceResponse> call, Response<compareFaceResponse> response) {
                            if(response.body().getCode() == 0)
                            Toast.makeText(FaceLoginActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FaceLoginActivity.this,"绑定失败，你可能已经绑定了人脸！",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<compareFaceResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                }
                else {//在未登录的状况下，通过人脸识别登录
                    api.compareFace(ask).enqueue(new Callback<compareFaceResponse>() {//注意此时需要更改ask的值，因为接口提供了一个不该存在的id
                        @Override
                        public void onResponse(Call<compareFaceResponse> call, Response<compareFaceResponse> response) {//待处理登录后逻辑，如修改UserInfo中的属性，设置登录
                            // 状态等，可能需要将JavaBean进行修改
                            //TODO(“请在此处写出登录成功后需要的逻辑,测试其他功能请删除此处”)
                            Toast.makeText(FaceLoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();//状态码异常，可能需要修改
                            finish();

                        }
                        @Override
                        public void onFailure(Call<compareFaceResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                }

            }

        });

    }
    public static String bitmapToBase64(Bitmap bitmap){//转格式
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);//将bitmap以清晰度为100的jpg格式，输出到baos中
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = data.getParcelableExtra("data");//尚未清楚原理
                        facePhoto.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }
}