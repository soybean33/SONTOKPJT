package com.sts.sontalksign.feature.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton
import java.io.File


class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var historyDetailTagAdapter: HistoryItemTagAdapter
    private lateinit var historyDetailConversationAdapter: HistoryDetailConversationAdapter

    var historyDetailTagList: ArrayList<CommonTagItem> = ArrayList()
    var historyDetailConList: ArrayList<HistoryDetailConversationModel> = ArrayList()

    private val TAG: String = "HistoryDetailActivity"
    private var directory: String? = null

    private var historyTitle: String? = null // historyTitle 추가

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        directory = filesDir.absolutePath

        // 데이터 바인딩 초기화
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent에서 "historyTitle"을 가져옵니다.
        historyTitle = intent.getStringExtra("historyTitle")

        // 가져온 historyTitle을 TextView에 설정
        binding.tvHistoryDetailTitle.text = historyTitle

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

        // RecyclerView 어댑터 초기화
        historyDetailTagAdapter = HistoryItemTagAdapter(historyDetailTagList)
        binding.rvHistoryDetailTag.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = historyDetailTagAdapter
        }


        // Safe call for directory
        val dirPath = directory
        if (dirPath != null) {

            readTextFile(directory ?: filesDir.absolutePath, historyTitle ?: "")
        }
    }

    private fun parseLine(line: String): HistoryDetailConversationModel? {
        val isLeft = line.contains("<<") // Check if it's a left message
        val parts = line.split("<", ">") // Split the line at '<'

        // Check if we have at least 3 parts (<<, Message, Time)
        if (parts.size == 0) return null


        val messageContent = parts[0]
        val messageTime = parts[1]



        return HistoryDetailConversationModel(messageContent, messageTime, isLeft)
    }

    private fun readFileContents(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return ""

        return file.readText()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun readTextFile(directoryPath: String, historyTitle: String) {

        val files = File(directoryPath).listFiles()

        files?.forEach { file ->
            if (file.isFile && file.name.endsWith(".txt") && file.name != "TAGS.txt") {
                try {
                    val fileContents = readFileContents(file.absolutePath)
                    val lines = fileContents.lines()

                    if (lines.size >= 3 && lines[0] == historyTitle) {
//                        binding.tvHistoryDetailTitle.text = lines[0]
                        val tagsLine = lines[1]

                        val tagsList = tagsLine.substringAfter("TAGS_").trim().split("_")
                        val tagItems = ArrayList<CommonTagItem>()

                        tagsList.forEach { tagId ->
                            val tagIdInt = tagId.toIntOrNull() // tagId를 안전하게 정수로 변환
                            if (tagIdInt != null && tagIdInt >= 0 && tagIdInt < TagSingleton.tagList.size) {
                                // 유효한 인덱스 범위 내에 있는 경우에만 처리
                                historyDetailTagList.add(CommonTagItem(tagId, TagSingleton.tagList[tagIdInt].tagText))
                            }
                        }

                        var splited = lines[2].split("<<", ">>")

                        for (line in splited) {
                            if (line.equals("")) continue
                            historyDetailConList.add(parseLine(line)!!)
                        }

                    }
                } catch (e: Exception) {
                    Log.e(TAG, "파일 읽기 중 오류 발생: ${e.message}")
                }
            }
        }

        historyDetailConversationAdapter.notifyDataSetChanged()
    }
}