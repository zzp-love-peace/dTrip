package com.zzp.dtrip.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;


import com.zzp.dtrip.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartFragment extends Fragment {

    //private DataMessage dataMessage;
    private List<Integer> list = new ArrayList<Integer>();

    private PieChartView pieChart;     //饼状图View
    private PieChartData data;         //存放数据

    private boolean hasLabels = true;                   //是否有标语
    private boolean hasLabelsOutside = false;           //扇形外面是否有标语
    private boolean hasCenterCircle = true;            //是否有中心圆
    private boolean hasCenterText1 = true;             //是否有中心的文字
    private boolean hasCenterText2 = true;             //是否有中心的文字2
    private boolean isExploded = false;                  //是否是炸开的图像
    private boolean hasLabelForSelected = true;         //选中的扇形显示标语


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.piechart_fragment, container, false);
        pieChart = (PieChartView) view.findViewById(R.id.pieChart);
        //dataMessage = (DataMessage) getArguments().get("data");
        //list = getArguments().getIntegerArrayList("list");
        list = getArguments().getIntegerArrayList("list");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        pieChart.setOnValueTouchListener(new ValueTouchListener());
        generateData();
    }

    /**
     * 配置数据
     */
    private void generateData() {
        //int numValues = 6;   //扇形的数量
        int numValues = list.size();   //扇形的数量

        //存放扇形数据的集合
        /*
        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < numValues; ++i) {
            SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
            values.add(sliceValue);
        }*/

        List<SliceValue> values = new ArrayList<SliceValue>();
        int sum = 0;
        for (int i = 0; i < list.size(); ++i) {
            sum += list.get(i);
        }
        for (int i = 0; i < list.size(); ++i) {
            SliceValue sliceValue = new SliceValue(list.get(i), ChartUtils.pickColor());
            values.add(sliceValue);
        }

        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            //data.setCenterText1("Hello!");
            data.setCenterText1("d伴行");//此处可随意修改，但是为了界面美观，最好不要超过5个字符长度

            // Get roboto-italic font.    //Typeface是用来设置字体的!
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }

        if (hasCenterText2) {
            //data.setCenterText2("Charts (Roboto Italic)");
            data.setCenterText2("D accompanying");

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");

            data.setCenterText2Typeface(tf);
            data.setCenterText2FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text2_size)));
        }

        pieChart.setPieChartData(data);
    }


    /**
     * 点击监听
     */
    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            Toast.makeText(getContext(), "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }

    }
}






