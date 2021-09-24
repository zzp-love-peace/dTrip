package com.zzp.dtrip.view;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.huawei.hms.mlsdk.gesture.MLGesture;
import com.zzp.dtrip.util.TtsUtil;

import java.util.List;
/**
 * 手势识别所使用的View,不需要改动
 */

public class HandGestureGraphic extends GraphicOverlay.Graphic {

    private final List<MLGesture> results;

    private Paint circlePaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint rectPaint;
    private final Rect rect;

    public HandGestureGraphic(GraphicOverlay overlay, List<MLGesture> results) {
        super(overlay);

        this.results = results;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.YELLOW);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(5f);
        textPaint.setTextSize(100);

        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4f);
        linePaint.setAntiAlias(true);

        rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(5f);
        rectPaint.setAntiAlias(true);

        rect = new Rect();

    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < results.size(); i++) {
            MLGesture mlGesture = results.get(i);

            canvas.drawRect(rect, rectPaint);

            Rect rect = translateRect(mlGesture.getRect());
            if (rect.right < rect.left) {
                int x = rect.left;
                rect.left = rect.right;
                rect.right = x;
            }
            canvas.drawRect(rect, linePaint);
            // 注意。如果绘制时坐标点需要与原图一一对应，需要使用translateX和translateY进项坐标转换
            canvas.drawText(getChineseDescription(mlGesture.getCategory()),
                    translateX((mlGesture.getRect().left + mlGesture.getRect().right) / 2f),
                    translateY((mlGesture.getRect().top + mlGesture.getRect().bottom) / 2f),
                    textPaint);

        }

    }


    private String getChineseDescription(int gestureCategory) {
        String chineseDescription;
        switch (gestureCategory) {
            case MLGesture.ONE:
                chineseDescription = "数字1";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.SECOND:
                chineseDescription = "数字2";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.THREE:
                chineseDescription = "数字3";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.FOUR:
                chineseDescription = "数字4";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.FIVE:
                chineseDescription = "数字5";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.SIX:
                chineseDescription = "数字6";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.SEVEN:
                chineseDescription = "数字7";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.EIGHT:
                chineseDescription = "数字8";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.NINE:
                chineseDescription = "数字9";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.DISS:
                chineseDescription = "差评";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.FIST:
                chineseDescription = "握拳";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.GOOD:
                chineseDescription = "点赞";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.HEART:
                chineseDescription = "单手比心";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            case MLGesture.OK:
                chineseDescription = "确认";
                TtsUtil.INSTANCE.playString(chineseDescription);
                break;
            default:
                chineseDescription = "其他手势";
                break;

        }
        return chineseDescription;
    }

    public Rect translateRect(Rect rect) {
        float left = translateX(rect.left);
        float right = translateX(rect.right);
        float bottom = translateY(rect.bottom);
        float top = translateY(rect.top);
        if (left > right) {
            float size = left;
            left = right;
            right = size;
        }
        if (bottom < top) {
            float size = bottom;
            bottom = top;
            top = size;
        }
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }
    public void play(String chineseDescription){
        new Thread(new Runnable(){
            @Override
            public void run() {
                TtsUtil.INSTANCE.playString(chineseDescription);
            }
        }).start();
        try {
            Thread.sleep(1000); //设置手势识别采样间隔（语音播报时间间隔）
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}