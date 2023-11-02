package com.sts.sontalksign.feature.conversation

import android.content.Context
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.gms.vision.TfLiteVision
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector

class HandSignHelper(val context: Context, val objectDetectorListener: DetectorListener) {

    private var handSign: ObjectDetector? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable: Boolean ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) optionsBuilder.setEnableGpuDelegateSupport(true)
            TfLiteVision.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            objectDetectorListener.onInitialized()
        }.addOnFailureListener {
            objectDetectorListener.onError()
        }
    }

    fun clearObjectDetector() {handSign = null}

    fun setupObjectDetector() {
        if(!TfLiteVision.isInitialized()) {return}

        val optionsBuilder = ObjectDetector.ObjectDetectorOptions.builder()

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(1)

        baseOptionsBuilder.useGpu()

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        val modelName = "ttttttttttttttt.tflite"


    }

    interface DetectorListener {
        fun onInitialized()
        fun onError()
        fun onResults()
    }
}