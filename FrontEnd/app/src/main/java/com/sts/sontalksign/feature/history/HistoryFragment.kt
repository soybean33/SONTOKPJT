package com.sts.sontalksign.feature.history

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sts.sontalksign.R
import com.sts.sontalksign.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyListAdapter: HistoryListAdapter

//    private val api = APIS.create();

    var historyList: ArrayList<HistoryListModel> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)

        // 데이터 바인딩 초기화
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        binding = FragmentHistoryBinding.bind(view)

        // 리사이클러뷰 초기화
        val recyclerViewList = binding.rvHistoryItem
        recyclerViewList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewList.setHasFixedSize(false)


// 더미 데이터 생성
        val dummyData1 = HistoryListModel(
            historyTitle = "Title 1",
            EndedTime = 1,
            historyitemmodel = arrayListOf(
                HistoryItemModel(historyItemTag = "1번 태그"),
                HistoryItemModel(historyItemTag = "2번 태그입니다"),
                HistoryItemModel(historyItemTag = "강한성구"),
                HistoryItemModel(historyItemTag = "용우오빠코테홧팅")
            )
        )

        val dummyData2 = HistoryListModel(
            historyTitle = "Title 2",
            EndedTime = 2,
            historyitemmodel = arrayListOf(
                HistoryItemModel(historyItemTag = "동동이언니"),
                HistoryItemModel(historyItemTag = "할아버지"),
                HistoryItemModel(historyItemTag = "구텐탁 팀장님")
            )
        )

        // 더미 데이터를 리스트에 추가
        historyList.add(dummyData1)
        historyList.add(dummyData2)


        // 어댑터 설정 (여기서 dataList는 데이터 리스트로 대체해야 합니다)
        historyListAdapter = HistoryListAdapter(historyList)
        binding.rvHistoryItem.adapter = historyListAdapter


//        historyList.clear()


        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())  // import androidx.preference.PreferenceManager 인지 확인
        val token = pref.getString("SERVER_ACCESS_TOKEN", "1")

//

        return view


    }

    private fun showNoDataMessage() {
        // 데이터가 없을 때 특정 메시지를 표시하는 로직을 여기에 구현합니다.
        // 예를 들어, TextView에 메시지를 설정하거나 다이얼로그를 표시할 수 있습니다.
        // 예제로 TextView에 메시지 설정하는 방법을 보여드리겠습니다.

        val noDataMessage = "대화한 내역이 없습니다." // 표시할 메시지를 지정
//        binding.tvViewNoData.text = noDataMessage
//        binding.tvViewNoData.visibility = View.VISIBLE // TextView를 화면에 표시
    }


    private fun saveData() {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val edit = pref.edit()

        // 토큰 정보를 저장
        val token = "your_token_here" // 실제 토큰 정보로 대체
        edit.putString("[키]", "[데이터]")


        // 닉네임 정보도 동일하게 저장할 수 있습니다.
        val nickname = "your_nickname_here" // 실제 닉네임 정보로 대체
        edit.putString("[키]", "[데이터]")


        edit.apply()    // 적용

    }
}