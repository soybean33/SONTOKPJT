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

    //내부저장소 - txt 파일
    private val timeFormat = SimpleDateFormat("HH:mm")

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


        loadTagList()

        // Safe call for directory
        val dirPath = directory
        if (dirPath != null) {
            // 대화 내용 로드 및 어댑터에 바인딩
            readTextFile(dirPath)
            historyDetailConversationAdapter.notifyDataSetChanged()
        }

    }

    private fun loadTagList() {
        // tagList는 최초 1회만 로드
        if (TagSingleton.tagList.size > 0) return

        Log.d(TAG, "Directory : $directory")
        val file = File(directory.toString())

        // 파일 미존재
        if (!file.exists()) {
            Log.d(TAG, "TAGS file does not exist!!")
            file.mkdirs()
        }

        directory = filesDir.absolutePath //내부경로의 절대 경로

        val tagFN = "TAGS.txt"
        val fPath = "$directory/$tagFN"
        val reader = FileReader(fPath)
        val buffer = BufferedReader(reader)

        var line: String?

        while (true) {
            line = buffer.readLine() // 줄 단위로 read
            if (line == null) break
            else {
                val (idx, name) = line.split(" ")
                TagSingleton.tagList.add(CommonTagItem(idx, name))
            }
        }

        val colorList = resources.getIntArray(R.array.tagColorArr)
        for (color in colorList) {
            TagSingleton.colorList.add(color)
        }
        TagSingleton.tagList.add(CommonTagItem("0", "TEST"))

        buffer.close()
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
        val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
        val files = File(directoryPath).listFiles()

        files?.forEach { file ->
            if (file.isFile && file.name.endsWith(".txt") && file.name != "TAGS.txt") {
                val fileContents = readFileContents(file.absolutePath)
                val lines = fileContents.lines()

                if (lines.size >= 3) {
                    val sentTime = lines[1]
                    val content = lines[2]

                    val (messageContent, messageTime) = if (content.contains("<<")) {
                        getMyConversation(content)
                    } else {
                        getYourConversation(content)
                    }

                    val isMine = content.contains("<<")
                    val date = formatter.parse(file.nameWithoutExtension)
                    val endedTime = date?.time ?: 0L

                    historyDetailConList.add(HistoryDetailConversationModel(messageContent, messageTime, isMine))
                }
            }
        }
    }

}







