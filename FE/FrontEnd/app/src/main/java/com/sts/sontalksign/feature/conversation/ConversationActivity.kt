package com.sts.sontalksign.feature.conversation

import ConversationCameraAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.naver.speech.clientapi.SpeechRecognitionResult
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.feature.apis.NaverAPI
import com.sts.sontalksign.feature.common.CustomForm
import com.sts.sontalksign.feature.utils.AudioWriterPCM
import com.sts.sontalksign.global.FileFormats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.lang.IllegalStateException
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConversationActivity : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener, HandLandmarkerHelper.LandmarkerListener {
    companion object {
        private const val TAG: String = "ConversationActivity"
        private const val cTAG = "CameraX Preview"
        private const val hlTAG = "Hand Landmarker"
        private const val pTAG = "Pose Landmarker"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        /** 카메라 및 오디오 권한 */
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

    /** ActivityConversationBinding 초기화 (이미 있는 코드) */
    private val binding by lazy {
        ActivityConversationBinding.inflate(layoutInflater)
    }

    /** MediaPipe 관련 변수 - PoseLandmarker, HandLandmarker */
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private lateinit var handLandmarkerHelper: HandLandmarkerHelper
    private val viewModel by lazy {
        ViewModelProvider(this@ConversationActivity).get(MainViewModel::class.java)
    }
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
//    private var imagePoseAnalyzer: ImageAnalysis? = null
//    private var imageHandAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundBothExecutor: ExecutorService
//    private lateinit var backgroundPoseExecutor: ExecutorService
//    private lateinit var backgroundHandExecutor: ExecutorService

    /** CameraX 관련 변수 */
//    private var imageCapture: ImageCapture? = null
//    private var videoCapture: VideoCapture<Recorder>? = null
//    private var recording: Recording? = null
//    private lateinit var cameraExecutor: ExecutorService

    /** 사용자의 "녹음하기" 선택 여부 */
    private var isNowRecording: Boolean = false

    /** MediaPlayer 관련 변수(Naver Clova TTS API) */
    private lateinit var mediaPlayer: MediaPlayer

    /** naverspeech-sdk-android(CLOVA Speech Recognition(CSR)) */
    private val CLIENT_ID = "89kna7451i"
    private var handler: RecognitionHandler? = null
    private var naverRecognizer: NaverRecognizer? = null
    private var mResult: String? = null
    private var audioWriter: AudioWriterPCM? = null

    /** 내부저장소 - txt 파일 */
    private var directory: String? = null
    private var convFilename: String? = null //대화내용 파일명
    private var currentTime: Long? = null

    private lateinit var textList: String
    private lateinit var conversationCameraAdapter: ConversationCameraAdapter
    private val conversationCamera: ArrayList<ConversationCameraModel> = ArrayList()
    private lateinit var recyclerView: RecyclerView // RecyclerView 선언

    /**  CSR 상태에 대한 동작, clientReady, audioRecording, partialResult, final Result, recognitionError, clientInactive */
    private fun handleMessage(msg: Message) {
        when (msg.what) {
            R.id.clientReady -> {
                binding.tvCRS.text = "Connected"
                audioWriter = AudioWriterPCM(
                    filesDir.absolutePath + "/NaverSpeechTest")
                audioWriter!!.open("Test")
            }
            R.id.audioRecording -> audioWriter?.write(msg.obj as ShortArray)
            R.id.partialResult -> {
                mResult = msg.obj as String
                binding.tvCRS.text = mResult
            }
            R.id.finalResult -> {
                val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                val results = speechRecognitionResult.results

//                val strBuf = StringBuilder()
//                for (result in results) {
//                    strBuf.append(result)
//                    strBuf.append("\n")
//                }
//                mResult = strBuf.toString()
//                binding.tvCRS.text = mResult

                handleSTTResult(results[0].toString(), false) // STT 결과를 RecyclerView에 추가

                binding.tvCRS.text = results[0].toString()
            }
            R.id.recognitionError -> {
                audioWriter?.close()
                mResult = "Error code : ${msg.obj}"
                binding.tvCRS.text = mResult
                binding.btnCRS.setText(R.string.str_start)
                binding.btnCRS.isEnabled = true
            }
            R.id.clientInactive -> {
                audioWriter?.close()
                binding.btnCRS.setText(R.string.str_start)
                binding.btnCRS.isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /** RecyclerView 초기화 */
        recyclerView = binding.rvCameraConversation
        recyclerView.layoutManager = LinearLayoutManager(this)
        conversationCameraAdapter = ConversationCameraAdapter(conversationCamera)
        recyclerView.adapter = conversationCameraAdapter
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(conversationCameraAdapter.itemCount - 1)

        /** 텍스트 입력 이벤트 처리 */
        binding.etTextConversation.setOnEditorActionListener { textView, actionId, _ ->
            var handled = false
            //완료버튼 클릭에만 처리
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                var inpContent= textView.text.toString()
                addTextLine(inpContent, false)
                binding.etTextConversation.setText("")

                generateTtsApi(inpContent) //TTS 적용 - 텍스트를 음성 출력

            }
            handled
        }

        /** "대화 종료" 버튼 클릭 */
        binding.btnStopConversation.setOnClickListener { stopConversation() }

        /** 대화내용 저장 */
        /** 대화 시작 - "녹음하기" 여부 저장 */
        isNowRecording = intent.getBooleanExtra("isRecord", false)

        /** 내부저장소의 경로 저장 */
        directory = filesDir.absolutePath //내부경로의 절대 경로
        createTextFile() //대화 텍스트 파일 생성

        /** TTS 초기 설정 */
        /** 음성 출력을 위한 MediaPlayer 초기 설정 */
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )

        /** CSR 관련 처리 */
        handler = RecognitionHandler(this)
        naverRecognizer = NaverRecognizer(this, handler!!, CLIENT_ID)
        binding.btnCRS.setOnClickListener {
            if (!naverRecognizer!!.getSpeechRecognizer().isRunning) {
                // Start button is pushed when SpeechRecognizer's state is inactive.
                // Run SpeechRecongizer by calling recognize().
                mResult = ""
                binding.tvCRS.text = "Connecting..."
                binding.btnCRS.setText(R.string.str_stop)
                naverRecognizer!!.recognize()


            } else {
                Log.d(TAG, "stop and wait Final Result")
                binding.tvCRS.isEnabled = false
                naverRecognizer!!.getSpeechRecognizer().stop()
            }
        }


        /** 카메라 권한 요청 */
        if(allPermissionsGranted()) {

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        /** MediaPipe 관련 초기 설정 */
        // Initialize our background executor
        backgroundBothExecutor = Executors.newFixedThreadPool(2)
//        backgroundPoseExecutor = Executors.newSingleThreadExecutor()
//        backgroundHandExecutor = Executors.newSingleThreadExecutor()

        binding.pvCamera.post {
            setUpCamera()
        }

        backgroundBothExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = viewModel.currentMinPoseDetectionConfidence,
                minPoseTrackingConfidence = viewModel.currentMinPoseTrackingConfidence,
                minPosePresenceConfidence = viewModel.currentMinPosePresenceConfidence,
                currentDelegate = viewModel.currentPoseDelegate,
                poseLandmarkerHelperListener = this
            )
        }

        backgroundBothExecutor.execute {
            handLandmarkerHelper = HandLandmarkerHelper(
                context = this,
                runningMode = RunningMode.LIVE_STREAM,
                minHandDetectionConfidence = viewModel.currentMinHandDetectionConfidence,
                minHandTrackingConfidence = viewModel.currentMinHandTrackingConfidence,
                minHandPresenceConfidence = viewModel.currentMinHandPresenceConfidence,
                maxNumHands = viewModel.currentMaxHands,
                currentDelegate = viewModel.currentHandDelegate,
                handLandmarkerHelperListener = this
            )
        }
    }

    /**
     * TTS API 요청
     * line: 입력으로 들어온 문장
     */
    private fun generateTtsApi(line: String) {

        //API 요청을 위한 스레드 생성
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = NaverAPI.create().generateSpeech("nsujin", 0, 0,0, "mp3", line).execute() //API 요청

                if(response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        //응답 결과를 임시 저장
                        val resFile = File.createTempFile("res", "mp3", cacheDir)
                        val outputStream = FileOutputStream(resFile)

                        val input = body.byteStream()
                        val buffer = ByteArray(1024)
                        var bytesRead: Int

                        while(input.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }

                        outputStream.close()

                        //MediaPlayer로 음성 출력
                        mediaPlayer.reset()
                        mediaPlayer.setDataSource(resFile.path)
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                        // Add the TTS result to the RecyclerView
                        runOnUiThread {
                            handleTTSResult(line, true)
                        }
                    }
                } else {
                    val errorBody = response.errorBody()
                    if(errorBody != null) {
                        Log.d(TAG, "Error: " + errorBody.string())
                    }
                }
            } catch(e : java.lang.Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    //대화 내용 기록
    private fun createTextFile() {
        //녹음하기 미선택의 경우
        if(!isNowRecording) return

        //내부 저장소 경로
        currentTime = System.currentTimeMillis() //ms로 반환
        directory = filesDir.absolutePath //내부경로의 절대 경로
        convFilename = FileFormats.dataFormat.format(currentTime) + ".txt"

        textList = ""
    }

    private fun getMyConversation(content:String, time:String) : String {
        return getString(R.string.my_conversation_content) + content + getString(R.string.my_conversation_time) + time
    }

    private fun getYourConversation(content:String, time:String) : String {
        return getString(R.string.your_conversation_content) + content + getString(R.string.your_conversation_time) + time
    }

    //isMine - 0:나의 대사, 1:상대의 대사
    private fun addTextLine(content:String, isMine:Boolean) {
        //녹음하기 미선택의 경우
        if(!isNowRecording) return

        var bContent = String()
        currentTime = System.currentTimeMillis()
        when(isMine) {
            true -> bContent = getMyConversation(content, FileFormats.timeFormat.format(currentTime))
            false -> bContent = getYourConversation(content, FileFormats.timeFormat.format(currentTime))
        }

        textList += bContent

    }

    fun handleTTSResult(ttsResult: String, isMine: Boolean) {

        val currentTime = System.currentTimeMillis()
        val conversationCameraModel = ConversationCameraModel(
            ConversationText = ttsResult,
            ConversationTime = FileFormats.timeFormat.format(currentTime),
            isLeft = isMine
        )
        conversationCameraAdapter.addItemAndScroll(conversationCameraModel, recyclerView)
    }


    // STT 결과 생성 시 호출되는 메서드
    fun handleSTTResult(sttResult: String, isMine: Boolean) {

        val currentTime = System.currentTimeMillis()
        val conversationCameraModel = ConversationCameraModel(
            ConversationText = sttResult,
            ConversationTime = FileFormats.timeFormat.format(currentTime),
            isLeft = isMine
        )
        conversationCameraAdapter.addItemAndScroll(conversationCameraModel, recyclerView)
    }


    // RecyclerView를 스크롤하는 코드
    fun scrollToLatestItem() {
        val itemCount = conversationCameraAdapter.itemCount
        if (itemCount > 0) {
            val targetPosition = itemCount - 1
            if (targetPosition >= 0) {
                recyclerView.post {
                    try {
                        recyclerView.smoothScrollToPosition(targetPosition)
                    } catch (e: IllegalArgumentException) {
                        showErrorMessage("Scroll error: $e")
                    }
                }
            } else {
                // 대상 위치가 유효하지 않을 때는 대신 스크롤하지 않음
                showErrorMessage("Invalid target position")
            }
        } else {
            // 아무 아이템도 없을 때는 대신 스크롤하지 않음
            showErrorMessage("No items in the RecyclerView")
        }
    }

    // 스크롤 대상 위치가 업데이트될 때 RecyclerView를 스크롤
    fun updateAndScrollToLatestItem() {
        conversationCameraAdapter.notifyDataSetChanged()
        scrollToLatestItem()
    }
    // 오류 메시지를 표시하는 메서드
    fun showErrorMessage(message: String) {
        // 오류 메시지를 사용자에게 표시하거나 다른 조치를 취하십시오.
        // 예를 들어, Toast 메시지를 표시할 수 있습니다.
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    //파일 쓰기
    private fun writeTextFile(result: String) {
        val dir = File(directory)

        //파일 미존재 시 디렉토리 및 파일 생성
        if(!dir.exists()) {
            dir.mkdirs()
        }

        //파일의 full path
        val writer = FileWriter(directory + "/" + convFilename, true)

        //쓰기 속도 향상
        val buffer = BufferedWriter(writer)
        buffer.write(result)
        buffer.close()
    }

    //TODO: 삭제 예정
    //파일 읽기 - ConversationActivity에서는 미사용
    private fun readTextFile(fullpath: String) : String {
        val file = File(fullpath)

        //파일 미존재
        if(!file.exists()) return ""

        val reader = FileReader(file)
        val buffer = BufferedReader(reader)

        var line:String? = ""
        var result = StringBuffer()

        while(true) {
            line = buffer.readLine() //줄단위로 read
            if(line == null) break
            else result.append(line).append("\n")
        }

        buffer.close()
        return result.toString()
    }

    //대화 종료 처리 함수
    private fun stopConversation() {
        Log.d(TAG, "stopConversation() START")

        //녹음하기 선택 시 - 팝업 발생 및 대화 내용 저장
        if(isNowRecording) {
            val cForm = CustomForm(this)
            cForm.show()
            cForm.setOnBtnStoreClickedListener(object: CustomForm.onBtnStoreClickedListener {
                override fun onBtnStoreClicked(title: String, tags: String) {
                    val rConversation = title + "\nTAGS_" + tags + "\n" + textList //{제목\n태그인덱스\n대화내용} 형식
                    writeTextFile(rConversation) //대화 종료 전 기록에 쌓인 대화 내용을 저장
                    finish()
                }
            })
        }
        else { //녹음하기 미선택 시 - "대화 종료" 질의 팝업 발생
            //TODO: : "대화를 종료하시겠습니까?" 팝업 생성 및 발생
        }
    }

    /*CameraX 관련 함수*/
    private fun takePhoto() {

    }

    private fun captureVideo() {

    }

    //카메라 시작
    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this@ConversationActivity) //Activity와 카메라의 수명 주기를 binding

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get() //카메라의 수명 주기를 APP 프로세스 내의 LifecycleOwner에 바인딩

            bindCameraUseCases() //Camera Use Cases 빌드 및 바인딩
        }, ContextCompat.getMainExecutor(this)) //기본 스레드에서 실행되는 Executor를 반환
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        //카메라 프리뷰 초기화 및 설정
        //CameraProvider
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build() //전면 카메라를 기본으로 선택

        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.pvCamera.display.rotation)
            .build()
