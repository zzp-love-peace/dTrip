package com.zzp.dtrip.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.*
import android.util.Log
import android.widget.Toast
import com.huawei.hms.mlsdk.sounddect.MLSoundDectConstants
import com.huawei.hms.mlsdk.sounddect.MLSoundDectListener
import com.huawei.hms.mlsdk.sounddect.MLSoundDector
import com.zzp.dtrip.activity.SettingsActivity

class SoundService : Service() {

    private val myBinder = MyBinder()

    private val soundDector = MLSoundDector.createSoundDector()

    private lateinit var mVibrator: Vibrator

    private val TAG = "SoundService"

//   是否震动
    companion object {
        var isVibrate = false
        var text = ""
        //    stopButton是否隐藏
        var buttonFlag = false
        var fabFlag = false
    }

    private val listener: MLSoundDectListener = object : MLSoundDectListener {
        override fun onSoundSuccessResult(result: Bundle) {
            //识别成功的处理逻辑，识别结果为：0-12（对应MLSoundDectConstants.java中定义的以SOUND_EVENT_TYPE开头命名的13种声音类型）。
            when(result.getInt(MLSoundDector.RESULTS_RECOGNIZED)){

                MLSoundDectConstants.SOUND_EVENT_TYPE_LAUGHTER->{
                    Log.d(TAG,"捕捉到笑声")
//                    Toast.makeText(this@SoundService,"捕捉到笑声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_LAUGHTER")
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                    text = "笑声"
                }

                MLSoundDectConstants.SOUND_EVENT_TYPE_BABY_CRY->{
                    Log.d(TAG,"捕捉到婴儿哭声")
//                    Toast.makeText(this@SoundService,"捕捉到婴儿哭声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_BABY_CRY")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "婴儿哭声"
                }

                MLSoundDectConstants.SOUND_EVENT_TYPE_SNORING->{
                    Log.d(TAG,"捕捉到打鼾声")
//                    Toast.makeText(this@SoundService,"捕捉到打鼾声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_SNORING")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "打鼾声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_SNEEZE->{
                    Log.d(TAG,"捕捉到喷嚏声")
//                    Toast.makeText(this@SoundService,"捕捉到喷嚏声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_SNEEZE")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "喷嚏声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_SCREAMING->{
                    Log.d(TAG,"捕捉到叫喊声")
//                    Toast.makeText(this@SoundService,"捕捉到叫喊声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_SCREAMING")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "叫喊声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_MEOW->{
                    Log.d(TAG,"捕捉到猫叫声")
//                    Toast.makeText(this@SoundService,"捕捉到猫叫声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_MEOW")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
//                    playVibrate(2000, 100, true)
                    text = "猫叫声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_BARK->{
                    Log.d(TAG,"捕捉到狗叫声")
//                    Toast.makeText(this@SoundService,"捕捉到狗叫声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_BARK")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "狗叫声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_WATER->{
                    Log.d(TAG,"捕捉到流水声")
//                    Toast.makeText(this@SoundService,"捕捉到流水声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_WATER")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    text = "流水声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_CAR_ALARM->{
                    Log.d(TAG,"捕捉到汽车喇叭声")
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_CAR_ALARM")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    if (!SettingsActivity.switchFlag) {
                        Toast.makeText(this@SoundService,"捕捉到汽车喇叭声", Toast.LENGTH_SHORT).show()
                        playVibrate(1800, 200, true)
                        isVibrate = true
                    }
                    text = "喇叭声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_DOOR_BELL->{
                    Log.d(TAG,"捕捉到门铃声")
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_DOOR_BELL")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    if (SettingsActivity.switchFlag) {
                        playVibrate(1500, 500, true)
                        Toast.makeText(this@SoundService,"捕捉到门铃声", Toast.LENGTH_SHORT).show()
                        isVibrate = true
                    }
                    text = "门铃声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_KNOCK->{
                    Log.d(TAG,"捕捉到敲门声")
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_KNOCK")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    if (SettingsActivity.switchFlag) {
                        playVibrate(1500, 500, true)
                        Toast.makeText(this@SoundService,"捕捉到敲门声", Toast.LENGTH_SHORT).show()
                        isVibrate = true
                    }
                    text = "敲门声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_ALARM->{
                    Log.d(TAG,"捕捉到火警报警声")
                    Toast.makeText(this@SoundService,"捕捉到火警报警声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_ALARM")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    playVibrate(2000, 100, true)
                    isVibrate = true
                    text = "火警报警声"
                }
                MLSoundDectConstants.SOUND_EVENT_TYPE_STEAM_WHISTLE->{
                    Log.d(TAG,"捕捉到警报声")
                    Toast.makeText(this@SoundService,"捕捉到警报声", Toast.LENGTH_SHORT).show()
                    val intent = Intent("com.zzp.SOUND_EVENT_TYPE_STEAM_WHISTLE")
                    intent.setPackage(packageName);
                    sendBroadcast(intent)
                    playVibrate(2000, 100, true)
                    isVibrate = true
                    text = "警报声"
                }
            }
        }
        override fun onSoundFailResult(errCode: Int) {
            var errCodeDesc = ""
            when (errCode) {
                MLSoundDectConstants.SOUND_DECT_ERROR_NO_MEM -> errCodeDesc = "no memory error"
                MLSoundDectConstants.SOUND_DECT_ERROR_FATAL_ERROR -> errCodeDesc = "fatal error"
                MLSoundDectConstants.SOUND_DECT_ERROR_AUDIO -> errCodeDesc = "microphone error"
                MLSoundDectConstants.SOUND_DECT_ERROR_INTERNAL -> errCodeDesc = "internal error"
                else -> {
                }
            }
            Log.e(TAG, "FailResult errCode: " + errCode + "errCodeDesc:" + errCodeDesc)
        }
    }

    inner class MyBinder : Binder() {

        fun start() {
            val startSuccess = soundDector.start(this@SoundService)
            if (startSuccess) {
                Toast.makeText(this@SoundService,"声音识别启动", Toast.LENGTH_LONG).show()
            }
        }

        fun stop() {
            soundDector.stop()
            Toast.makeText(this@SoundService, "声音识别停止", Toast.LENGTH_LONG).show()
        }

        fun stopVibrate() {
            mVibrator.cancel()
            Toast.makeText(this@SoundService, "震动停止", Toast.LENGTH_LONG).show()
            isVibrate = false
        }

        fun getService(): SoundService {
            return this@SoundService
        }
    }

    override fun onCreate() {
        super.onCreate()
        soundDector.setSoundDectListener(listener)//开始识别
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    /**
     * 销毁识别
     */
    override fun onDestroy() {
        super.onDestroy()
        soundDector.destroy()
    }

    private fun playVibrate(time: Long, stop: Long, isRepeat: Boolean) {
        /*
         * 设置震动，用一个long的数组来表示震动状态（以毫秒为单位）
         * 如果要设置先震动1秒，然后停止0.5秒，再震动2秒则可设置数组为：long[]{1000, 500, 2000}。
         * 别忘了在AndroidManifest配置文件中申请震动的权限
         */
        try {
            if (!::mVibrator.isInitialized) {
                mVibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            val patern = longArrayOf(1000, time, stop)
            /**
             * 适配android7.0以上版本的震动
             * 说明：如果发现5.0或6.0版本在app退到后台之后也无法震动，那么只需要改下方的Build.VERSION_CODES.N版本号即可
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) //key
                    .build();
                mVibrator.vibrate(patern, if (isRepeat) 1 else -1, audioAttributes);
            }else {
                mVibrator.vibrate(patern, if (isRepeat) 1 else -1)
            }
        } catch (ex: Exception) {
        }
    }

}