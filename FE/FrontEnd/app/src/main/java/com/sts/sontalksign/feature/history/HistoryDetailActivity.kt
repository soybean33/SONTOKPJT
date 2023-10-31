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
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    private lateinit var recyclerView1: RecyclerView
    private lateinit var historyDetailTagAdapter: HistoryItemTagAdapter

    private lateinit var recyclerView2: RecyclerView
    private lateinit var historyDetailConversationAdapter: HistoryDetailConversationAdapter

    var historyDetailTagList: ArrayList<CommonTagItem> = ArrayList()
    var historyDetailConList: ArrayList<HistoryDetailConversationModel> = ArrayList()

    private val TAG: String = "HistoryDetailActivity"

    private var directory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        directory = filesDir.absolutePath

        // 데이터 바인딩 초기화
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView1 = binding.rvHistoryDetailTag
        historyDetailTagAdapter = HistoryItemTagAdapter(historyDetailTagList)
        recyclerView1.layoutManager = LinearLayoutManager(this)
        recyclerView1.adapter = historyDetailTagAdapter

        recyclerView2 = binding.rvMessages
        historyDetailConversationAdapter = HistoryDetailConversationAdapter(historyDetailConList)
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView2.adapter = historyDetailConversationAdapter
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
            // 중복된 어댑터 생성을 제거합니다.
            readTextFile(directory ?: filesDir.absolutePath)
        }
    }

    private fun parseLine(line: String): Triple<String, String, Boolean>? {
        val isLeft = line.contains("<<") // Check if it's a left message
        val parts = line.split("<") // Split the line at '<'

        // Check if we have at least 3 parts (<<, Message, Time)
        if (parts.size < 3) return null

        // Assuming the first part is empty for left messages, and the second part is the message
        val messageContent = parts[2].trim()
        val messageTime = parts[3].trim() // The third part is the time


        Log.d("메세지컨텐트", messageContent)
        Log.d("메세지타임", messageTime)

        return Triple(messageContent, messageTime, isLeft)
    }


    private fun readFileContents(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return ""

        return file.readText()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun readTextFile(directoryPath: String) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        val files = File(directoryPath).listFiles()

        files?.forEach { file ->
            if (file.isFile && file.name.endsWith(".txt") && file.name != "TAGS.txt") {
                try {
                    val fileContents = readFileContents(file.absolutePath)
                    val lines = fileContents.lines()

                    if (lines.size >= 3) {
                        binding.tvHistoryDetailTitle.text = lines[0]
                        processTags(lines[1])

                        Log.d("라인222222드롭",lines.drop(2).toString() )

                        // 메시지 줄들 처리
                        lines.drop(2).forEach { line ->
                            if (line.isNotBlank()) {
                                parseLine(line)?.let { (messageContent, messageTime, isLeft) ->
                                    formatter.parse(messageTime)?.let { time ->
                                        historyDetailConList.add(HistoryDetailConversationModel(messageContent, messageTime, isLeft))
                                        Log.d("히스토리디테일컨리스트", historyDetailConList.toString())
                                        }
                                } ?: run {
                                    Log.d("Debug", "Line could not be parsed: $line")
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "파일 읽기 중 오류 발생: ${e.message}")
                }
            }
        }
        // 한 번에 변경 사항을 반영합니다.
        historyDetailTagAdapter.notifyDataSetChanged()
        historyDetailConversationAdapter.notifyDataSetChanged()
    }

    private fun processTags(tagsLine: String) {
        val tagsList = tagsLine.substringAfter("TAGS_").trim().split("_")

        historyDetailTagList.clear()

        tagsList.forEach { tagId ->
            val tagIdInt = tagId.toIntOrNull()
            if (tagIdInt != null && tagIdInt >= 0 && tagIdInt < TagSingleton.tagList.size) {
                historyDetailTagList.add(CommonTagItem(tagId, TagSingleton.tagList[tagIdInt].tagText))
            } else {
                Log.e(TAG, "유효하지 않은 태그 ID: $tagId")
            }
        }
    }
}