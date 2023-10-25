package com.sts.sontalksign.feature.history

data class HistoryListModel(
    val historyTitle: String, // 기록 제목
    val EndedTime: Long, // 기록 끝난 시간
    val historyitemmodel: ArrayList<HistoryItemModel>,
)

data class HistoryItemModel(
    val historyItemTag: String,
)

data class HistoryDetailTagModel(
    val historyItemTag: String,
)

data class HistoryDetailConversationModel(
    val historyText: String, // 기록 대화 텍스트
    val SendedTime: String, // 대화한 시간
    val isLeft: Boolean // 왼쪽 메시지 여부 (true: 왼쪽, false: 오른쪽)
)