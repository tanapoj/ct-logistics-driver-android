package com.scgexpress.backoffice.android.camera

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import android.view.TextureView
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class QrScannerController constructor(
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val textureView: TextureView
) : CameraController(context, lifecycleOwner, textureView) {

    var notifySoundWhenScaned: Boolean = true

    private val _imageSize: MutableLiveData<Pair<Int, Int>> = MutableLiveData()
    val imageSize: LiveData<Pair<Int, Int>>
        get() = _imageSize

    private val _scanCode: MutableLiveData<String> = MutableLiveData()
    val scanCode: LiveData<String>
        get() = _scanCode

    var lastSoundNotifyTimestamp = 0L

    init {
        bind {
            it.analyzer = LuminosityAnalyzer()
        }
    }

    private fun soundNotify() {
        val currentTimestamp = System.currentTimeMillis()
        val soundLength = TimeUnit.SECONDS.toMillis(2)
        if (currentTimestamp - lastSoundNotifyTimestamp >= soundLength && notifySoundWhenScaned) {
            ToneGenerator(AudioManager.STREAM_MUSIC, 100).run {
                startTone(ToneGenerator.TONE_PROP_BEEP, soundLength.toInt())
            }
            lastSoundNotifyTimestamp = System.currentTimeMillis()
        }
    }

    private fun setCode(code: String) {
        soundNotify()
        ThreadUtils.runOnUiThread {
            _scanCode.value = code
        }
    }

    private fun setImageSize(width: Int, height: Int) {
        ThreadUtils.runOnUiThread {
            _imageSize.value = width to height
        }
    }

    private inner class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()
            val data = ByteArray(remaining())
            get(data)
            return data
        }

        override fun analyze(image: ImageProxy, rotationDegrees: Int) {
            try {
                val currentTimestamp = System.currentTimeMillis()

                val width = image.width
                val height = image.height

                setImageSize(width, height)

                if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
                    val buffer = image.planes[0].buffer
                    val data = buffer.toByteArray()
                    val pixels = data.map { it.toInt() and 0xFF }

                    var contents = decode(pixels, width, height, "ori-0")
                    if (contents != null) {
                        setCode(contents)
                    } else {
                        contents = decode(t(pixels, width, height), height, width, "rotate-90")
                        if (contents != null) {
                            setCode(contents)
                        }
                    }
                    lastAnalyzedTimestamp = currentTimestamp
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("aaa", "$e")
            }
        }

        private fun decode(pixels: List<Int>, width: Int, height: Int, debug: String = ""): String? {

            val source: LuminanceSource = RGBLuminanceSource(width, height, pixels.toIntArray())
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val reader: Reader = MultiFormatReader()
            try {
                val result = reader.decode(binaryBitmap)
                //Log.i("aaa", "QrScannerController.decode $debug $result:: $width x $height, ${pixels.size}")
                return result.text
            } catch (e: NotFoundException) {
                e.printStackTrace();
            } catch (e: ChecksumException) {
                e.printStackTrace();
            } catch (e: FormatException) {
                e.printStackTrace();
            }
            return null
        }

        private fun <T> t(arr: List<T>, w: Int, h: Int) = List(w * h) { i ->
            val start = w - 1 - (i / h)
            val offset = i % h
            arr[start + offset * w]
        }


    }
}
