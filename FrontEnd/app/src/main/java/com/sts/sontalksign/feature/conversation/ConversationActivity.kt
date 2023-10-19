package com.sts.sontalksign.feature.conversation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.feature.common.CustomForm
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ConversationActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityConversationBinding.inflate(layoutInflater)
    }

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //카메라 권한 요청
        if(allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        
        //이벤트 리스너 설정
        binding.btnStopConversation.setOnClickListener { stopConversation() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun takePhoto() {

    }

    private fun captureVideo() {

    }

    private fun startCamera() {
        //Activity와 카메라의 수명 주기를 binding
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            //카메라의 수명 주기를 APP 프로세스 내의 LifecycleOwner에 바인딩
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //카메라 프리뷰 초기화 및 설정
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.pvCamera.surfaceProvider)
                }

            //전면 카메라를 기본으로 선택
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                //바인딩된 항목 전체 제거
                cameraProvider.unbindAll()

                //카메라 관련 객체 바인딩
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
            } catch(exc: Exception) {
                //앱에 더이상 포커스 없는 경우 등의 실패 케이스 처리
                Log.e(TAG, "Use case binding failed", exc)
            }
            
            //기본 스레드에서 실행되는 Executor를 반환
        }, ContextCompat.getMainExecutor(this))
    }

    //대화 종료 처리 함수
    private fun stopConversation() {
        //팝업을 띄운다
        val cForm = CustomForm(this)
        cForm.setOnBtnStoreClickedListener {
        }

        //팝업 종료시 액티비티 종료
        cForm.show("저장합니다")
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {

        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if(allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraX Preview"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}