package com.zzp.dtrip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zzp.dtrip.R;
import com.zzp.dtrip.util.HttpBeanService;
import com.zzp.dtrip.javabean.RegisterAsk;
import com.zzp.dtrip.javabean.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView accountTextview;
    private TextView passwordTextview;
    private TextView password2Textview;
    private Button registerButton;
    private RadioButton manButton;
    private RadioButton womenButton;
    private Retrofit retrofit;
    private HttpBeanService httpBeanService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        accountTextview =findViewById(R.id.account_register);
        passwordTextview =findViewById(R.id.password_register);
        password2Textview =findViewById(R.id.password2_register);
        registerButton=findViewById(R.id.register_button);
        registerButton.setOnClickListener(this::onClick);
        manButton=findViewById(R.id.register_man);
        manButton.setOnClickListener(this::onClick);
        womenButton=findViewById(R.id.register_women);
        womenButton.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.register_button:register();break;
            case R.id.register_man:manButton.setChecked(true);womenButton.setChecked(false);break;
            case  R.id.register_women:manButton.setChecked(false);womenButton.setChecked(true);break;
        }

    }

    private void register() {
        String account = accountTextview.getText().toString();
        String password = null;
        String sex = "0";
        if (password2Textview.getText().toString() == passwordTextview.getText().toString())
            password = passwordTextview.getText().toString();
        else {
            Toast.makeText(RegisterActivity.this, "密码不一致，请重试", Toast.LENGTH_SHORT).show();

        }

        if (manButton.isChecked() == true)
            sex = "0";
        if (womenButton.isChecked() == true)
            sex = "1";
        RegisterAsk ask = new RegisterAsk();
        ask.setAccount(account);
        ask.setPassword(password);
        ask.setSex(sex);
        retrofit = new Retrofit.Builder().baseUrl("http://101.34.85.209.5240/user/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        httpBeanService = retrofit.create(HttpBeanService.class);
        Call<RegisterResponse> call = httpBeanService.register(ask);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {

            }
        });
    }

    }
