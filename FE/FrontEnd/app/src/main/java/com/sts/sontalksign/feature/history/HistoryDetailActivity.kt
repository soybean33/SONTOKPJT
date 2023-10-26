package com.sts.sontalksign.feature.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.ActivityConversationBinding
import com.sts.sontalksign.databinding.ActivityHistoryDetailBinding

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding

    private lateinit var recyclerView1: RecyclerView
    private lateinit var historyDetailTagAdapter: HistoryDetailTagAdapter

    private lateinit var recyclerView2: RecyclerView
    private lateinit var historyDetailConversationAdapter: HistoryDetailConversationAdapter

//    private val api = APIS.create();

    var historyDetailTagList: ArrayList<HistoryDetailTagModel> = ArrayList()
    var historyDetailConList: ArrayList<HistoryDetailConversationModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 데이터 바인딩 초기화
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        // RecyclerView 초기화
        // 첫 번째 리사이클러뷰 초기화
        recyclerView1 = binding.rvHistoryDetailTag
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView1.setHasFixedSize(false)

        // 두 번째 리사이클러뷰 초기화
        recyclerView2 = binding.rvMessages
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView2.setHasFixedSize(false)

//      // 더미 데이터 생성 및 추가
        val dummyTag1 = HistoryDetailTagModel("태그1")
        val dummyTag2 = HistoryDetailTagModel("태그2")
        val dummyTag3 = HistoryDetailTagModel("태그3")
        val dummyTag4 = HistoryDetailTagModel("태그4")

        // 더미 대화 데이터 생성
        val dummyCon1 = HistoryDetailConversationModel("대화1", "시간1", true)
        val dummyCon2 = HistoryDetailConversationModel("대화2", "시간2", false)
        val dummyCon3 = HistoryDetailConversationModel("대화3", "시간3", true)
        val dummyCon4 = HistoryDetailConversationModel("대화4", "시간4", false)




        // 생성된 더미 데이터를 리스트에 추가
        historyDetailTagList.addAll(listOf(dummyTag1, dummyTag2, dummyTag3, dummyTag4))
        // 생성된 더미 데이터를 리스트에 추가
        historyDetailConList.addAll(listOf(dummyCon1, dummyCon2, dummyCon3, dummyCon4))

        // 첫 번째 리사이클러뷰 어댑터 설정
        historyDetailTagAdapter = HistoryDetailTagAdapter(historyDetailTagList)
        recyclerView1.adapter = historyDetailTagAdapter

        // 두 번째 리사이클러뷰 어댑터 설정
        historyDetailConversationAdapter = HistoryDetailConversationAdapter(historyDetailConList)
        recyclerView2.adapter = historyDetailConversationAdapter

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



