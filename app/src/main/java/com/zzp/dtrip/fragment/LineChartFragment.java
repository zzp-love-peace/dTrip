package com.zzp.dtrip.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import com.zzp.dtrip.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartFragment extends Fragment {

    private List<Integer> list = new ArrayList<Integer>();

    /**
     * 折线图的相关控件
     */
    private LineChartView lineChart;
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linechart_fragment, container, false);
        lineChart = (LineChartView) view.findViewById(R.id.line_chart);
        list = getArguments().getIntegerArrayList("list");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //折线图
        getAxisLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.BLUE).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑,即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
//      line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true); //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GRAY);    //设置字体颜色
        axisX.setName("里程关于时间的折线图");   //表格名称
        axisX.setTextSize(7);//设置字体大小
        axisX.setMaxLabelChars(7);  //最多几个X轴坐标
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//      data.setAxisXTop(axisX);  //x 轴在顶部

        Axis axisY = new Axis();  //Y轴
        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setName("温度");//y轴标注
        axisY.setTextSize(7);//设置字体大小

        data.setAxisYLeft(axisY);  //Y轴设置在左边
//      data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
    }


    /**
     * X 轴的显示
     */
    /*
    private void getAxisLables() {
        for (int i = 0; i < weeks.length; i++) {
            mAxisValues.add(new AxisValue(i).setLabel(weeks[i]));
        }
    }*/
    private void getAxisLables() {
        for (int i = 0; i < list.size(); i++) {
            mAxisValues.add(new AxisValue(i).setLabel(String.valueOf(i+1)));
        }
    }

    /**
     * 图表的每个点的显示
     */
    /*
    private void getAxisPoints() {
        for (int i = 0; i < weather.length; i++) {
            mPointValues.add(new PointValue(i, weather[i]));
        }
    }*/
    private void getAxisPoints() {
        for (int i = 0; i < list.size(); i++) {
            mPointValues.add(new PointValue(i, list.get(i)));
        }
    }


}
