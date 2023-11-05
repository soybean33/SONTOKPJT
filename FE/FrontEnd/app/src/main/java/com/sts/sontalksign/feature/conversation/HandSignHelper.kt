package com.sts.sontalksign.feature.conversation

import kotlin.math.*

class HandSignHelper() {

    val hand_1 : Array<Array<Double>> = Array(21, {Array(3, {0.0})})
    val hand_1_score : Double = 0.98
    var hand_1_category : String = "Left"

    val hand_2: Array<Array<Double>> = Array(21, {Array(3, {0.0})})
    val hand_2_score : Double = 0.99
    var hand_2_category : String = "Right"

    val face: Array<Array<Double>> = Array(11, {Array(3, {0.0})})
    val body: Array<Array<Double>> = Array(20, {Array(3, {0.0})})

    var returnArray : Array<Double> = emptyArray()

    private fun Init(){
        if(hand_1_category != hand_2_category) return

        if(hand_1_category == "Left" && hand_2_category == "Left") {
            if(hand_1_score > hand_2_score) {
                hand_1_category = "Left"
                hand_2_category = "Right"
            }
            else{
                hand_1_category = "Right"
                hand_2_category = "Left"
            }
        }
        else if(hand_1_category == "Right" && hand_2_category == "Right") {
            if(hand_1_score > hand_2_score) {
                hand_1_category = "Right"
                hand_2_category = "Left"
            }
            else{
                hand_1_category = "Left"
                hand_2_category = "Right"
            }
        }
    }
    private fun CalHand(hand : Array<Array<Double>>) {
        val hand_v1 : Array<Array<Double>> = arrayOf(hand[0], hand[1], hand[2], hand[3], hand[0],hand[5], hand[6], hand[7], hand[0],hand[9],hand[10], hand[11], hand[0], hand[13], hand[14], hand[15], hand[0], hand[17], hand[18], hand[19])
        val hand_v2 : Array<Array<Double>> = arrayOf(hand[1], hand[2], hand[3], hand[4], hand[5], hand[6], hand[7], hand[8], hand[9], hand[10], hand[11], hand[12], hand[13], hand[14], hand[15], hand[16], hand[17], hand[18], hand[19], hand[20])
        val hand: Array<Array<Double>> = Array(20) {Array(3){0.0}}
        val hand_d : Array<Double> = Array(20) {0.0}
        val hand_result: Array<Double> = Array(20) { 0.0 }

        for(i in 0..19) {
            for(j in 0..2) {
                hand[i][j] = hand_v2[i][j] - hand_v1[i][j]
            }
            hand_d[i] = hand[i][0] * hand[i][0] + hand[i][1] * hand[i][1] + hand[i][2] * hand[i][2] // 내적
            hand_result[i] = Math.acos(hand_d[i])   // 아크 코싸인
        }
    }

    private fun Solution(){
        Init()
        if(hand_1_category == "Left") {
            CalHand(hand_1)
            CalHand(hand_2)
        }
        else{
            CalHand(hand_2)
            CalHand(hand_1)
        }
    }
}
