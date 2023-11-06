package com.sts.sontalksign.feature.conversation

import android.content.Context
import android.util.Log
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import kotlin.math.*

class HandSignHelper() {
    private val TAG : String = "HandSignHelper"

    var leftHand : Array<Array<Float>> = Array(21) {Array(3) {0f}}
    var rightHand: Array<Array<Float>> = Array(21) {Array(3) {0f}}
    var face: Array<Array<Float>> = Array(11) {Array(3) {0f}}
    var body: Array<Array<Float>> = Array(22) {Array(3) {0f}}

    var returnArray : Array<Double> = emptyArray()

    /** PoseLandmark 정형화 - 11개의 Face, 22개의 Body */
    fun initPose(poseResultBundle: PoseLandmarkerHelper.ResultBundle) {
        if(poseResultBundle.results.first().landmarks().size == 1) {
            for(i in 0..10) {
                if(poseResultBundle.results.first().landmarks()[0][i].presence().orElse(0f) < 0.5) {
                    face[i][0] = 0f
                    face[i][1] = 0f
                    face[i][2] = 0f

                } else {
                    face[i][0] = poseResultBundle.results.first().landmarks()[0][i].x()
                    face[i][1] = poseResultBundle.results.first().landmarks()[0][i].y()
                    face[i][2] = poseResultBundle.results.first().landmarks()[0][i].z()
                }
            }

            for(i in 11..32) {
                if(poseResultBundle.results.first().landmarks()[0][i].visibility().orElse(0f) < 0.5) {
                    body[i - 11][0] = 0f
                    body[i - 11][1] = 0f
                    body[i - 11][2] = 0f
                } else {
                    body[i - 11][0] = poseResultBundle.results.first().landmarks()[0][i].x()
                    body[i - 11][1] = poseResultBundle.results.first().landmarks()[0][i].y()
                    body[i - 11][2] = poseResultBundle.results.first().landmarks()[0][i].z()
                }
            }
        }

//        Log.d(TAG, "Face: " + face[0][0].toString())
        Log.d(TAG, "Body: ${body[0][0].toString()}, ${body[1][0].toString()}")
    }

    /** Hand Landmark 정형화 */
    fun initHand(handResultBundle: HandLandmarkerHelper.ResultBundle) {
        when(handResultBundle.results.first().handednesses().size) {
            /** 한손 입력 */
            1 -> {
                /** 오른손 입력이 들어왔다면 */
                if (handResultBundle.results.first()
                        .handednesses()[0][0].categoryName() == "Right"
                ) {
                    for (i in 0..20) {
                        rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                        rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                        rightHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                    }
                } else {
                    for (i in 0..20) {
                        leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                        leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                        leftHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                    }
                }
            }
            /** 두손 입력 */
            2 -> {
                var handA: String =
                    handResultBundle.results.first().handednesses()[0][0].categoryName()
                var handB: String =
                    handResultBundle.results.first().handednesses()[1][0].categoryName()
                val scoreA: Float = handResultBundle.results.first().handednesses()[0][0].score()
                val scoreB: Float = handResultBundle.results.first().handednesses()[1][0].score()

                if (handA.equals(handB)) {
                    if (handA.equals("Left")) {
                        if (scoreA >= scoreB) {
                            for (i in 0..20) {
                                leftHand[i][0] =
                                    handResultBundle.results.first().landmarks()[0][i].x()
                                leftHand[i][1] =
                                    handResultBundle.results.first().landmarks()[0][i].y()
                                leftHand[i][2] =
                                    handResultBundle.results.first().landmarks()[0][i].z()
                            }
                        } else {
                            for (i in 0..20) {
                                leftHand[i][0] =
                                    handResultBundle.results.first().landmarks()[1][i].x()
                                leftHand[i][1] =
                                    handResultBundle.results.first().landmarks()[1][i].y()
                                leftHand[i][2] =
                                    handResultBundle.results.first().landmarks()[1][i].z()
                            }
                        }
                    } else {
                        if (scoreA >= scoreB) {
                            for (i in 0..20) {
                                rightHand[i][0] =
                                    handResultBundle.results.first().landmarks()[0][i].x()
                                rightHand[i][1] =
                                    handResultBundle.results.first().landmarks()[0][i].y()
                                rightHand[i][2] =
                                    handResultBundle.results.first().landmarks()[0][i].z()
                            }
                        } else {
                            for (i in 0..20) {
                                rightHand[i][0] =
                                    handResultBundle.results.first().landmarks()[1][i].x()
                                rightHand[i][1] =
                                    handResultBundle.results.first().landmarks()[1][i].y()
                                rightHand[i][2] =
                                    handResultBundle.results.first().landmarks()[1][i].z()
                            }
                        }
                    }
                } else {
                    if (handA.equals("Left")) {
                        for (i in 0..20) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            leftHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                        }
                        for (i in 0..20) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            rightHand[i][2] = handResultBundle.results.first().landmarks()[1][i].z()
                        }

                    } else {
                        for (i in 0..20) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            rightHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                        }
                        for (i in 0..20) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            leftHand[i][2] = handResultBundle.results.first().landmarks()[1][i].z()
                        }
                    }
                }
            }
        }

        Log.d(TAG, "RightHand: ${rightHand.toString()}")
        Log.d(TAG, "LeftHand: ${leftHand.toString()}")
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

    }
}
