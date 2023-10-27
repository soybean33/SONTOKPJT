package com.sts.sontalksign.feature.common

/**
 * 사용자 개인의 TAG명
 * 각 태그는 인덱스 번호와 태그명으로 저장합니다.
 * MVP 기준 인덱스는 최대 10개 저장 가능합니다.(0~9번)
 * MVP 기준 태그명은 최대 10글자까지 가능합니다.
 */
data class CommonTagItem (
    val tagInd: String, //태그의 인덱스 번호
    val tagText: String, //태그명
)