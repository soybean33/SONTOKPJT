package com.sts.sontalksign.feature.conversation

import android.app.Activity
import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class HandSignHelper() {

    val compatList = CompatibilityList()

    val options = Interpreter.Options().apply{
        if(compatList.isDelegateSupportedOnThisDevice){
            // if the device has a supported GPU, add the GPU delegate
            val delegateOptions = compatList.bestOptionsForThisDevice
            this.addDelegate(GpuDelegate(delegateOptions))
        } else {
            // if the GPU is not supported, run on 4 threads
            this.setNumThreads(4)
        }
    }

//    private val model = getTfliteInterpreter(this,"mymodel.tflite")
//
//    val interpreter = Interpreter(model, options)
//
//    // Run inference
//    writeToInput(input)
//    interpreter.run(input, output)
//    readFromOutput(output)
//
//    private fun getTfliteInterpreter(modelPath: String) : Interpreter{
//        return Interpreter(loadModelFile(this, modelPath))
//    }
//
//    private fun loadModelFile(context: Context, modelPath: String): ByteBuffer {
//        val assetFileDescriptor = context.assets.openFd(modelPath)
//        val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
//        val channel = inputStream.channel
//
//        val startOffset = assetFileDescriptor.startOffset
//        val declaredLength = assetFileDescriptor.declaredLength
//
//        return channel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//    }
}
