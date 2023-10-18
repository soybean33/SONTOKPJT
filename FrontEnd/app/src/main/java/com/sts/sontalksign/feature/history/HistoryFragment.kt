package com.sts.sontalksign.feature.history

import android.os.Bundle
import android.preference.PreferenceManager
import android.telecom.Call
import android.util.Log
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
        recyclerViewList.setHasFixedSize(true)


        // 어댑터 설정 (여기서 dataList는 데이터 리스트로 대체해야 합니다)
        historyListAdapter = HistoryListAdapter(historyList)
        binding.rvHistoryItem.adapter = historyListAdapter




        historyList.clear()


        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())  // import androidx.preference.PreferenceManager 인지 확인
        val token = pref.getString("SERVER_ACCESS_TOKEN", "1")

//        Log.d("엑세스토큰입니다", token.toString())
//        api.MyCoins(authorization = "Bearer ${token}").enqueue(object: Callback<ArrayList<HistoryListModel>> {
//            override fun onResponse(call: Call<ArrayList<HistoryListModel>>, response: Response<ArrayList<HistoryListModel>>) {
//                if(response.code() == 200) {    // 200 Success
//                    Log.d("로그", "보유 마패 전체 조회: 200 Success")
//
//                    val responseBody = response.body()
//
//
//
//                    if (responseBody != null) {
//                        for (coin in responseBody) {
//
//
//                            historyList.add(coin)
//
//                        }
//                        HistoryListAdapter.notifyDataSetChanged()
//                    } else {
//                        showNoDataMessage()
//                    }
//
//                    HistoryListAdapter = HistoryListAdapter(historyList)
//
//                    binding.rvHistoryItem.adapter = HistoryListAdapter
//
//
//
//                }
//                else if(response.code() == 400) {   // 400 Bad Request - Message에 누락 필드명 기입
//                    Log.d("로그", "내 자산 전체 조회: 400 Bad Request")
//                }
//                else if(response.code() == 401) {   // 401 Unauthorized - 인증 토큰값 무효
//                    Log.d("로그", "내 자산 전체 조회: 401 Unauthorized")
//                }
//                else if(response.code() == 403) {
//                    Log.d("로그", "내 자산 전체 조회: 403 Forbidden")
//                }
//                else if(response.code() == 404) {   // 404 Not Found
//                    Log.d("로그", "내 자산 전체 조회: 404 Not Found")
//                }
//            }
//            override fun onFailure(call: Call<ArrayList<HistoryListModel>>, t: Throwable) {
//                Log.d("로그", "내 자산 전체 조회: onFailure")
//            }
//        })
//
//        api.MyTotalAsset(authorization = "Bearer ${token}").enqueue(object : Callback<HistoryListModel> {
//            override fun onResponse(call: Call<HistoryListModel>, response: Response<HistoryListModel>) {
//                if (response.code() == 200) {    // 200 Success
//                    Log.d("로그", "내 자산 전체 조회: 200 Success")
//
//                    val responseBody = response.body()
//
//
//
//                    if (responseBody != null) {
//
//                    }
//                }
//                else if(response.code() == 400) {   // 400 Bad Request - Message에 누락 필드명 기입
//                    Log.d("로그", "최근 기록 리스트 조회: 400 Bad Request")
//                }
//                else if(response.code() == 401) {   // 401 Unauthorized - 인증 토큰값 무효
//                    Log.d("로그", "최근 기록 리스트 조회: 401 Unauthorized")
//                }
//                else if(response.code() == 403) {
//                    Log.d("로그", "최근 기록 리스트 조회: 403 Forbidden")
//                }
//                else if(response.code() == 404) {   // 404 Not Found
//                    Log.d("로그", "최근 기록 리스트 조회: 404 Not Found")
//                }
//            }
//            override fun onFailure(call: Call<HistoryListModel>, t: Throwable) {
//                Log.d("로그", "최근 기록 리스트 조회: onFailure")
//
//            }
//        })


        return view


    }

    private fun showNoDataMessage() {
        // 데이터가 없을 때 특정 메시지를 표시하는 로직을 여기에 구현합니다.
        // 예를 들어, TextView에 메시지를 설정하거나 다이얼로그를 표시할 수 있습니다.
        // 예제로 TextView에 메시지 설정하는 방법을 보여드리겠습니다.

        val noDataMessage = "보유한 마패가 없습니다." // 표시할 메시지를 지정
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