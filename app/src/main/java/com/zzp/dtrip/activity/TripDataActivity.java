package com.zzp.dtrip.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.zzp.dtrip.R;
import com.zzp.dtrip.adapter.DataAdapter;
import com.zzp.dtrip.data.DataMessage;
import com.zzp.dtrip.fragment.ColumnChartFragment;
import com.zzp.dtrip.fragment.LineChartFragment;
import com.zzp.dtrip.fragment.PieChartFragment;
import com.zzp.dtrip.util.UserInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TripDataActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private DataMessage dataMessage;
    private List<Integer> list = new ArrayList<Integer>();
    private List<DataMessage.DataDTO> data = new ArrayList<DataMessage.DataDTO>();

    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private RadioButton miles;
    private RadioButton transportation;
    private RadioButton total;

    //这俩表示标签
    private TextView textView1;
    private TextView textView2;
    //这俩表示标签对应的数据
    private TextView textView3;
    private TextView textView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_data);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }//隐藏顶部导航栏
        // 经测试在代码里直接声明透明状态栏更有效
        //不过我使用后效果不太好，不建议使用
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }*/
        sendRequestWithOkHttp(UserInformation.INSTANCE.getID());

    }

    private void sendRequestWithOkHttp(int Id) {
        //开启新线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder().build();
                JsonObject json = new JsonObject();
                json.addProperty("id", Id);
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, String.valueOf(json));
                final Request request = new Request.Builder()
                        .url("http://101.34.85.209:5240/data/selectById")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.e("Graph", response.toString());
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    dataMessage = gson.fromJson(responseData, DataMessage.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (dataMessage.getErrorCode() == 0 && !dataMessage.getError())
                    showResponse(dataMessage);
                else showErrorResponse();
            }
        }).start();
    }

    private void showResponse(final DataMessage dataMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作，将结果显示在界面上
                //这里本来想重写Fragment的构造函数用来传参的，但是Fragment是用反射的方式来创建的，重写构造函数会报错。所以是用bundle来传参数。
                Bundle bundle = new Bundle();
                //bundle.putSerializable("dataMessage", dataMessage);

                recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
                textView1 = (TextView) findViewById(R.id.textView3);
                textView2 = (TextView) findViewById(R.id.textView5);
                textView3 = (TextView)findViewById(R.id.textView4);
                textView4 = (TextView) findViewById(R.id.textView6);
                radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
                radioGroup.setOnCheckedChangeListener(TripDataActivity.this);
                //获取第一个单选按钮，并设置其为选中状态
                miles = (RadioButton) findViewById(R.id.miles);
                miles.setChecked(true);
                data = dataMessage.getList();
                initView();

                LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                DataAdapter adapter = new DataAdapter(data);
                recyclerView.setAdapter(adapter);
                //NestedScrollingEnabled(false);


            }
        });
    }

    private void showErrorResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //在这里进行UI操作，将结果显示在界面上
                Toast.makeText(TripDataActivity.this, "网络连接错误", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.miles:
                textView1.setText("总里程");
                textView2.setText("平均里程");
                int sum = 0;
                for (int i = 0; i < data.size(); i++) sum += data.get(i).getMileage();
                textView3.setText("" + sum);
                if (data.size() == 0)
                    textView4.setText("" + 0);
                else
                    textView4.setText("" + sum / data.size());
                Bundle bundle = new Bundle();
                list.clear();
                //data = dataMessage.getList();
                bundle.putIntegerArrayList("list", (ArrayList<Integer>) list);
                Fragment fragment_pie = new PieChartFragment();
                Fragment fragment_column = new ColumnChartFragment();
                Fragment fragment_line = new LineChartFragment();
                fragment_pie.setArguments(bundle);
                fragment_column.setArguments(bundle);
                fragment_line.setArguments(bundle);

                replaceFragment(R.id.fragment_columnChart, fragment_column);
                replaceFragment(R.id.fragment_lineChart, fragment_line);
                replaceFragment(R.id.fragment_pieChart, fragment_pie);
                for (int i = 0; i < data.size(); i++) {
                    list.add(data.get(i).getMileage());
                }
                break;
            case R.id.transportation:
                textView1.setText("种类");
                textView2.setText("最常用");
                textView3.setText("5");
                textView4.setText("地铁");

                list.clear();
                data = dataMessage.getList();
                for (int i = 0; i < 10; i++)
                    list.add(0);
                for (int i = 0; i < data.size(); i++) {
                    int x;
                    switch (data.get(i).getType()) {
                        case "公交":
                            x = list.get(0);
                            list.set(0, ++x);
                            break;
                        case "汽车":
                            x = list.get(1);
                            list.set(1, ++x);
                            break;
                        case "飞机":
                            x = list.get(2);
                            list.set(2, ++x);
                            break;
                        case "火车":
                            x = list.get(3);
                            list.set(3, ++x);
                            break;
                        case "轮船":
                            x = list.get(4);
                            list.set(4, ++x);
                            break;
                        case "电动车":
                            x = list.get(5);
                            list.set(5, ++x);
                            break;
                        case "高铁":
                            x = list.get(6);
                            list.set(6, ++x);
                            break;
                        case "自行车":
                            x = list.get(7);
                            list.set(7, ++x);
                            break;
                        case "地铁":
                            x = list.get(8);
                            list.set(8, ++x);
                            break;
                        default:
                            x = list.get(9);
                            list.set(9, ++x);
                            break;

                    }

                }
                Bundle bundle2 = new Bundle();
                bundle2.putIntegerArrayList("list", (ArrayList<Integer>) list);
                Fragment fragment_pie2 = new PieChartFragment();
                Fragment fragment_column2 = new ColumnChartFragment();
                Fragment fragment_line2 = new LineChartFragment();
                fragment_pie2.setArguments(bundle2);
                fragment_column2.setArguments(bundle2);
                fragment_line2.setArguments(bundle2);
                replaceFragment(R.id.fragment_columnChart, fragment_column2);
                replaceFragment(R.id.fragment_lineChart, fragment_line2);
                replaceFragment(R.id.fragment_pieChart, fragment_pie2);

                break;

            case R.id.total:
                textView1.setText("总里程");
                textView2.setText("平均");
                break;
            default:
                break;

        }

    }

    private void replaceFragment(int layout, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(layout, fragment);
        transaction.commit();
    }

    private void initView(){
        textView1.setText("总里程");
        textView2.setText("平均里程");
        int sum = 0;
        for (int i = 0; i < data.size(); i++) sum += data.get(i).getMileage();
        textView3.setText("" + sum);
        if (data.size() == 0)
            textView4.setText("" + 0);
        else
            textView4.setText("" + sum / data.size());
        Bundle bundle = new Bundle();
        list.clear();
        //data = dataMessage.getList();
        bundle.putIntegerArrayList("list", (ArrayList<Integer>) list);
        Fragment fragment_pie = new PieChartFragment();
        Fragment fragment_column = new ColumnChartFragment();
        Fragment fragment_line = new LineChartFragment();
        fragment_pie.setArguments(bundle);
        fragment_column.setArguments(bundle);
        fragment_line.setArguments(bundle);

        replaceFragment(R.id.fragment_columnChart, fragment_column);
        replaceFragment(R.id.fragment_lineChart, fragment_line);
        replaceFragment(R.id.fragment_pieChart, fragment_pie);
        for (int i = 0; i < data.size(); i++) {
            list.add(data.get(i).getMileage());
        }
    }
}