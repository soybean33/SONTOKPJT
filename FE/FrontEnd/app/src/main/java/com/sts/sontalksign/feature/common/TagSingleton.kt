package com.sts.sontalksign.feature.common

/**
 * 대화 기록용 TAG
 * 사용자별로 TAG 정보는 유일하므로 Singleton으로 정의합니다.
 * 각 TAG는 2가지로 나뉩니다.
 * 1) 사용자 개인의 TAG명
 * 2) 시스템 정의된 색상
 */
object TagSingleton {
    val tagList = ArrayList<CommonTagItem>() //사용자의 TAG명
    val colorList = ArrayList<Int>() //시스템의 색상
}