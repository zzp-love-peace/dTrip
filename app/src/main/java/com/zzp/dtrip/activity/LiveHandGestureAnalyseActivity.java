package com.zzp.dtrip.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.gesture.MLGesture;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzer;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzerFactory;
import com.huawei.hms.mlsdk.gesture.MLGestureAnalyzerSetting;
import com.zzp.dtrip.R;
import com.zzp.dtrip.view.GraphicOverlay;
import com.zzp.dtrip.view.HandGestureGraphic;
import com.zzp.dtrip.view.LensEnginePreview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LiveHandGestureAnalyseActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LiveHandGestureAnalyseActivity.class.getSimpleName();

    private LensEnginePreview mPreview;

    private GraphicOverlay mOverlay;

    private Button mFacingSwitch;

    private MLGestureAnalyzer mAnalyzer;

    private LensEngine mLensEngine;

    private int lensType = LensEngine.BACK_LENS;

    private int mLensType;

    private boolean isFront = false;

    private boolean isPermissionRequested;

    private static final int CAMERA_PERMISSION_CODE = 0;

    private static final String[] ALL_PERMISSION =
            new String[]{
                    Manifest.permission.CAMERA,
            };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_handkeypoint_analyse);
        if (savedInstanceState != null) {
            mLensType = savedInstanceState.getInt("lensType");
        }
        initView();
        createHandAnalyzer();
        if (Camera.getNumberOfCameras() == 1) {
            mFacingSwitch.setVisibility(View.GONE);
        }
        // Checking Camera Permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            createLensEngine();
        } else {
            checkPermission();
        }
    }


    private void initView() {
        mPreview = findViewById(R.id.hand_preview);
        mOverlay = findViewById(R.id.hand_overlay);
        mFacingSwitch = findViewById(R.id.hand_switch);
        mFacingSwitch.setOnClickListener(this);
    }


    private void createHandAnalyzer() {
        // Create a  analyzer. You can create an analyzer using the provided customized face detection parameter: MLHandKeypointAnalyzerSetting
        MLGestureAnalyzerSetting setting =
                new MLGestureAnalyzerSetting.Factory()
                        .create();
        mAnalyzer = MLGestureAnalyzerFactory.getInstance().getGestureAnalyzer(setting);
        mAnalyzer.setTransactor(new HandAnalyzerTransactor(this, mOverlay));
    }

    // Check the permissions required by the SDK.
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            for (String perm : getAllPermission()) {
                if (PackageManager.PERMISSION_GRANTED != this.checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                }
            }

            if (!permissionsList.isEmpty()) {
                requestPermissions(permissionsList.toArray(new String[0]), 0);
            }
        }
    }

    public static List<String> getAllPermission() {
        return Collections.unmodifiableList(Arrays.asList(ALL_PERMISSION));
    }

    private void createLensEngine() {
        Context context = this.getApplicationContext();
        // Create LensEngine.
        mLensEngine = new LensEngine.Creator(context, mAnalyzer)
                .setLensType(this.mLensType)
                .applyDisplayDimension(640, 480)
                .applyFps(25.0f)
                .enableAutomaticFocus(true)
                .create();
    }

    private void startLensEngine() {
        if (this.mLensEngine != null) {
            try {
                this.mPreview.start(this.mLensEngine, this.mOverlay);
            } catch (IOException e) {
                Log.e(TAG, "启动镜头引擎失败", e);

                this.mLensEngine.release();
                this.mLensEngine = null;
            }
        }
    }

    // Permission application callback.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean hasAllGranted = true;
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.createLensEngine();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    showWaringDialog();
                } else {
                    Toast.makeText(this, "请授予相机权限！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("lensType", this.lensType);
        super.onSaveInstanceState(outState);
    }

    private static class HandAnalyzerTransactor implements MLAnalyzer.MLTransactor<MLGesture> {
        private GraphicOverlay mGraphicOverlay;

        HandAnalyzerTransactor(LiveHandGestureAnalyseActivity mainActivity, GraphicOverlay ocrGraphicOverlay) {
            this.mGraphicOverlay = ocrGraphicOverlay;
        }

        /**
         * 处理分析器返回的结果
         */
        @Override
        public void transactResult(MLAnalyzer.Result<MLGesture> result) {
            this.mGraphicOverlay.clear();

            SparseArray<MLGesture> handGestureSparseArray = result.getAnalyseList();
            List<MLGesture> list = new ArrayList<>();
            for (int i = 0; i < handGestureSparseArray.size(); i++) {
                list.add(handGestureSparseArray.valueAt(i));
            }
            HandGestureGraphic graphic = new HandGestureGraphic(this.mGraphicOverlay, list);
            this.mGraphicOverlay.add(graphic);
        }


        @Override
        public void destroy() {
            this.mGraphicOverlay.clear();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hand_switch:
                switchCamera();
                break;
            default:
                break;
        }
    }

    private void switchCamera() {
        isFront = !isFront;
        if (this.isFront) {
            mLensType = LensEngine.FRONT_LENS;
        } else {
            mLensType = LensEngine.BACK_LENS;
        }
        if (this.mLensEngine != null) {
            this.mLensEngine.close();
        }
        this.createLensEngine();
        this.startLensEngine();
    }


    private void showWaringDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("获取相关权限失败，请您在设置中打开相机权限")
                .setPositiveButton("Go Authorization", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setOnCancelListener(dialogInterface);
        dialog.setCancelable(false);
        dialog.show();
    }

    static DialogInterface.OnCancelListener dialogInterface = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            createLensEngine();
            startLensEngine();
        } else {
            checkPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mLensEngine != null) {
            this.mLensEngine.release();
        }
        if (this.mAnalyzer != null) {
            this.mAnalyzer.stop();
        }
    }


}
