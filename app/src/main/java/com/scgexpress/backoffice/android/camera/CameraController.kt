package com.scgexpress.backoffice.android.camera

import android.content.Context
import android.graphics.Matrix
import android.os.Handler
import android.os.HandlerThread
import android.util.*
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.scgexpress.backoffice.android.common.pxToDp

open class CameraController constructor(
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val textureView: TextureView
) {

    private var lensFacing = CameraX.LensFacing.BACK
    protected lateinit var onImageAnalysis: (imageAnalysis: ImageAnalysis) -> Unit

    fun bind(onImageAnalysis: (imageAnalysis: ImageAnalysis) -> Unit) {
        this.onImageAnalysis = onImageAnalysis
    }

    fun stopCamera() {
        CameraX.unbindAll()
    }

    fun startCamera() = with(textureView) {

        val screenSize = Size(width, height)
        val screenAspectRatio = Rational(1, 1)

        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
            setTargetAspectRatio(screenAspectRatio)
            setTargetRotation(textureView.display.rotation)
            //setTargetRotation(windowManager.defaultDisplay.rotation)
        }.build()

        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = parent as ViewGroup
            parent.removeView(this)
            parent.addView(this, 0)
            surfaceTexture = it.surfaceTexture
            updateTransform(textureView)
        }

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {

            val analyzerThread = HandlerThread(
                    "LuminosityAnalysis"
            ).apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))

            setImageReaderMode(
                    ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE
            )
        }.build()

        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            //            analyzer = LuminosityAnalyzer()
            onImageAnalysis(this)
        }

        textureView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform(textureView)
        }

        CameraX.bindToLifecycle(lifecycleOwner, preview, analyzerUseCase)
    }


    fun updateTransform(textureView: TextureView) {
        val matrix = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        textureView.setTransform(matrix)

        val w = textureView.width.pxToDp(context).toInt()
        val h = textureView.height.pxToDp(context).toInt()

        updateTextureMatrix(textureView, w, h)
    }

    private fun updateTextureMatrix(textureView: TextureView, width: Int, height: Int) {

        val ratioSurface = (1.0 * width / height)
        val ratioPreview = (1.0 * width / height)

        val (scaleX, scaleY) = if (ratioSurface > ratioPreview) {
            (1.0 * height / height) to 1.0
        } else {
            1.0 to (1.0 * height / height)
        }

        val matrix = Matrix()

        matrix.setScale(scaleX.toFloat(), scaleY.toFloat())
        textureView.setTransform(matrix)

        val scaledWidth = width * scaleX
        val scaledHeight = height * scaleY

        val dx = (width - scaledWidth) / 2
        val dy = (height - scaledHeight) / 2
        textureView.translationX = dx.toFloat()
        textureView.translationY = dy.toFloat()
    }
}
