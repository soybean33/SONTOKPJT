package com.sts.sontalksign.feature.conversation

data class ConversationCameraModel(
    val ConversationText: String, // 기록 대화 텍스트
    val ConversationTime: String, // 대화한 시간
    val isLeft: Boolean // 왼쪽 메시지 여부 (true: 왼쪽, false: 오른쪽)
)