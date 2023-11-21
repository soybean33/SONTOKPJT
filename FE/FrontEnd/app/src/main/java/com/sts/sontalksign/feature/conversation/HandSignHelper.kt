package com.sts.sontalksign.feature.conversation

import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.acos

class HandSignHelper() {
    private val TAG : String = "HandSignHelper"

    var leftHand : Array<Array<Float>> = Array(21) {Array(2) {0f}} /** 왼손 RAW 좌표 */
    var rightHand: Array<Array<Float>> = Array(21) {Array(2) {0f}} /** 오른손 RAW 좌표 */
    var pose: Array<Array<Float>> = Array(33) {Array(2) {0f}} /** 포즈 RAW 좌표 */

    var lag: Int = 1
    
    /** model 입력 데이터 관련 변수 */
    /** model에 들어가는 입력값은 frameDeque[5][190] 크기의 2차원 배열을 한차원 감싼 형태 */
    val frameDeque = ArrayList<FloatArray>().apply {
        repeat(lag) {
            add(FloatArray(190) {0f})
        }
    }
    
    /** 확률을 출력되는 값으로 변경 */
    var signWords : Array<String> = arrayOf("", ".", "끝내다", "담당", "발표", "제", "준비", "?")
    val signWordSize : Int = signWords.size
    var wordQueue : Array<String> = arrayOf(".", "1", "2", "3", "4", "5", "6", "7")
    val wordCounterMap : MutableMap<String, Int> = mutableMapOf("." to 0, "1" to 0, "2" to 0, "3" to 0, "4" to 0, "5" to 0, "6" to 0, "7" to 0)

    /** 변경해보며 적용해 봐야하는 임계값들 */
    val probabilityThreshold: Float = 0.8f
    val counterThreshold: Int = 10

    /** PoseLandmark 정형화 - 33개의 Pose = 11개의 Face + 22개의 Body */
    fun initPose(poseResultBundle: PoseLandmarkerHelper.ResultBundle) {
        if(poseResultBundle.results.first().landmarks().size == 1) {
            var poseFlag = true
            for(i in 0 until 33) {                
                pose[i][0] = poseResultBundle.results.first().landmarks()[0][i].x()
                pose[i][1] = poseResultBundle.results.first().landmarks()[0][i].y()
                if(pose[i][0] < 0 || pose[i][1] < 0 || pose[i][0] > 1 || pose[i][1] > 1) {
                    poseFlag = false
                    break
                }
            }

            if(!poseFlag){
                for(i in 0 until 33) {
                    for(j in 0 until  2) {
                        pose[i][j] = 0f
                    }
                }
            }
        }
    }

