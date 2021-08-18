package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceView
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer
import com.zzp.dtrip.R
import java.io.IOException

class FaceLoginActivity : AppCompatActivity() {

    private lateinit var analyzer: MLFaceAnalyzer
    private lateinit var lensEngine: LensEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_login)

        analyzer = MLAnalyzerFactory.getInstance().faceAnalyzer
        analyzer.setTransactor(FaceAnalyzerTransactor())
        lensEngine = LensEngine.Creator(applicationContext, analyzer)
            .setLensType(LensEngine.BACK_LENS)
            .applyDisplayDimension(1440, 1080)
            .applyFps(30.0f)
            .enableAutomaticFocus(true)
            .create()
        // 请自行实现SurfaceView控件的其他逻辑。
        val mSurfaceView: SurfaceView? = findViewById(R.id.surface_view)
        try {
            lensEngine.run(mSurfaceView!!.holder)
        } catch (e: IOException) {
            // 异常处理逻辑。
        }
    }

    class FaceAnalyzerTransactor : MLAnalyzer.MLTransactor<MLFace?> {
        override fun transactResult(results: MLAnalyzer.Result<MLFace?>) {
            val items = results.analyseList
            // 开发者根据需要处理识别结果，需要注意，这里只对检测结果进行处理。
            // 不可调用ML Kit提供的其他检测相关接口。
        }

        override fun destroy() {
            // 检测结束回调方法，用于释放资源等。
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            analyzer.stop()
        } catch (e: IOException) {
            // 异常处理。
        }
        lensEngine.release()
    }
}