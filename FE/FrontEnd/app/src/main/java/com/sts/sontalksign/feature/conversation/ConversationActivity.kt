package com.sts.sontalksign.feature.conversation

import ConversationCameraAdapter
import CustomNoRecordForm
import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint

import android.content.Context

import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View

import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import com.google.android.material.internal.ViewUtils.dpToPx

import com.google.mediapipe.tasks.vision.core.RunningMode
import com.naver.speech.clientapi.SpeechRecognitionResult
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.feature.apis.NaverAPI
import com.sts.sontalksign.feature.common.CustomForm
import com.sts.sontalksign.feature.utils.AudioWriterPCM
import com.sts.sontalksign.global.FileFormats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ConversationActivity : AppCompatActivity(), PoseLandmarkerHelper.LandmarkerListener,
    HandLandmarkerHelper.LandmarkerListener {
    companion object {
        private const val TAG: String = "ConversationActivity"
        private const val cTAG = "CameraX Preview"
        private const val hlTAG = "Hand Landmarker"
        private const val pTAG = "Pose Landmarker"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val MODEL_CLASSIFIER = "sl_model.tflite"

        /** 카메라 및 오디오 권한 */
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    /** CLOVA Speech Recognition(CSR) 관련 내부 클래스
     * SpeechRecognizer 쓰레드의 메시지를 처리하는 핸들러 정의
     * */
    internal class RecognitionHandler(activity: ConversationActivity) : Handler() {
        private val mActivity: WeakReference<ConversationActivity>

        init {
            mActivity = WeakReference(activity)
        }

        override fun handleMessage(msg: Message) {
            val activity = mActivity.get()
            activity?.handleMessage(msg)
        }
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
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    /** Blocking ML operations are performed using this executor */
    private lateinit var backgroundBothExecutor: ExecutorService
    private val handSignHelper: HandSignHelper = HandSignHelper()

    private var tflite: Interpreter? = null
    private var interpreter: Interpreter? = null

    /** 사용자의 "녹음하기" 선택 여부 */
    private var isNowRecording: Boolean = false

    /** MediaPlayer 관련 변수(Naver Clova TTS API) */
    private lateinit var mediaPlayer: MediaPlayer
    private var isTTSPlaying: Boolean = false

    /** naverspeech-sdk-android(CLOVA Speech Recognition(CSR)) */
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

    /** Foldable 반응형 */
    private lateinit var windowInfoTracker: WindowInfoTracker

    private var sign: String = ""
    private var preret: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(binding.root)

        /** Foldable 반응 tracker */
        windowInfoTracker = WindowInfoTracker.getOrCreate(this@ConversationActivity)
        onWindowLayoutInfoChange()

        /** RecyclerView 초기화 */
        recyclerView = binding.rvCameraConversation
        recyclerView.layoutManager = LinearLayoutManager(this)
        conversationCameraAdapter = ConversationCameraAdapter(conversationCamera)
        recyclerView.adapter = conversationCameraAdapter
        (recyclerView.layoutManager as LinearLayoutManager).scrollToPosition(
            conversationCameraAdapter.itemCount - 1
        )

        /** 텍스트 입력 이벤트 처리 */
        binding.etTextConversation.setOnEditorActionListener { textView, actionId, _ ->
            var handled = false
            //완료버튼 클릭에만 처리
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var inpContent = textView.text.toString()
                addTextLine(inpContent, true)
                binding.etTextConversation.setText("")

                generateTtsApi(inpContent) //TTS 적용 - 텍스트를 음성 출력
                addTextLine(inpContent, true)
            }
            handled
        }
        /** "대화 종료" 버튼 클릭 */
        binding.btnStopConversation.setOnClickListener {
            stopConversation()
        }

        binding.btnEarlyUse1.setOnClickListener {
            var earlyUse1 = "안녕하세요! 이 기기를 통해 의사소통을 하려고 합니다. 말씀하시면 텍스트를 통해 제가 볼 수 있어요."
            addTextLine(earlyUse1, true)
            generateTtsApi(earlyUse1)
        }

        binding.btnEarlyUse2.setOnClickListener {
            var earlyUse1 = "네"
            addTextLine(earlyUse1, true)
            generateTtsApi(earlyUse1)
        }

        binding.btnEarlyUse3.setOnClickListener {
            var earlyUse1 = "아니오"
            addTextLine(earlyUse1, true)
            generateTtsApi(earlyUse1)
        }

        binding.btnEarlyUse4.setOnClickListener {
            var earlyUse1 = "감사합니다"
            addTextLine(earlyUse1, true)
            generateTtsApi(earlyUse1)
        }

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

        /** 카메라 권한 요청 */
        if (allPermissionsGranted()) {

        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        /** MediaPipe의 background executor 초기 설정 */
        backgroundBothExecutor = Executors.newFixedThreadPool(2)
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

        getTfliteInterpreter()

        /** CSR 관련 처리 */
        handler = RecognitionHandler(this)
        naverRecognizer = NaverRecognizer(this, handler!!)
        startSTTCoroutine()

    }

    /******** CSR 관련 함수 ********/
    private fun startSTTCoroutine() {
        lifecycleScope.launch(Dispatchers.Default) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                startSTTRoutine()
            }
        }
    }

    private fun startSTTRoutine() = runBlocking {
        playSTT()

    }

    private suspend fun playSTT() = coroutineScope {
        if (!naverRecognizer!!.getSpeechRecognizer().isRunning) {
            mResult = ""
            naverRecognizer!!.recognize()
        } else {
            Log.d(TAG, "stop and wait Final Result")
            naverRecognizer!!.getSpeechRecognizer().stop()
        }
    }

    /**  CSR 상태에 대한 동작 - clientReady, audioRecording, partialResult, final Result, recognitionError, clientInactive */
    private fun handleMessage(msg: Message): Int {
        when (msg.what) {
            /** 음성 인식을 시작할 준비가 완료된 경우 */
            R.id.clientReady -> {
                audioWriter = AudioWriterPCM(
                    filesDir.absolutePath + "/NaverSpeechTest"
                )
                audioWriter!!.open("Test")
            }
            /** 현재 음성 인식이 진행되고 있는 경우 */
            R.id.audioRecording -> {
                audioWriter?.write(msg.obj as ShortArray)
            }
            /** 처리가 되고 있는 도중에 결과를 받은 경우 */
            R.id.partialResult -> {
                mResult = msg.obj as String
            }
            /** 최종 인식이 완료되면 유사 결과를 모두 보여줌 */
            R.id.finalResult -> {
                val speechRecognitionResult = msg.obj as SpeechRecognitionResult
                val results = speechRecognitionResult.results
                val result = results[0].toString()
                startSTT(result, false)

            }
            /** 인식 오류가 발생한 경우 */
            R.id.recognitionError -> {
                audioWriter?.close()
                mResult = "Error code : ${msg.obj}"
            }
            /** 음성 인식 비활성화 상태인 경우 */
            R.id.clientInactive -> {
                audioWriter?.close()
                startSTTRoutine()
            }
        }

        return msg.what
    }

    /** STT 백그라운드 실행 초기 설정 */
    fun startSTT(sttResult: String, isMine: Boolean) {
        if (sttResult.isNullOrBlank()) return
        if (isTTSPlaying) {
            isTTSPlaying = false
            return
        }

        val currentTalkTime = System.currentTimeMillis()
        val conversationCameraModel = ConversationCameraModel(
            ConversationText = sttResult,
            ConversationTime = FileFormats.timeFormat.format(currentTalkTime),
            isLeft = isMine
        )

        // Add STT result to the conversation
        conversationCameraAdapter.addItemAndScroll(conversationCameraModel, recyclerView)

        // If recording is selected, save the STT result to the text file
        if (isNowRecording) {
            addTextLine(sttResult, isMine)
        }
    }

    /******** NAVER TTS API 관련 함수 ********/
    /** NAVER TTS API 요청 처리 */
    private fun generateTtsApi(line: String) {
        //API 요청을 위한 스레드 생성
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = NaverAPI.create().generateSpeech("nsujin", 0, 0, 0, "mp3", line)
                    .execute() //API 요청

                if (response.isSuccessful) {
                    isTTSPlaying = true
                    val body = response.body()
                    if (body != null) {
                        //응답 결과를 임시 저장
                        val resFile = File.createTempFile("res", "mp3", cacheDir)
                        val outputStream = FileOutputStream(resFile)

                        val input = body.byteStream()
                        val buffer = ByteArray(1024)
                        var bytesRead: Int

                        while (input.read(buffer).also { bytesRead = it } != -1) {
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
                    if (errorBody != null) {
                        Log.d(TAG, "Error: " + errorBody.string())
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.d(TAG, e.toString())
            }
        }
    }

    /** NAVER TTS API 결과 처리 */
    @SuppressLint("SuspiciousIndentation")
    fun handleTTSResult(ttsResult: String, isMine: Boolean) {
        val currentTime = System.currentTimeMillis()
        val conversationCameraModel = ConversationCameraModel(
            ConversationText = ttsResult,
            ConversationTime = FileFormats.timeFormat.format(currentTime),
            isLeft = isMine
        )

        this.conversationCameraAdapter.addItemAndScroll(conversationCameraModel, recyclerView)
    }

    /******** ML Model 관련 함수 ********/
    /** Tensorflow Lite 모델의 Interpreter 초기 설정 */
    private fun getTfliteInterpreter() {
        tflite = Interpreter(loadModelFile(this))
    }

    /** MODEL FILE 초기 로드 */
    private fun loadModelFile(context: Context): ByteBuffer {
        val am: AssetManager = context.getAssets()
        val afd: AssetFileDescriptor = am.openFd(MODEL_CLASSIFIER)
        val fis: FileInputStream = FileInputStream(afd.fileDescriptor)
        val fc: FileChannel = fis.channel
        val startOffSet: Long = afd.startOffset
        val declaredLength: Long = afd.declaredLength

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffSet, declaredLength)
    }

    /******** 대화 내용 기록 관련 함수 ********/
    /** 대화 내용 기록용 텍스트 파일 초기 생성 */
    private fun createTextFile() {
        /** 녹음하기 미선택의 경우 텍스트 파일 미생성 */
        if (!isNowRecording) return

        /** 내부 저장소 경로 */
        currentTime = System.currentTimeMillis() //ms로 반환
        directory = filesDir.absolutePath //내부경로의 절대 경로
        convFilename = FileFormats.dataFormat.format(currentTime) + ".txt"

        textList = ""
    }

    /** 나의 발화 텍스트 생성 */
    private fun getMyConversation(content: String, time: String): String {
        return getString(R.string.my_conversation_content) + content + getString(R.string.my_conversation_time) + time
    }

    /** 상대의 발화 텍스트 생성 */
    private fun getYourConversation(content: String, time: String): String {
        return getString(R.string.your_conversation_content) + content + getString(R.string.your_conversation_time) + time
    }

    /** 발화 내용 한 줄 추가
     * isMine - 0:나의 대사, 1:상대의 대사
     * */
    private fun addTextLine(content: String, isMine: Boolean) {
        /** 녹음하기 미선택의 경우 대황 내용 저장 X */
        if (!isNowRecording) return

        var bContent = String()
        currentTime = System.currentTimeMillis()
        when (isMine) {
            true -> bContent =
                getMyConversation(content, FileFormats.timeFormat.format(currentTime))

            false -> bContent =
                getYourConversation(content, FileFormats.timeFormat.format(currentTime))
        }

        textList += bContent
    }

    /** 파일 쓰기 */
    private fun writeTextFile(result: String) {
        val dir = File(directory)

        //파일 미존재 시 디렉토리 및 파일 생성
        if (!dir.exists()) {
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
    private fun readTextFile(fullpath: String): String {
        val file = File(fullpath)

        //파일 미존재
        if (!file.exists()) return ""

        val reader = FileReader(file)
        val buffer = BufferedReader(reader)

        var line: String? = ""
        var result = StringBuffer()

        while (true) {
            line = buffer.readLine() //줄단위로 read
            if (line == null) break
            else result.append(line).append("\n")
        }

        buffer.close()
        return result.toString()
    }

    /** 대화 종료 처리 함수 */
    private fun stopConversation() {
        Log.d(TAG, "stopConversation() START")

        /** 녹음하기를 선택한 경우 - 팝업 발생 및 대화 내용 저장 */
        if (isNowRecording) {
            val cForm = CustomForm(this)
            cForm.show()
            cForm.setOnBtnClickedListener(object : CustomForm.onBtnClickedListener {
                override fun onBtnStoreClicked(title: String, tags: String) {
                    /** 대화 종료 전 기록에 쌓인 대화 내용을 저장 */
                    /** {제목\n태그인덱스\n대화내용} 형식 */
                    val rConversation = title + "\nTAGS" + tags + "\n" + textList
                    writeTextFile(rConversation)
                    finish()
                }

                override fun onBtnCancelStoreClicked() {

                }
            })
        } else {
            // When recording is not happening and "End conversation?" popup should be shown
            val nForm = CustomNoRecordForm(this)
            nForm.show()
            nForm.setOnBtnDismissCancelClickedListener(object :
                CustomNoRecordForm.OnBtnDismissCancelClickedListener {
                override fun onBtnDismissCancelClicked() {
                    // This will be called when the dismiss or cancel button is clicked
                    finish()
                }

            }
            )
        }
    }

    /******** CameraX 관련 함수 ********/
    /** 카메라 시작 */
    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(this@ConversationActivity) //Activity와 카메라의 수명 주기를 binding

        cameraProviderFuture.addListener({
            cameraProvider =
                cameraProviderFuture.get() //카메라의 수명 주기를 APP 프로세스 내의 LifecycleOwner에 바인딩

            bindCameraUseCases() //Camera Use Cases 빌드 및 바인딩
        }, ContextCompat.getMainExecutor(this)) //기본 스레드에서 실행되는 Executor를 반환
    }

    /** 카메라 바인딩 */
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        /** 카메라 프리뷰 초기화 및 설정 */
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(cameraFacing).build()
        /** 전면 카메라를 기본으로 선택 */

        preview = Preview.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.pvCamera.display.rotation)
            .build()

        /** ImageAnalysis. Using RGBA 8888 to match how our models work */
        imageAnalyzer =
            ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.pvCamera.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    /** analyzer 할당 */
                    it.setAnalyzer(backgroundBothExecutor) { image ->
                        mediaPipeSequence(image)
                    }
                }

        /** 바인딩된 항목 전체 제거 */
        cameraProvider.unbindAll()

        try {
            /** 카메라 관련 객체 바인딩 */
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            preview?.setSurfaceProvider(binding.pvCamera.surfaceProvider)
        } catch (exc: Exception) {
            /** 실패 케이스 - 앱 내 포커스가 더이상 없는 경우 등 */
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    /******** MediaPipe 관련 함수 ********/
    /** ImageAnalyzer에 대한 처리 시작 */
    private fun mediaPipeSequence(imageProxy: ImageProxy) {
        lifecycleScope.launch {
            try {
                val ret: String = withContext(Default) {
                    mediaPipe(imageProxy)
                    mediaPipeProcess()
                }

                if (ret != "" && ret != "1") {
                    binding.tvCRS.text = ret
                }
            } catch (e: Exception) {
                // 예외 처리를 수행하세요.
                Log.e("mediaPipeSequence", e.message.toString())
            }
        }
    }
    /** imageProxy 처리 */
    private suspend fun mediaPipe(imageProxy: ImageProxy) = coroutineScope {
        val frameTime = SystemClock.uptimeMillis()
        val bitmapBuffer = Bitmap.createBitmap(
            imageProxy.width,
            imageProxy.height,
            Bitmap.Config.ARGB_8888
        )

        imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
        imageProxy.close()

        val poseResult = async { detectPose(imageProxy, bitmapBuffer, frameTime) }
        val handResult = async { detectHand(imageProxy, bitmapBuffer, frameTime) }

        // await() 함수를 사용하여 각각의 작업이 완료될 때까지 기다립니다.
        poseResult.await()
        handResult.await()
    }

    /** MediaPipe의 결과를 ML에 적용 */
    private fun mediaPipeProcess(): String {
        val inputArrayList: ArrayList<FloatArray> = handSignHelper.Solution()
        val inputArray: Array<FloatArray> = inputArrayList.toTypedArray()
        val input3DArray: Array<Array<FloatArray>> = arrayOf(inputArray)

        val output = Array(1) { FloatArray(handSignHelper.dataSize()) { 0.0f } }

        tflite!!.run(input3DArray, output)

        // 확률 로그 작업
        val precision = 3
        val resultStringBuilder = StringBuilder("")
        for (i in 0 until handSignHelper.dataSize()) {
            val formattedValue = String.format("%.${precision}f", output[0][i])
            resultStringBuilder.append("$formattedValue\t")
        }

        Log.d("Result", resultStringBuilder.toString())

        /** 확률 로그 끝 */
        return handSignHelper.wordQueueManager(output[0].toList().toTypedArray())
    }

    /** MediaPipe - Pose 감지 */
    private fun detectPose(imageProxy: ImageProxy, bitmapBuffer: Bitmap, frameTime: Long) {
        lifecycleScope.launch {
            try {
                if (this@ConversationActivity::poseLandmarkerHelper.isInitialized) {
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
    }

    /** MediaPipe - Hand 감지 */
    private fun detectHand(imageProxy: ImageProxy, bitmapBuffer: Bitmap, frameTime: Long) {
        lifecycleScope.launch {
            try {
                if (this@ConversationActivity::handLandmarkerHelper.isInitialized) {
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
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            binding.pvCamera.display.rotation
    }

    /** PoseLandmarker 결과 */
    override fun onPoseResults(
        resultBundle: PoseLandmarkerHelper.ResultBundle
    ) {
        this?.runOnUiThread {
            if (binding != null) {
                handSignHelper.initPose(resultBundle)

                /** OverlayView에 필수 정보 전달 */
                binding.poseOverlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                /** 갱신 */
                binding.poseOverlay.invalidate()
            }
        }
    }

    /** PoseLandmarker 에러 처리 */
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

    /** HandLandmarker 결과 */
    override fun onHandResults(
        resultBundle: HandLandmarkerHelper.ResultBundle
    ) {
        this.runOnUiThread {
            if (binding != null) {
//                Log.d("TEST-Hand", resultBundle.results.first().toString())

                handSignHelper.initHand(resultBundle)

                /** OverlayView에 필수 정보 전달 */
                binding.handOverlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )

                /** 갱신 */
                binding.handOverlay.invalidate()
            }
        }
    }

    /** HandLandmarker 에러 처리 */
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

    /******** Z Flip 모델 반응형 관련 함수 ********/
    /** Z Flip 모델 Foldable H/W 감지 */
    private fun onWindowLayoutInfoChange() {
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoTracker.windowLayoutInfo(this@ConversationActivity)
                    .collect { value ->
                        updateUI(value)
                    }
            }
        }
    }

    /** Z Flip 모델 반응형 처리
     * 1) Camera Preview의 Height
     * 2) Layout의 Background Color
     * 3) EditText의 Text Color
     * */
    private fun updateUI(newLayoutInfo: WindowLayoutInfo) {
        /** foldable 폰이 아닌 경우 UI 갱신 불가 */
        if (newLayoutInfo.displayFeatures.isNullOrEmpty()) return

        var oldLayoutHeight: Int? = null
        var newLayoutHeight: Int? = null
        var oldBackgroundColor: Int? = null
        var newBackgroundColor: Int? = null
        var oldTextColor: Int? = null
        var newTextColor: Int? = null

        if (newLayoutInfo.displayFeatures[0].toString().contains("HALF_OPENED")) {
            oldLayoutHeight = dpToPx(700)
            newLayoutHeight = (newLayoutInfo.displayFeatures[0].bounds.bottom * 0.9).toInt()
            oldBackgroundColor = Color.WHITE
            newBackgroundColor = Color.BLACK
            oldTextColor = Color.BLACK
            newTextColor = Color.WHITE

//            isFolded = true
            binding.tvAlertUnfolded.visibility = View.GONE
            binding.vBackgroundConversation.visibility = View.VISIBLE
        } else if (newLayoutInfo.displayFeatures[0].toString().contains("FLAT")) {
            oldLayoutHeight = (newLayoutInfo.displayFeatures[0].bounds.bottom * 0.9).toInt()
            newLayoutHeight = dpToPx(700)
            oldBackgroundColor = Color.BLACK
            newBackgroundColor = Color.WHITE
            oldTextColor = Color.WHITE
            newTextColor = Color.BLACK

//            isFolded = false
            binding.tvAlertUnfolded.visibility = View.VISIBLE
            binding.vBackgroundConversation.visibility = View.GONE
        }

        /** Camera Preview의 Height */
        val heightAnimator = ValueAnimator.ofInt(oldLayoutHeight!!, newLayoutHeight!!)
        heightAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            binding.cameraContainer.layoutParams.height = value
            binding.cameraContainer.requestLayout()
        }

        /** Layout의 Background Color */
        val bgColorAnimator =
            ValueAnimator.ofObject(
                ArgbEvaluator(),
                oldBackgroundColor!!,
                newBackgroundColor!!
            )
        bgColorAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            binding.clConversation.setBackgroundColor(value)
        }

        /** EditText의 Text Color */
        val txtColorAnimator =
            ValueAnimator.ofObject(ArgbEvaluator(), oldTextColor!!, newTextColor!!)
        txtColorAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            binding.etTextConversation.setTextColor(value)
            binding.etTextConversation.setHintTextColor(value)
        }

        heightAnimator.duration = 500
        bgColorAnimator.duration = 500
        txtColorAnimator.duration = 500
        heightAnimator.start()
        bgColorAnimator.start()
        txtColorAnimator.start()
    }

    /** dp 단위를 px로 변경 */
    private fun dpToPx(dp: Int): Int {
        val scale = resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    /******** 권한 요청 관련 함수 ********/
    /** 필수 권한 확인 및 요청 */
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** 권한 요청 처리 - 카메라 */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                setUpCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                )
                    .show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()

        /** Shut down our background executor */
        backgroundBothExecutor.shutdown()
        backgroundBothExecutor.awaitTermination(
            Long.MAX_VALUE, TimeUnit.NANOSECONDS
        )
    }

    public override fun onStart() {
        super.onStart()
        naverRecognizer?.getSpeechRecognizer()?.initialize()
    }

    override fun onResume() {
        super.onResume()

        /** MediaPipe 초기 설정 */
        backgroundBothExecutor.execute {
            if (this::poseLandmarkerHelper.isInitialized) {
                if (poseLandmarkerHelper.isClose()) {
                    poseLandmarkerHelper.setupPoseLandmarker()
                }
            }

            if (this::handLandmarkerHelper.isInitialized) {
                if (handLandmarkerHelper.isClose()) {
                    handLandmarkerHelper.setupHandLandmarker()
                }
            }
        }

        /** STT 초기 설정 */
        mResult = ""
    }

    override fun onPause() {
        super.onPause()
        if (this::poseLandmarkerHelper.isInitialized) {
            viewModel.setMinPoseDetectionConfidence(poseLandmarkerHelper.minPoseDetectionConfidence)
            viewModel.setMinPoseTrackingConfidence(poseLandmarkerHelper.minPoseTrackingConfidence)
            viewModel.setMinPosePresenceConfidence(poseLandmarkerHelper.minPosePresenceConfidence)
            viewModel.setPoseDelegate(poseLandmarkerHelper.currentDelegate)

            /** Close the PoseLandmarkerHelper and release resources */
            backgroundBothExecutor.execute { poseLandmarkerHelper.clearPoseLandmarker() }
        }

        if (this::handLandmarkerHelper.isInitialized) {
            viewModel.setMaxHands(handLandmarkerHelper.maxNumHands)
            viewModel.setMinHandDetectionConfidence(handLandmarkerHelper.minHandDetectionConfidence)
            viewModel.setMinHandTrackingConfidence(handLandmarkerHelper.minHandTrackingConfidence)
            viewModel.setMinHandPresenceConfidence(handLandmarkerHelper.minHandPresenceConfidence)
            viewModel.setHandDelegate(handLandmarkerHelper.currentDelegate)

            /** Close the HandLandmarkerHelper and release resources */
            backgroundBothExecutor.execute { handLandmarkerHelper.clearHandLandmarker() }
        }
    }

    public override fun onStop() {
        super.onStop()
        naverRecognizer?.getSpeechRecognizer()?.release()
    }
}