    /** Hand Landmark 정형화 - 21개의 Hand * 2 */
    fun initHand(handResultBundle: HandLandmarkerHelper.ResultBundle) {
        var leftHandFlag = true
        var rightHandflag = true
        when(handResultBundle.results.first().handednesses().size) {
            /** 한손 입력 */
            1 -> {
                /** 오른손 입력이 들어왔다면 */
                if (handResultBundle.results.first().handednesses()[0][0].categoryName() == "Right") {
                    for (i in 0 until 21) {
                        rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                        rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                        if(rightHand[i][0] < 0 || rightHand[i][1] < 0 || rightHand[i][0] > 1 || rightHand[i][1] > 1) {
                            rightHandflag = false
                            break
                        }
                    }
                } else { /** 왼손 입력이 들어왔다면 */
                    for (i in 0 until 21) {
                        leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                        leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                        if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                            leftHandFlag = false
                            break
                        }
                    }
                }
            }
            /** 두손 입력 */
            2 -> {
                var handA: String = handResultBundle.results.first().handednesses()[0][0].categoryName()
                var handB: String = handResultBundle.results.first().handednesses()[1][0].categoryName()
                val scoreA: Float = handResultBundle.results.first().handednesses()[0][0].score()
                val scoreB: Float = handResultBundle.results.first().handednesses()[1][0].score()

                /** 같은 쪽 손 두개가 입력된 경우 */
                if (handA.equals(handB)) {
                    if (handA.equals("Left")) { /** 두 손 모두 왼손 인 경우 */
                        /**  score가 더 높은 쪽이 왼손 */
                        if (scoreA >= scoreB) { /** scoreA가 더 크거나 같다면 0번 인덱스가 왼손 */
                            for (i in 0 until 21) {
                                leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                                leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                                if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                                    leftHandFlag = false
                                    break
                                }
                            }
                        } else { /** scoreB가 더 크다면 1번 인덱스가 왼손 */
                            for (i in 0 until 21) {
                                leftHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                                leftHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                                if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                                    leftHandFlag = false
                                    break
                                }
                            }
                        }
                    } else {    /** 두 손 모두 오른손 인 경우 */
                        if (scoreA >= scoreB) { /** scoreA가 더 크거나 같다면 0번 인덱스가 오른손 */
                            for (i in 0 until 21) {
                                rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                                rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                                if(rightHand[i][0] < 0 || rightHand[i][1] < 0 || rightHand[i][0] > 1 || rightHand[i][1] > 1) {
                                    rightHandflag = false
                                    break
                                }
                            }
                        } else {    /** scoreB가 더 크다면 1번 인덱스가 오른손 */
                            for (i in 0 until 21) {
                                rightHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                                rightHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                                if(rightHand[i][0] < 0 || rightHand[i][1] < 0 || rightHand[i][0] > 1 || rightHand[i][1] > 1) {
                                    rightHandflag = false
                                    break
                                }
                            }
                        }
                    }
                } else {    /** 다른손이 입력된 경우 */
                    if (handA.equals("Left")) { /** 0번 인덱스가 왼손 */
                        for (i in 0 until 21) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                                leftHandFlag = false
                                break
                            }
                        }
                        for (i in 0 until 21) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                                leftHandFlag = false
                                break
                            }
                        }

                    } else { /** 1번 인덱스가 오른손 */
                        for (i in 0 until 21) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            if(rightHand[i][0] < 0 || rightHand[i][1] < 0 || rightHand[i][0] > 1 || rightHand[i][1] > 1) {
                                rightHandflag = false
                                break
                            }
                        }
                        for (i in 0 until 21) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            if(leftHand[i][0] < 0 || leftHand[i][1] < 0 || leftHand[i][0] > 1 || leftHand[i][1] > 1) {
                                leftHandFlag = false
                                break
                            }
                        }
                    }
                }
            }
        }

        if(!leftHandFlag) {
            for(i in 0 until 21) {
                for(j in 0 until  2) {
                    leftHand[i][j] = 0f
                }
            }
        }

        if(!rightHandflag) {
            for(i in 0 until 21) {
                for(j in 0 until  2) {
                    rightHand[i][j] = 0f
                }
            }
        }
    }

    /** HandLandmarker 필요한 데이터로 변경 */
    private fun calHand(hand: Array<Array<Float>>) : FloatArray {
        /** 각도를 구할 20개의 점 */
        val startJoints = intArrayOf(0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19)
        val destJoints = intArrayOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)

        val hand_v1 : Array<Array<Float>> = calculateIdx(hand, startJoints)
        val hand_v2 : Array<Array<Float>> = calculateIdx(hand, destJoints)
        var hand_v = Array(20) { Array(2) { 0f } }  /** 2개의 20개의 점 */

        /** 크기 구하기 */
        for (i in 0 until 20) {
            for (j in 0 until 2) {
                hand_v[i][j] = hand_v2[i][j] - hand_v1[i][j]
            }
        }

        /** 정규화 */
        for (i in 0 until 20) {
            val norm = vectorNorm(hand_v[i])
            if (norm != 0f) { vectorDivideInPlace(hand_v[i], norm) }
        }

        /** 각도 구하기 및 반환 */
        return calculateHandAngles(hand_v)
    }

    /** PoseLandmarker 필요한 데이터로 변경 */
    private  fun calPose(pose: Array<Array<Float>>) : FloatArray{
        /** 각도를 구할 15개의 점 */
        val startJoints = intArrayOf(0,4,5,6,0,1,2,3,12,14,11,13,11,23)
        val destJoints = intArrayOf(4,5,6,8,1,2,3,7,14,16,13,15,12,24)

        val pose_v1:  Array<Array<Float>> = calculateIdx(pose, startJoints)
        val pose_v2:  Array<Array<Float>> = calculateIdx(pose, destJoints)
        var pose_v = Array(14) { Array(2) { 0f } }  /** 2개의 15개의 점 */

        /** 크기 구하기 */
        for (i in 0 until 14) {
            for (j in 0 until 2) {
                pose_v[i][j] = pose_v2[i][j] - pose_v1[i][j]
            }
        }

        /** 정규화 */
        for (i in 0 until 14) {
            val norm = vectorNorm(pose_v[i])
            if (norm != 0f) { vectorDivideInPlace(pose_v[i], norm) }
        }

        /** 각도 구하기 */
        return calculatePoseAngles(pose_v)
    }

    private fun calculateIdx(hand: Array<Array<Float>>, indices: IntArray): Array<Array<Float>> {
        val hand_v = Array(indices.size) { Array(2) { 0f } }
        for (i in indices.indices) {
            val index = indices[i]
            for (j in 0 until 2) {
                hand_v[i][j] = hand[index][j]
            }
        }
        return hand_v
    }

    /** 정규화 함수 */
    private inline fun vectorNorm(vector: Array<Float>): Float {
        var sum = 0f
        for (element in vector) { sum += element * element }
        return kotlin.math.sqrt(sum)
    }

    /** 벡터 나눗셈 */
    private inline fun vectorDivideInPlace(vector: Array<Float>, scalar: Float) {
        for (i in vector.indices) { vector[i] /= scalar } /** 참조에 의한 전달 */
    }

    /** 손 벡터 내적 */
    private fun calculateHandAngles(hand_v: Array<Array<Float>>): FloatArray {
        val angles = FloatArray(15)
        val joints1 = intArrayOf(0,1,2,4,5,6,8,9,10,12,13,14,16,17,18)
        val joints2 = intArrayOf(1,2,3,5,6,7,9,10,11,13,14,15,17,18,19)

        for (i in 0 until 15) {
            val dotProduct = vectorDot(hand_v[joints1[i]], hand_v[joints2[i]])
            angles[i] = acos(dotProduct)
        }

        return angles
    }

    /** 포즈 벡터 내적 */
    private fun calculatePoseAngles(hand_v: Array<Array<Float>>): FloatArray {
        val angles = FloatArray(10)
        val joints1 = intArrayOf(0,1,2,4,5,6,12,8,12,10)
        val joints2 = intArrayOf(1,2,3,5,6,7,8,9,10,11)

        for (i in 0 until 10) {
            val dotProduct = vectorDot(hand_v[joints1[i]], hand_v[joints2[i]])
            angles[i] = acos(dotProduct)
        }

        return angles
    }

    /** 벡터 크기 구하기 */
    private inline fun vectorDot(vector1: Array<Float>, vector2: Array<Float>): Float {
        var dotProduct = 0f
        for (i in vector1.indices) { dotProduct += vector1[i] * vector2[i] }
        return dotProduct
    }

    fun Solution() : ArrayList<FloatArray> {
        val result = FloatArray(190) {0f}

        /** leftHand 데이터 - point와 angle */
        for(i in 0 until 21) {
            for( j in 0 until 2) {
                result[i * 2 + j] = leftHand[i][j]
            }
        }

        val resultLeftHand = calHand(leftHand)
        for(i in 0 until 15) {
            result[i + 42] = (resultLeftHand[i]  * (180.0 / Math.PI)).toFloat()
        }

        /** rightHand 데이터 - point와 angle */
        for(i in 0 until 21) {
            for( j in 0 until 2) {
                result[57 + i * 2 + j] = rightHand[i][j]
            }
        }

        val resultRightHand = calHand(rightHand)
        for(i in 0 until 15) {
            result[i + 99] = (resultRightHand[i] * (180.0 / Math.PI)).toFloat()
        }

        /** pose 데이터 - point와 angle */
        for(i in 0 until 33) {
            for(j in 0 until 2) {
                result[114 + i  * 2 + j] = pose[i][j]
            }
        }

        val resultPose = calPose(pose)
        for(i in 0 until 10) {
            result[180 + i] = (resultPose[i] * (180.0 / Math.PI)).toFloat()
        }

        frameDeque.add(result)
        frameDeque.removeFirst() /** 먼저 추가하고 제거하는 것이 out of bound를 방지할 수 있을 듯 */

        return frameDeque
    }

    private fun getWordIndex(predictionResult: Array<Float>): Int {
        var maxIndex: Int = 0
        var maxProbability: Float = 0f
        for (index in 0 until predictionResult.size) {
            if (maxProbability < predictionResult[index]) {
                maxIndex = index
                maxProbability = predictionResult[index]
            }
        }

        return maxIndex
    }

    fun wordQueueManager(predictionResult: Array<Float>): String {
        var wordIndex: Int = getWordIndex(predictionResult)
        var signWord: String
        if (predictionResult[wordIndex] < probabilityThreshold) {
            signWord = ""
        } else {
            signWord = signWords[wordIndex]
        }
        var signWordExistInQueue: Boolean = false
        var minIndex: Int = 0
        var minCount: Int = counterThreshold
        for (index in 0 until wordQueue.size) {
            if (wordQueue[index] == signWord) {
                signWordExistInQueue = true
                minIndex = index
                break
            } else {
                if (wordCounterMap[wordQueue[index]]!! < minCount) {
                    minIndex = index
                    minCount = wordCounterMap[wordQueue[index]]!!
                }
            }
        }
        var signWordVerified: Boolean = false
        if (signWordExistInQueue) {
            if (wordCounterMap[signWord]!! == counterThreshold - 1) {
                signWordVerified = true
            }
        } else {
            wordCounterMap[wordQueue[minIndex]] = 0
            wordQueue[minIndex] = signWord
            wordCounterMap[signWord] = 0
        }
        for (word in wordQueue) {
            if (signWordVerified && counterThreshold < wordCounterMap[word]!!) {
                wordCounterMap[word] = counterThreshold
            }
            wordCounterMap[word] = (wordCounterMap[word]!! - 1).coerceAtLeast(0)
        }
        if (signWordExistInQueue) {
            wordCounterMap[signWord] = wordCounterMap[signWord]!! + 2
        } else {
            wordCounterMap[signWord] = 1
        }
        if (signWordVerified) {
            return signWord
        }
        return ""
    }

    fun dataSize() : Int {
        return signWordSize
    }
}

/**
 * leftHand 21 * 2 = 42 + 15 = 57
 * rightHand 상동 = 57
 * ==== 114
 * pose 33 * 2 = 66 + 10 = 76
 * ==== 57 + 57 + 76 = 190
 *
 * */
