package com.sts.sontalksign.feature.history

import android.os.Bundle
import android.text.TextUtils.split
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.FragmentHistoryBinding
import com.sts.sontalksign.feature.common.CommonTagItem
import com.sts.sontalksign.feature.common.TagSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyListAdapter: HistoryListAdapter

    var historyList: ArrayList<HistoryListModel> = ArrayList()

    private val TAG: String = "HistoryFragment"

    //내부저장소 - txt 파일
    private val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val timeFormat = SimpleDateFormat("HH:mm")

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

        loadTagList()

        // Safe call for directory
        val dirPath = directory
        if (dirPath != null) {
            // 히스토리 데이터 로드
            readTextFile(dirPath)
        }
    }



    fun loadTagList() {
        // tagList는 최초 1회만 로드
        if (TagSingleton.tagList.size > 0) return

        Log.d(TAG, "Directory : $directory")
        val file = File(directory.toString())

        // 파일 미존재
        if (!file.exists()) {
            Log.d(TAG, "TAGS file does not exist!!")
            file.mkdirs()
        }

        directory = requireActivity().filesDir.absolutePath //내부경로의 절대 경로

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


    private fun readFileContents(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) return ""

        return file.bufferedReader().use { it.readText() }
    }

    private fun readTextFile(directoryPath: String) {

        directory = requireActivity().filesDir.absolutePath //내부경로의 절대 경로


        if (directory != null) {
            val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val files = File(directory).listFiles() // Using local val here

            files?.forEach { file ->
                if (file.isFile && file.name.endsWith(".txt")) {
                    if (file.name != "TAGS.txt"){
                        val fileContents = readFileContents(file.absolutePath)
                        Log.d("무어머야러ㅐㅑ너래냐", fileContents)
                        val lines = fileContents.lines()

                        if (lines.size >= 3) {
                            val title = lines[0]
                            val tagsLine = lines[1]
                            val tagsList = tagsLine.substringAfter("TAGS_").split("_")

                            val tagItems = tagsList.mapNotNull { tagId ->
                                TagSingleton.tagList.find { it.tagInd == tagId }
                            }

//                        val fileName = file.absoluteFile
                            val date = formatter.parse(file.name.split(".")[0])
                            val endedTime = date?.time ?: 0L

                            historyList.add(HistoryListModel(title, endedTime, ArrayList(tagItems)))
                        }
                    }


                }
            }
        }

    }
}




