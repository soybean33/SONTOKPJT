package com.sts.sontalksign.feature.conversation

import android.app.Activity
import android.content.Context
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.acos


class HandSignHelper() {
    companion object {
        const val MODEL_CLASSIFIER = "sl_model.tflite"
    }

    private val TAG : String = "HandSignHelper"

    var leftHand : Array<Array<Float>> = Array(21) {Array(3) {0f}}
    var rightHand: Array<Array<Float>> = Array(21) {Array(3) {0f}}
    var pose: Array<Array<Float>> = Array(33) {Array(3) {0f}}

    var returnArray : Array<Double> = emptyArray()

    private var interpreter : Interpreter ?= null

    /** model 입력 데이터 관련 변수 */
    val frameDeque = ArrayList<FloatArray>().apply {
        repeat(5) {
            add(FloatArray(265) {0f})
        }
    }

    /** PoseLandmark 정형화 - 11개의 Face, 22개의 Body */
    fun initPose(poseResultBundle: PoseLandmarkerHelper.ResultBundle) {
        if(poseResultBundle.results.first().landmarks().size == 1) {
            for(i in 0 until 33) {
                if(poseResultBundle.results.first().landmarks()[0][i].presence().orElse(0f) < 0.5) {
                    pose[i][0] = 0f
                    pose[i][1] = 0f
                    pose[i][2] = 0f

                } else {
                    pose[i][0] = poseResultBundle.results.first().landmarks()[0][i].x()
                    pose[i][1] = poseResultBundle.results.first().landmarks()[0][i].y()
                    pose[i][2] = poseResultBundle.results.first().landmarks()[0][i].z()
                }
            }
        }

//        Log.d(TAG, "Body: ${pose[0][0].toString()}, ${pose[1][0].toString()}")
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
                    for (i in 0 until 21) {
                        rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                        rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                        rightHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                    }
                } else {
                    for (i in 0 until 21) {
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
                            for (i in 0 until 21) {
                                leftHand[i][0] =
                                    handResultBundle.results.first().landmarks()[0][i].x()
                                leftHand[i][1] =
                                    handResultBundle.results.first().landmarks()[0][i].y()
                                leftHand[i][2] =
                                    handResultBundle.results.first().landmarks()[0][i].z()
                            }
                        } else {
                            for (i in 0 until 21) {
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
                            for (i in 0 until 21) {
                                rightHand[i][0] =
                                    handResultBundle.results.first().landmarks()[0][i].x()
                                rightHand[i][1] =
                                    handResultBundle.results.first().landmarks()[0][i].y()
                                rightHand[i][2] =
                                    handResultBundle.results.first().landmarks()[0][i].z()
                            }
                        } else {
                            for (i in 0 until 21) {
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
                        for (i in 0 until 21) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            leftHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                        }
                        for (i in 0 until 21) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            rightHand[i][2] = handResultBundle.results.first().landmarks()[1][i].z()
                        }

                    } else {
                        for (i in 0 until 21) {
                            rightHand[i][0] = handResultBundle.results.first().landmarks()[0][i].x()
                            rightHand[i][1] = handResultBundle.results.first().landmarks()[0][i].y()
                            rightHand[i][2] = handResultBundle.results.first().landmarks()[0][i].z()
                        }
                        for (i in 0 until 21) {
                            leftHand[i][0] = handResultBundle.results.first().landmarks()[1][i].x()
                            leftHand[i][1] = handResultBundle.results.first().landmarks()[1][i].y()
                            leftHand[i][2] = handResultBundle.results.first().landmarks()[1][i].z()
                        }
                    }
                }
            }
        }

