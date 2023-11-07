package com.sts.sontalksign.feature.conversation

import android.util.Log
import kotlin.math.acos


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

    /** HandLandmarker 필요한 데이터로 변경 */
    private fun calHand(hand: Array<Array<Float>>) {
        val startJoints = intArrayOf(0, 1, 2, 3, 0, 5, 6, 7, 0, 9, 10, 11, 0, 13, 14, 15, 0, 17, 18, 19)
        val destJoints = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        val hand_v1 = calHandV(hand, startJoints)
        val hand_v2 = calHandV(hand, destJoints)
        var hand_v = Array(20) { Array(3) { 0f } }

        /** 크기 구하기 */
        for (i in 0 until 20) {
            for (j in 0 until 3) {
                hand_v[i][j] = hand_v2[i][j] - hand_v1[i][j]
            }
        }

        /** 정규화 */
        for (i in 0 until 20) {
            val norm = vectorNorm(hand_v[i])
            if (norm != 0f) {
                vectorDivideInPlace(hand_v[i], norm)
            }
        }

        /** 각도 구하기 */
        val angles = calculateAngles(hand_v)
    }

    private fun calHandV(hand: Array<Array<Float>>, indices: IntArray): Array<Array<Float>> {
        val hand_v = Array(indices.size) { Array(3) { 0f } }
        for (i in indices.indices) {
            val index = indices[i]
            for (j in 0 until 3) {
                hand_v[i][j] = hand[index][j]
            }
        }
        return hand_v
    }

    /** 정규화 함수 */
    private inline fun vectorNorm(vector: Array<Float>): Float {
        var sum = 0f
        for (element in vector) {
            sum += element * element
        }
        return kotlin.math.sqrt(sum)
    }

    /** 벡터 나눗셈 */
    private inline fun vectorDivideInPlace(vector: Array<Float>, scalar: Float) {
        for (i in vector.indices) {
            vector[i] /= scalar
        }
    }

    /** 벡터 내적 */
    private fun calculateAngles(hand_v: Array<Array<Float>>): FloatArray {
        val angles = FloatArray(15)
        val joints1 = intArrayOf(0, 1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 16, 17, 18)
        val joints2 = intArrayOf(1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15, 17, 18, 19)

        for (i in 0 until 15) {
            val dotProduct = vectorDot(hand_v[joints1[i]], hand_v[joints2[i]])
            angles[i] = acos(dotProduct)
        }

        return angles
    }

    private inline fun vectorDot(vector1: Array<Float>, vector2: Array<Float>): Float {
        var dotProduct = 0f
        for (i in vector1.indices) {
            dotProduct += vector1[i] * vector2[i]
        }
        return dotProduct
    }


    private fun Solution(){

    }
}
