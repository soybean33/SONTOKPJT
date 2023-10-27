package com.sts.sontalksign.global

import java.text.SimpleDateFormat

/**
 * 대화내용 기록용의 DateFormat
 */
object FileFormats {
    val tagFileName: String = "TAGS.txt" //태그정보 파일명
    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //대화내용 텍스트파일의 파일명 형식
    val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm") //각 대화의 시간 형식
}