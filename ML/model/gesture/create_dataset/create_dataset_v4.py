import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np
import tensorflow as tf
import os

def calculate_angle(
        res:list, 
        start_joint_index:list, 
        destination_joint_index:list, 
        angle_index_list_1:list, 
        angle_index_list_2:list,
                    ) -> np.ndarray:
    # default point 설정
    # joint = np.zeros((21, 3))
    joint = np.zeros((len(res), 3))
    # 값에서 대입
    for j, lm in enumerate(res):
        joint[j] = [lm.x, lm.y, lm.z]
     
    # 각도 계산
    # Compute angles between joints    
    hand_v1 = joint[start_joint_index, :3]
    hand_v2 = joint[destination_joint_index, :3]
    
    hand_v = hand_v2 - hand_v1
    # Normalize v
    hand_v = hand_v / np.linalg.norm(hand_v, axis=1)[:, np.newaxis]

    # Get angle using arcos of dot product
    # 내적 후 arc cos
    angle = np.arccos(np.einsum('nt,nt->n',
        hand_v[angle_index_list_1,:], 
        hand_v[angle_index_list_2,:])) # [15,]
    # radian -> degree
    angle = np.degrees(angle) # Convert radian to degree
    # make ndarray
    angle_label = np.array(angle, dtype=np.float16)
    # joint와 angle을 하나의 ndarray로 저장
    d = np.concatenate([joint.flatten(), angle_label])
  
    return d