//            .also {
//                it.setSurfaceProvider(binding.pvCamera.surfaceProvider)
//            }

//        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.pvCamera.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(backgroundBothExecutor) {image ->
                        mediaPipeSequence(image)
                        //detectBoth(image)
//                        detectPose(image)
//                        detectHand(image)
                    }
                }

//        imagePoseAnalyzer =
//            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .setTargetRotation(binding.pvCamera.display.rotation)
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//                // The analyzer can then be assigned to the instance
//                .also {
//                    it.setAnalyzer(backgroundPoseExecutor) {image ->
//                        detectPose(image)
//                    }
//                }
//
//        imageHandAnalyzer =
//            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
//                .setTargetRotation(binding.pvCamera.display.rotation)
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//                // The analyzer can then be assigned to the instance
//                .also {
//                    it.setAnalyzer(backgroundHandExecutor) { image ->
//                        detectHand(image)
//                    }
//                }

        cameraProvider.unbindAll()

        try {
            //카메라 관련 객체 바인딩
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.pvCamera.surfaceProvider)

//            cameraProvider!!.unbindAll() //바인딩된 항목 전체 제거


//            cameraProvider!!.bindToLifecycle(
//                this, cameraSelector, preview
//            )
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc) //앱에 더이상 포커스 없는 경우 등의 실패 케이스 처리
        }
    }

    private suspend fun mediaPipe(imageProxy: ImageProxy) = coroutineScope {


        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()


        launch {
            detectPose(imageProxy, bitmapBuffer, frameTime)
        }

        launch {
            detectHand(imageProxy, bitmapBuffer, frameTime)
        }
    }

    private suspend fun mediaPipeProcess() = coroutineScope {
        launch {

        }
    }


    private fun mediaPipeSequence(imageProxy: ImageProxy) = runBlocking {
        mediaPipe(imageProxy)

        mediaPipeProcess()
    }

    private fun detectBoth(imageProxy: ImageProxy) {

        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer =
            Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        detectPose(imageProxy, bitmapBuffer, frameTime)
        detectHand(imageProxy, bitmapBuffer, frameTime)

/*        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                bitmapBuffer = bitmapBuffer,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT,
                frameTime = frameTime
            )
        }

        if(this::handLandmarkerHelper.isInitialized) {
            handLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                bitmapBuffer = bitmapBuffer,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT,
                frameTime = frameTime
            )
        }*/

//        val job = CoroutineScope(Dispatchers.Default).launch {
//
//        }
//
//        runBlocking {
//            job.join()
//        }

//        if(this::poseLandmarkerHelper.isInitialized) {
//            poseLandmarkerHelper.detectLiveStream(
//                imageProxy = imageProxy,
//                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
//            )
//        }
//
//        if(this::handLandmarkerHelper.isInitialized) {
//            handLandmarkerHelper.detectLiveStream(
//                imageProxy = imageProxy,
//                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
//            )
//        }
    }

    private fun detectPose(imageProxy: ImageProxy, bitmapBuffer: Bitmap, frameTime: Long) {
        try {
            if(this::poseLandmarkerHelper.isInitialized) {
                poseLandmarkerHelper.detectLiveStream(
                    imageProxy = imageProxy,
                    bitmapBuffer = bitmapBuffer,
                    isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT,
                    frameTime = frameTime
                )
            }

        } catch (exec: Exception) {
            Log.d("detectPose: ", exec.message.toString())
        }
    }

    private fun detectHand(imageProxy: ImageProxy, bitmapBuffer: Bitmap, frameTime: Long) {
        try {
            if(this::handLandmarkerHelper.isInitialized) {
                handLandmarkerHelper.detectLiveStream(
                    imageProxy = imageProxy,
                    bitmapBuffer = bitmapBuffer,
                    isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT,
                    frameTime = frameTime
                )
            }
        } catch (exec: Exception) {
            Log.d("detectHand: ", exec.message.toString())
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            binding.pvCamera.display.rotation

//        imagePoseAnalyzer?.targetRotation =
//            binding.pvCamera.display.rotation
//
//        imageHandAnalyzer?.targetRotation =
//            binding.pvCamera.display.rotation
    }

    // Update UI after pose have been detected. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView
    override fun onPoseResults(
        resultBundle: PoseLandmarkerHelper.ResultBundle
    ) {
        this?.runOnUiThread {
            if (binding != null) {
                Log.d("TEST-Pose", resultBundle.results.first().toString())

                //TODO: landmarks().size() 가 1이상인지 조건 확인 필요
//                if(resultBundle.results.first().landmarks()[0].size >= 1) {
//                    Log.d("TEST-Pose-", resultBundle.results.first().landmarks()[0].size.toString())
//                }

//                Log.d("TEST-", resultBundle.results.first().handednesses().toString())
//                binding.bottomSheetLayout.inferenceTimeVal.text =
//                    String.format("%d ms", resultBundle.inferenceTime)
//
//                // Pass necessary information to OverlayView for drawing on the canvas
                binding.poseOverlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                binding.poseOverlay.invalidate()
            }
        }
    }

    override fun onPoseError(error: String, errorCode: Int) {
        this?.runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            if (errorCode == PoseLandmarkerHelper.GPU_ERROR) {
//                binding.bottomSheetLayout.spinnerDelegate.setSelection(
//                    PoseLandmarkerHelper.DELEGATE_CPU, false
//                )
            }
        }
    }

    // Update UI after hand have been detected. Extracts original
    // image height/width to scale and place the landmarks properly through
    // OverlayView
    override fun onHandResults(
        resultBundle: HandLandmarkerHelper.ResultBundle
    ) {
        this.runOnUiThread {
            if (binding != null) {
                Log.d("TEST-Hand", resultBundle.results.first().toString())
                Log.d("TEST-Hand-handed", resultBundle.results.first().handednesses().toString())

//                binding.bottomSheetLayout.inferenceTimeVal.text =
//                    String.format("%d ms", resultBundle.inferenceTime)

                // Pass necessary information to OverlayView for drawing on the canvas
                binding.handOverlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                // Force a redraw
                binding.handOverlay.invalidate()
            }
        }
    }

    override fun onHandError(error: String, errorCode: Int) {
        this.runOnUiThread {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            if (errorCode == HandLandmarkerHelper.GPU_ERROR) {
//                binding.pvCamera.spinnerDelegate.setSelection(
//                    HandLandmarkerHelper.DELEGATE_CPU, false
//                )
            }
        }
    }



    //카메라 권한
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    //카메라 권한 요청 처리
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {

        if(requestCode == REQUEST_CODE_PERMISSIONS) {
            if(allPermissionsGranted()) {
                setUpCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
//        cameraExecutor.shutdown()
        mediaPlayer.release()

        // Shut down our background executor
        backgroundBothExecutor.shutdown()
        backgroundBothExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )

//        backgroundPoseExecutor.shutdown()
//        backgroundPoseExecutor.awaitTermination(
//            Long.MAX_VALUE, TimeUnit.NANOSECONDS
//        )
//
//        backgroundHandExecutor.shutdown()
//        backgroundHandExecutor.awaitTermination(
//            Long.MAX_VALUE, TimeUnit.NANOSECONDS
//        )
    }

    public override fun onStart() {
        super.onStart()
        naverRecognizer?.getSpeechRecognizer()?.initialize()
    }

    override fun onResume() {
        super.onResume()
        
        //MediaPipe 초기 설정
        backgroundBothExecutor.execute {
            if(this::poseLandmarkerHelper.isInitialized) {
                if(poseLandmarkerHelper.isClose()) {
                    poseLandmarkerHelper.setupPoseLandmarker()
                }
            }

            if(this::handLandmarkerHelper.isInitialized) {
                if (handLandmarkerHelper.isClose()) {
                    handLandmarkerHelper.setupHandLandmarker()
                }
            }
        }

//        backgroundPoseExecutor.execute {
//            if(this::poseLandmarkerHelper.isInitialized) {
//                if(poseLandmarkerHelper.isClose()) {
//                    poseLandmarkerHelper.setupPoseLandmarker()
//                }
//            }
//        }
//
//        backgroundHandExecutor.execute {
//            if(this::handLandmarkerHelper.isInitialized) {
//                if (handLandmarkerHelper.isClose()) {
//                    handLandmarkerHelper.setupHandLandmarker()
//                }
//            }
//        }
        
        //STT 초기 설정
        mResult = ""
        binding.tvCRS.text = ""
        binding.btnCRS.setText(R.string.str_start)
        binding.btnCRS.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        if(this::poseLandmarkerHelper.isInitialized) {
            viewModel.setMinPoseDetectionConfidence(poseLandmarkerHelper.minPoseDetectionConfidence)
            viewModel.setMinPoseTrackingConfidence(poseLandmarkerHelper.minPoseTrackingConfidence)
            viewModel.setMinPosePresenceConfidence(poseLandmarkerHelper.minPosePresenceConfidence)
            viewModel.setPoseDelegate(poseLandmarkerHelper.currentDelegate)

            // Close the PoseLandmarkerHelper and release resources
            backgroundBothExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
//            backgroundPoseExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        }

        if(this::handLandmarkerHelper.isInitialized) {
            viewModel.setMaxHands(handLandmarkerHelper.maxNumHands)
            viewModel.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            viewModel.setHandDelegate(handLandmarkerHelper.currentDelegate)

            // Close the HandLandmarkerHelper and release resources
            backgroundBothExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
//            backgroundHandExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
        }
    }

    public override fun onStop() {
        super.onStop()
        naverRecognizer?.getSpeechRecognizer()?.release()
    }

    internal class RecognitionHandler(activity: ConversationActivity) : Handler() {
        private val mActivity: WeakReference<ConversationActivity>

        init {mActivity = WeakReference(activity)}
        override fun handleMessage(msg: Message) {

            val activity = mActivity.get()
            activity?.handleMessage(msg)
        }
    }
}