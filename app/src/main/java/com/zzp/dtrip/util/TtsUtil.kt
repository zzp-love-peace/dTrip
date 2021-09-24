package com.zzp.dtrip.util

import android.media.MediaPlayer
import android.os.Build
import android.util.Base64.DEFAULT
import android.util.Log
import androidx.annotation.RequiresApi
import com.huawei.sis.bean.AuthInfo
import com.huawei.sis.bean.SisConfig
import com.huawei.sis.bean.SisConstant
import com.huawei.sis.bean.request.TtsCustomRequest
import com.huawei.sis.client.TtsCustomizationClient
import com.huawei.sis.exception.SisException
import com.huawei.sis.util.JsonUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Base64

object TtsUtil {
    private val DEFAULT_PITCH = 0
    private val DEFAULT_SPEED = 0
    private val DEFAULT_VOLUME = 50

    /**
     * 华为身份认证配置，请不要随意修改
     */
    private val ak = "UUGGCX3AW5VDFHTBEM0P"
    private val sk = "Lkfb2IswOB9nKfHqHVjnfDVrYx8Ey8b5ePL4MEp5"
    private val region = "cn-north-4" // 区域，如cn-north-1、cn-north-4

    private val projectId =
        "0d3151146e00f3082fbfc00673a0ebfa" // 项目id。登录管理控制台，鼠标移动到右上角的用户名上，在下拉列表中选择我的凭证个人设置。在我的凭证个人设置页面，可以查看用户名、帐号名，选择“项目列表”页签，在项目列表中查看项目。多项目时，展开“所属区域”，从“项目ID”列获取子项目ID。


    private lateinit var text: String // 待合成的文本

    //private val path = "D:/test.wav" // 设置本地音频保存路径.可选择不保存到本地。需具体到文件，如D:/test.wav

    private fun setParameter(request: TtsCustomRequest) {
        // 设置语音格式，可选MP3，pcm等，默认wav
        request.audioFormat = "wav"
        // 音高，[-500, 500], 默认0
        request.pitch = DEFAULT_PITCH
        // 语速，[-500, 500]，默认0
        request.speed = DEFAULT_SPEED
        // 音量，[0, 100]，默认50
        request.volume = DEFAULT_VOLUME
        // 当前支持8000和16000，默认8000
        request.sampleRate = "8000"
        // 设置property，特征字符串，{language}_{speaker}_{domain}
        request.property = "chinese_xiaoyu_common"

        // 设置返回数据是否保存，默认不保存。若保存，则需要设置一下保存路径，如D:/1.wav
        request.isSaved = false
//        request.savePath = path
    }


    /**
     * 定义config，所有参数可选，设置超时时间等。
     *
     * @return SisConfig
     */
    private fun getConfig(): SisConfig? {
        val config = SisConfig()
        // 设置连接超时，默认10000ms
        config.connectionTimeout = SisConstant.DEFAULT_CONNECTION_TIMEOUT
        // 设置读取超时，默认10000ms
        config.readTimeout = SisConstant.DEFAULT_READ_TIMEOUT
        // 设置代理, 一定要确保代理可用才启动此设置。 代理初始化也可用不加密的代理，new ProxyHostInfo(host, port);
        // ProxyHostInfo proxy = new ProxyHostInfo(host, port, username, password);
        // config.setProxy(proxy);
        return config
    }

    /**
     * play the String you provide with mediaPlayer
     *
     */


    public fun playString(string: String) {
        Thread {
            try {
                text = string
                // 1. 初始化TtsCustomizationClient
                // 定义authInfo，根据ak，sk，region, projectId.
                val authInfo = AuthInfo(ak, sk, region, projectId)
                // 定义config，所有参数可选，设置超时时间。
                val config = getConfig()
                // 根据authInfo和config，构造TtsCustomizationClient
                val tts = TtsCustomizationClient(authInfo, config)

                // 2. 配置请求
                val request = TtsCustomRequest(text)
                // 设置参数，所有参数均可选，如果要保存合成音频文件，需要在request设置
                setParameter(request)

                // 3. 发送请求，获取响应。具体结果可通过response.getXX获取。
                val response = tts.getTtsResponse(request)
                base64StringPlayer(response.result.data)
                Log.d("Test", JsonUtils.obj2Str(response, true))
            } catch (e: SisException) {
                e.printStackTrace()
                println(
                    """
                error_code:${e.errorCode}
                error_msg:${e.errorMsg}
                """.trimIndent()
                )
            }
        }.start()
    }

    /**
     * 播放base64字符文件
     */
    private fun base64StringPlayer(base64Str: String) {
        val byte = android.util.Base64.decode(base64Str, DEFAULT)
        val mediaPlayer = MediaPlayer()
        val tempMp3 = File.createTempFile("kurchina", "wav")//创建临时文件，以wav模式播放
        tempMp3.deleteOnExit()//需要考虑删除临时文件问题？
        val fileOutputStream = FileOutputStream(tempMp3)
        fileOutputStream.write(byte)
        fileOutputStream.close()
        val fileInputStream = FileInputStream(tempMp3)
        mediaPlayer.setDataSource(fileInputStream.fd)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

}