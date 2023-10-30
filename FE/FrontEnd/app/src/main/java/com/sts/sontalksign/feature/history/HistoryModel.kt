package com.sts.sontalksign.feature.history

import com.sts.sontalksign.feature.common.CommonTagItem
import java.util.Date

data class HistoryListModel(
    val historyTitle: String, // 기록 제목
    val EndedTime: String, // 첫 기록 시간
    val historyitemmodel: ArrayList<CommonTagItem>,
)


data class HistoryDetailTagModel(
    val historyItemTag: String,
)

data class HistoryDetailConversationModel(
    val historyText: String, // 기록 대화 텍스트
    val sendedTime: String, // 대화한 시간
    val isLeft: Boolean // 왼쪽 메시지 여부 (true: 왼쪽, false: 오른쪽)
)