//        Log.d(TAG, "RightHand: ${rightHand.toString()}")
//        Log.d(TAG, "LeftHand: ${leftHand.toString()}")
    }

    /** HandLandmarker 필요한 데이터로 변경 */
    private fun calHand(hand: Array<Array<Float>>) : FloatArray {
        val startJoints = intArrayOf(0, 1, 2, 3, 0, 5, 6, 7, 0, 9, 10, 11, 0, 13, 14, 15, 0, 17, 18, 19)
        val destJoints = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)

        val hand_v1 = calculateIdx(hand, startJoints)
        val hand_v2 = calculateIdx(hand, destJoints)
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

        /** 각도 구하기 및 반환 */
        return calculateHandAngles(hand_v)
    }

    private  fun calPose(pose: Array<Array<Float>>) : FloatArray{
        val startJoints = intArrayOf(0, 4, 5, 6, 0, 1, 2, 3, 14, 12, 24, 13, 11, 23)
        val destJoints = intArrayOf(4, 5, 6, 8, 1, 2, 3, 7, 16, 14, 12, 15, 13, 11)

        val pose_v1 = calculateIdx(pose, startJoints)
        val pose_v2 = calculateIdx(pose, destJoints)
        var pose_v = Array(14) { Array(3) { 0f } }

        /** 크기 구하기 */
        for (i in 0 until 14) {
            for (j in 0 until 3) {
                pose_v[i][j] = pose_v2[i][j] - pose_v1[i][j]
            }
        }

        /** 정규화 */
        for (i in 0 until 14) {
            val norm = vectorNorm(pose_v[i])
            if (norm != 0f) {
                vectorDivideInPlace(pose_v[i], norm)
            }
        }

        /** 각도 구하기 */
        return calculatePoseAngles(pose_v)
    }

    private fun calculateIdx(hand: Array<Array<Float>>, indices: IntArray): Array<Array<Float>> {
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
    private fun calculateHandAngles(hand_v: Array<Array<Float>>): FloatArray {
        val angles = FloatArray(15)
        val joints1 = intArrayOf(0, 1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 16, 17, 18)
        val joints2 = intArrayOf(1, 2, 3, 5, 6, 7, 9, 10, 11, 13, 14, 15, 17, 18, 19)

        for (i in 0 until 15) {
            val dotProduct = vectorDot(hand_v[joints1[i]], hand_v[joints2[i]])
            angles[i] = acos(dotProduct)
        }

        return angles
    }

    private fun calculatePoseAngles(hand_v: Array<Array<Float>>): FloatArray {
        val angles = FloatArray(10)
        val joints1 = intArrayOf(0, 1, 2, 4, 5, 6, 8, 9, 11, 12)
        val joints2 = intArrayOf(1, 2, 3, 5, 6, 7, 9, 10, 12, 13)

        for (i in 0 until 10) {
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


    public fun Solution(context: Context){
        val result = FloatArray(265) {0f}

        /** leftHand 데이터 - point와 angle */
        for(i in 0 until 21) {
            for( j in 0 until 3) {
                result[i*3 + j] = leftHand[i][j]
            }
        }

        val resultLeftHand = calHand(leftHand)
        for(i in 0 until 15) {
            result[i + 63] = resultLeftHand[i]
        }

        /** rightHand 데이터 - point와 angle */
        for(i in 0 until 21) {
            for( j in 0 until 3) {
                result[78 + i * 3 + j] = rightHand[i][j]
            }
        }

        val resultRightHand = calHand(rightHand)
        for(i in 0 until 15) {
            result[i + 141] = resultRightHand[i]
        }

        /** pose 데이터 - point와 angle */
        for(i in 0 until 33) {
            for(j in 0 until 3) {
                result[156 + i  * 3 + j] = pose[i][j]
            }
        }

        val resultPose = calPose(pose)
        for(i in 0 until 10) {
            result[255 + i] = resultPose[i]
        }

        frameDeque.removeFirst()
        frameDeque.add(result)

        val output = Array(1) {
            FloatArray(2) { 0.0f }
        }

        val tflite = getTfliteInterpreter("sl_model.tflite", context)

        var input = convertArrayToByteBuffer(frameDeque)
        tflite.run(input, output)

        for(i in 0 until output.size) {
            for(j in 0 until output[i].size) {
                Log.d("output", "${i}, ${j} : ${output[i][j]}")
            }
        }
    }

    private fun convertArrayToByteBuffer(inputData: ArrayList<FloatArray>) : ByteBuffer {
        var byteBuffer: ByteBuffer = ByteBuffer.allocate(5 * 265 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        Log.d("byteBuffer", byteBuffer.capacity().toString())

        for(i in 0 until 5) {
            for(j in 0 until 265) {
                byteBuffer.putFloat(inputData[i][j])
            }
        }
        ///tensorflow =  53,000
        ///159,000
        return byteBuffer
    }

    private fun getTfliteInterpreter(modelPath: String, context: Context) : Interpreter{
        Log.d("modelPath 누구냐?", context.packageCodePath)

        val model : ByteBuffer = loadModelFile(context)
        model.order(ByteOrder.nativeOrder())
        interpreter = Interpreter(model)

        return interpreter!!
    }

    private fun loadModelFile(context: Context): ByteBuffer {
        val am : AssetManager = context.getAssets()
        val afd : AssetFileDescriptor = am.openFd(MODEL_CLASSIFIER)
        val fis : FileInputStream = FileInputStream(afd.fileDescriptor)
        val fc : FileChannel = fis.channel
        val startOffSet : Long = afd.startOffset
        val declaredLength : Long = afd.declaredLength

        return fc.map(FileChannel.MapMode.READ_ONLY, startOffSet, declaredLength)
    }
}

/**
 * leftHand 21 * 3 = 63 + 15 = 78
 * rightHand 상동 = 78
 * ==== 156
 * pose 33 * 3 = 99 + 10 = 109
 * ==== 265
 *
 * */
