package com.sts.sontalksign.feature.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    private lateinit var historyDetailTagAdapter: HistoryDetailTagAdapter
    private lateinit var historyDetailConversationAdapter: HistoryDetailConversationAdapter

//    private val api = APIS.create();

    var historyDetailTagList: ArrayList<HistoryDetailTagModel> = ArrayList()
    var historyDetailConList: ArrayList<HistoryDetailConversationModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // RecyclerView 초기화
        val recyclerViewTagList = binding.rvHistoryDetailTag
        recyclerViewTagList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewTagList.setHasFixedSize(false)

        val recyclerViewConversationList = binding.rvHistoryDetailConversation
        recyclerViewConversationList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewConversationList.setHasFixedSize(false)

//        val dummyData1 =
////            HistoryDetailTagModel  = arrayListOf(
//            HistoryItemModel(historyItemTag = "1번 태그"),
//            HistoryItemModel(historyItemTag = "2번 태그입니다"),
//            HistoryItemModel(historyItemTag = "강한성구"),
//            HistoryItemModel(historyItemTag = "용우오빠코테홧팅")
//        )

        // 더미 데이터를 리스트에 추가
//        val historyList: ArrayList<HistoryListModel> = arrayListOf(dummyData1, dummyData2)

        // RecyclerView에 대한 어댑터 생성 및 설정
        historyDetailTagAdapter = HistoryDetailTagAdapter(historyDetailTagList)
        recyclerViewTagList.adapter = historyDetailTagAdapter

        historyDetailConversationAdapter = HistoryDetailConversationAdapter(historyDetailConList)
        recyclerViewConversationList.adapter = historyDetailConversationAdapter

        // SharedPreferences에서 액세스 토큰 가져오기
//        val token = getAccessToken()

//


    }
}

private fun showNoDataMessage() {
    // 데이터가 없을 때 특정 메시지를 표시하는 로직을 여기에 구현합니다.
    // 예를 들어, TextView에 메시지를 설정하거나 다이얼로그를 표시할 수 있습니다.
    // 예제로 TextView에 메시지 설정하는 방법을 보여드리겠습니다.

    val noDataMessage = "대화한 내역이 없습니다." // 표시할 메시지를 지정
//        binding.tvViewNoData.text = noDataMessage
//        binding.tvViewNoData.visibility = View.VISIBLE // TextView를 화면에 표시
}



