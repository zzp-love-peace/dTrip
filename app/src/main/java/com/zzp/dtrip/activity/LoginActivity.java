package com.zzp.dtrip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.zzp.dtrip.R;
import com.zzp.dtrip.util.HttpBeanService;
import com.zzp.dtrip.javabean.LoginAsk;
import com.zzp.dtrip.javabean.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Retrofit retrofit;
    private HttpBeanService httpBeanService;
    private Button button_login;
    private TextView textview_account;
    private TextView textview_password;
    private CheckBox checkbox_password;
    private TextView text_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button_login = findViewById(R.id.entry_button);
        button_login.setOnClickListener(this::onClick);
        textview_account = findViewById(R.id.account_layout);
        textview_password = findViewById(R.id.password_layout);
        checkbox_password = findViewById(R.id.remember_button);
        text_register = findViewById(R.id.register_text);
        text_register.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remember_button:
                if (checkbox_password.isChecked());
                break;
            case R.id.entry_button:
                Login(textview_account.getText().toString(), textview_password.getText().toString());
                break;
            case R.id.register_text:

                break;
        }

    }

    private void Login(String account, String password) {
        LoginAsk ask = new LoginAsk();
        ask.setAccount(account);
        ask.setPassword(password);
        retrofit = new Retrofit.Builder().baseUrl("http://101.34.85.209.5240/user/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        httpBeanService = retrofit.create(HttpBeanService.class);
        Call<LoginResponse> call = httpBeanService.login(ask);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getCode() == 0) {
                    //登录成功
                } else {
                    Toast.makeText(LoginActivity.this, "账号或密码错误，请重试!", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }
}