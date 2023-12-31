package com.sts.sontalksign.feature.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.flogger.context.Tags
import com.sts.sontalksign.databinding.FragmentHistoryBinding
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    lateinit var historyListAdapter: HistoryListAdapter

    var historyList: ArrayList<HistoryListModel> = ArrayList()


    private var directory: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // 데이터 바인딩 초기화
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        directory = requireActivity().filesDir.absolutePath

        historyListAdapter = HistoryListAdapter(historyList)
        binding.rvHistoryItem.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyListAdapter
        }

        // Safe call for directory
        val dirPath = directory
        if (dirPath != null) {
            // 히스토리 데이터 로드
            readTextFile()
        }
    }

    private fun readFileContents(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return ""

        return file.bufferedReader().use { it.readText() }
    }

    private fun readTextFile() {
        directory = requireActivity().filesDir.absolutePath //내부경로의 절대 경로

        if (directory != null) {
            val files = File(directory).listFiles() // Using local val here

            files?.forEach { file ->
                if (file.isFile && file.name.endsWith(".txt")) {
                    if (file.name != "TAGS.txt"){
                        val fileContents = readFileContents(file.absolutePath)
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

                                }
                            }

                            // 원하는 출력 형식으로 SimpleDateFormat을 다시 사용하여 포맷
                            val date = file.name.split(".")[0]
                            val storedFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
                            val resultDate = storedFormat.parse(date)
                            val formattedDate = outputFormat.format(resultDate)

                            historyList.add(HistoryListModel(title, formattedDate, tagItems))
                        }
                    }
                }
            }
        }
    }
}




