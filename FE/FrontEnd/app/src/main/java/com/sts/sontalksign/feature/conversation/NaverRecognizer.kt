package com.sts.sontalksign.feature.conversation

import android.content.Context
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.annotation.WorkerThread
import com.naver.speech.clientapi.SpeechConfig
import com.naver.speech.clientapi.SpeechConfig.EndPointDetectType
import com.naver.speech.clientapi.SpeechConfig.LanguageType
import com.naver.speech.clientapi.SpeechRecognitionException
import com.naver.speech.clientapi.SpeechRecognitionListener
import com.naver.speech.clientapi.SpeechRecognitionResult
import com.naver.speech.clientapi.SpeechRecognizer
import com.sts.sontalksign.R

class NaverRecognizer(context: Context, handler: Handler, clientId: String) : SpeechRecognitionListener {

    companion object {
        private const val TAG = "NaverRecognizer"
    }

    private val mHandler: Handler = handler
    private val mRecognizer: SpeechRecognizer

    init {
        mRecognizer = try {
            SpeechRecognizer(context, clientId)
        } catch (e: SpeechRecognitionException) {
            e.printStackTrace()
            throw e
        }

        mRecognizer.setSpeechRecognitionListener(this)
    }

    fun getSpeechRecognizer(): SpeechRecognizer {
        return mRecognizer
    }

    fun recognize() {
        try {
            mRecognizer.recognize(SpeechConfig(LanguageType.KOREAN, EndPointDetectType.AUTO))
        } catch (e: SpeechRecognitionException) {
            e.printStackTrace()
        }
    }

    @WorkerThread
    override fun onInactive() {
        val msg = Message.obtain(mHandler, R.id.clientInactive)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onReady() {
        val msg = Message.obtain(mHandler, R.id.clientReady)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onRecord(speech: ShortArray) {
        val msg = Message.obtain(mHandler, R.id.audioRecording, speech)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onPartialResult(result: String) {
        val msg = Message.obtain(mHandler, R.id.partialResult, result)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetected() {
        Log.d(TAG, "Event occurred: EndPointDetected")
    }

    @WorkerThread
    override fun onResult(result: SpeechRecognitionResult) {
        val msg = Message.obtain(mHandler, R.id.finalResult, result)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onError(errorCode: Int) {
        val msg = Message.obtain(mHandler, R.id.recognitionError, errorCode)
        msg.sendToTarget()
    }

    @WorkerThread
    override fun onEndPointDetectTypeSelected(epdType: EndPointDetectType) {
        val msg = Message.obtain(mHandler, R.id.endPointDetectTypeSelected, epdType)
        msg.sendToTarget()
    }
}
