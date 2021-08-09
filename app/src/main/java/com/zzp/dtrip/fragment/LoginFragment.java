package com.zzp.dtrip.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tencentmap.mapsdk.maps.model.VisibleRegion;
import com.zzp.dtrip.R;
import com.zzp.dtrip.activity.MainActivity;
import com.zzp.dtrip.util.javabean.HttpBeanService;
import com.zzp.dtrip.util.javabean.login;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragment extends Fragment implements View.OnClickListener {

    private Retrofit retrofit;
    private HttpBeanService httpBeanService;
    private View root;
    private Button button_login;
    private TextView textview_account;
    private TextView textview_password;
    private CheckBox checkbox_password;
    private Button button_register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button_login=root.findViewById(R.id.entry_button);
        textview_account=root.findViewById(R.id.account_layout);
        textview_password=root.findViewById(R.id.password_layout);
        checkbox_password=root.findViewById(R.id.remember_button);
        button_register=root.findViewById( R.id.register_button);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            root = inflater.inflate(R.layout.fragment_login, container, false);
            return root;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.remember_button:if(checkbox_password.isChecked());break;
            case R.id.account_layout:Login(textview_account.getText().toString(),textview_password.getText().toString());break;
            case R.id.register_button:break;
        }


    }

    private void Login(String account,String password) {
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                        "{\n" +
                                "    \"username\": \""+account+"\",\n" +
                                "    \"password\": \""+password+"\"\n" +
                                "}");
        retrofit = new Retrofit.Builder().baseUrl("http://127.0.0.1:8080/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        httpBeanService = retrofit.create(HttpBeanService.class);
        Call<login> call = httpBeanService.login(requestBody);
        call.enqueue(new Callback<login>() {
            @Override
            public void onResponse(Call<login> call, Response<login> response) {
                if(response.body().getCode()==0)
                {
                    //登录成功
                }
                else
                {
                    Toast.makeText(getContext(),"账号或密码错误，请重试!",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<login> call, Throwable t) {

            }
        });


    }
}