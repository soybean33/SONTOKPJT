package com.sts.sontalksign.feature.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    private lateinit var recyclerView1: RecyclerView
    private lateinit var historyDetailTagAdapter: HistoryDetailTagAdapter

    private lateinit var recyclerView2: RecyclerView
    private lateinit var historyDetailConversationAdapter: HistoryDetailConversationAdapter

//    private val api = APIS.create();

    var historyDetailTagList: ArrayList<HistoryDetailTagModel> = ArrayList()
    var historyDetailConList: ArrayList<HistoryDetailConversationModel> = ArrayList()


    private val TAG: String = "HistoryDetailActivity"


    private var directory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        directory = filesDir.absolutePath

        // 데이터 바인딩 초기화
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        directory = filesDir.absolutePath

        historyDetailConversationAdapter = HistoryDetailConversationAdapter(historyDetailConList)
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyDetailConversationAdapter
        }



        // Safe call for directory
        val dirPath = directory
        if (dirPath != null) {
            // 대화 내용 로드 및 어댑터에 바인딩
            readTextFile(dirPath)
            historyDetailConversationAdapter.notifyDataSetChanged()
        }

    }

    private fun getMyConversation(input: String): Pair<String, String> {

        val parts = input.split("<<")
        val isMine = true

        if (parts.size == 2) {
            // 내용(content)과 시간(time) 추출
            val content = parts[0]
            val time = parts[1]

            return Pair(content, time)
        } else {

            return Pair("", "")
        }
    }

    private fun getYourConversation(input: String): Pair<String, String> {
        val parts = input.split(">>")
        val isMine = false

        if (parts.size == 2) {
            // 내용(content)과 시간(time) 추출
            val content = parts[0]
            val time = parts[1]

            return Pair(content, time)
        } else {

            return Pair("", "")
        }
    }


    private fun readFileContents(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return ""

        return file.bufferedReader().use { it.readText() }
    }

    private fun readTextFile(directoryPath: String) {

        directory = filesDir.absolutePath //내부경로의 절대 경로
        Log.d("디렉토리확인", directory.toString())


        if (directory != null) {
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val files = File(directory).listFiles()

        files?.forEach { file ->
            if (file.isFile && file.name.endsWith(".txt")) {
                if (file.name != "TAGS.txt"){
                    val fileContents = readFileContents(file.absolutePath)
                    Log.d("sspspsp무어머야러ㅐㅑ너래냐", fileContents)
                    val lines = fileContents.lines()

                    if (lines.size >= 3) {
                        val title = lines[0]

                        val tagsLine = lines[1]
                        val tagsList = tagsLine.substringAfter("TAGS_").trim().split("_")
                        val tagItems = ArrayList<CommonTagItem>()
                        tagsList.forEach { tagId ->
                            val tagIdInt = tagId.toIntOrNull() // tagId를 안전하게 정수로 변환
                            if (tagIdInt != null && tagIdInt >= 0 && tagIdInt < TagSingleton.tagList.size) {
                                // 유효한 인덱스 범위 내에 있는 경우에만 처리
                                tagItems.add(CommonTagItem(tagId, TagSingleton.tagList[tagIdInt].tagText))
                            } else {
                                // 유효하지 않은 tagId에 대한 처리 (예: 로깅)
                                Log.e("태그ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ", "태그아이디 아닙니다: $tagId")
                            }
                        }



                    val content = lines[2]

                    val (messageContent, messageTime) = if (content.contains("<<")) {
                        getMyConversation(content)

                    } else {
                        getYourConversation(content)
                    }

                    val isMine = content.contains("<<")
                    val date = formatter.parse(file.name.split(".")[0])

                    // 원하는 출력 형식으로 SimpleDateFormat을 다시 사용하여 포맷
                    val outputFormat = SimpleDateFormat("hh시 MM분", Locale.getDefault())
                    val formattedDate = outputFormat.format(date)

                    historyDetailConList.add(
                        HistoryDetailConversationModel(
                            messageContent,
                            messageTime,
                            isMine
                             )
                          )
                       }
                  }
              }
          }
        }
    }

}