def create_data(file_root:str, file_list:list, hand_model_path: str, pose_model_path:str) -> None:
    # 동작 라벨 모음 actions_save :  파일 저장용 
    actions = []
    actions_save = []
    # video 데이터 모음 (.mp4)
    videos = []


    # hand joint setting
    hand_start_joint_index = [0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19]
    hand_destination_joint_index = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20]
    hand_angle_index_list_1 = [0,1,2,4,5,6,8,9,10,12,13,14,16,17,18]
    hand_angle_index_list_2 = [1,2,3,5,6,7,9,10,11,13,14,15,17,18,19]


    # pose joint setting
    pose_start_joint_index = [0,4,5,6,0,1,2,3,14,12,24,13,11,23]
    pose_destination_joint_index = [4,5,6,8,1,2,3,7,16,14,12,15,13,11]
    pose_angle_index_list_1 = [0,1,2,4,5,6,8,9,11,12]
    pose_angle_index_list_2 = [1,2,3,5,6,7,9,10,12,13]

    # action_idx : 동작 라벨의 index, label_index : dataset 안에 설정되는 index
    action_idx = label_idx = 0
    
    # mediapipe hand model load
    hand_base_options = python.BaseOptions(model_asset_path=hand_model_path)
    hand_options = vision.HandLandmarkerOptions(base_options=hand_base_options,
                                           num_hands=2)
    mp_hands = vision.HandLandmarker.create_from_options(hand_options)

    # mediapipe pose model load
    pose_base_options = python.BaseOptions(model_asset_path=pose_model_path)
    pose_options = vision.PoseLandmarkerOptions(
        base_options=pose_base_options,
        output_segmentation_masks=True)
    mp_pose = vision.PoseLandmarker.create_from_options(pose_options)

    # video file load
    for file in file_list:
        if file[-3:] == "mp4":
            videos.append(file)
        else:
            f = open(file_root+file, "r",encoding='UTF-8')
            words = f.readline().replace("(", ",").replace(")", ",").split(",")
            if words[0]:
                actions.append(words[0])
            elif words[2]:
                actions.append(words[2])
            else:
                actions.append(words[1])

    print(f"videos: {videos}")
    print(f"actions: {actions}")

    os.makedirs('dataset', exist_ok=True)
    
    for video in videos:
        cap = cv2.VideoCapture(file_root + video)

        # video frame data
        video_data = []


        print(f"action: {actions[action_idx]}, file name : {video}")
        print(f'real save label idx is :{label_idx}')
        
        
        while cap.isOpened():
            frame_data = []
            ret, img = cap.read()
            if not ret:
                break
            img = mp.Image(image_format=mp.ImageFormat.SRGB, data=img)
            hand_result = mp_hands.detect(img)
            pose_result = mp_pose.detect(img)

    # Hand
            if hand_result.hand_landmarks and pose_result.pose_landmarks:   # 둘 다 있을 때만 저장
                # 양손이 모두 있는지 체크용
                right_check = left_check = 0
                # 두 손이 체크되었을 때
                if len(hand_result.hand_landmarks) == 2:
                    # 둘다 같은 손으로 판별되면
                    if hand_result.handedness[0][0].category_name == hand_result.handedness[1][0].category_name:
                        # 정확도가 낮은 쪽을 반대손으로 세팅
                        if hand_result.handedness[0][0].score >= hand_result.handedness[1][0].score:
                            hand_result.handedness[1][0].category_name = 'Left' if hand_result.handedness[1][0].category_name == 'Right' else 'Right'
                        else:
                            hand_result.handedness[0][0].category_name = 'Left' if hand_result.handedness[0][0].category_name == 'Right' else 'Right'
                # hand landmarker angle 저장
                for hand_idx in range(len(hand_result.hand_landmarks)):
                    res = hand_result.hand_landmarks[hand_idx]
                    if len(hand_result.hand_landmarks) == 1:
                        default = np.zeros(15)
                        aa = np.concatenate([np.zeros((21,3)).flatten(), default])
                        if hand_result.handedness[0][0].category_name == 'Left':
                            frame_data = np.append(frame_data,calculate_angle(res, hand_start_joint_index, hand_destination_joint_index, hand_angle_index_list_1, hand_angle_index_list_2))
                            left_check += 1
                        else:
                            frame_data = np.append(frame_data,calculate_angle(res, hand_start_joint_index, hand_destination_joint_index, hand_angle_index_list_1, hand_angle_index_list_2))
                            right_check += 1
                    else:
                        if hand_result.handedness[hand_idx][0].category_name == 'Left' and hand_result.handedness[hand_idx][0].score >= 0.8:
                            frame_data = np.append(frame_data,calculate_angle(res, hand_start_joint_index, hand_destination_joint_index, hand_angle_index_list_1, hand_angle_index_list_2))
                            left_check += 1
                        elif hand_result.handedness[hand_idx][0].category_name == 'Right' and hand_result.handedness[hand_idx][0].score >= 0.8:
                            frame_data = np.append(frame_data,calculate_angle(res, hand_start_joint_index, hand_destination_joint_index, hand_angle_index_list_1, hand_angle_index_list_2))
                            right_check += 1
                # 없는 손 default angle 저장
                if left_check == 0:
                    default = np.zeros(15)
                    aa = np.concatenate([np.zeros((21,3)).flatten(), default])
                    frame_data = np.append(frame_data, aa)
                if right_check == 0:
                    default = np.zeros(15)
                    aa = np.concatenate([np.zeros((21,3)).flatten(), default])
                    frame_data = np.append(frame_data, aa)

    # pose
                # pose anlge 계산 후 저장
                for res in pose_result.pose_landmarks:
                    frame_data = np.append(frame_data, calculate_angle(res, pose_start_joint_index, pose_destination_joint_index, pose_angle_index_list_1, pose_angle_index_list_2))

                # frame의 angle 저장
                video_data.append(frame_data)

        # frame 전체 video 데이터로 저장 후 형태 변환 list -> ndarray
        video_data = np.array(video_data)
        
        # frame이 10개 이상일 때만 저장
        if video_data.size >= 10:
            print("saved!")
            np.save(os.path.join('dataset', f'seq_{actions[action_idx]}'), video_data)
            actions_save.append(actions[action_idx])
            label_idx += 1
            print(f'next label is :{label_idx}')
            # np.save(os.path.join('dataset', f'seq_{actions[action_idx]}_{str(index).rjust(5, "0")}'), full_seq_data)
        else:
            print("zero landmarkers is not saved!")
        action_idx += 1
    # 라벨 데이터 저장
    np.save(os.path.join('dataset', f'labels'), actions_save)

    return


# 경로 설정
def dir_setting() -> list:
    # default
    file_root = "data/"
    hand_model_path = "mediapipe_models/hand_landmarker.task"
    pose_model_path = "mediapipe_models/pose_landmarker_lite.task"

    # input 을 받아 설정
    root_input = input("data file root: ").strip()
    hand_model_path_input = input("hand model path: ").strip()
    pose_model_path_input = input("pose model path: ").strip()

    # input이 있을 경우만 설정
    if root_input != '':
        file_root = root_input
    if hand_model_path_input != '':
        hand_model_path = hand_model_path_input
    if pose_model_path_input != '':
        pose_model_path = pose_model_path_input
    
    file_list = os.listdir(file_root)
    
    return [file_root, file_list, hand_model_path, pose_model_path]


if __name__ == "__main__":
    create_data(*dir_setting())