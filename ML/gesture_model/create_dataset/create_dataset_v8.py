import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np
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


def create_data(file_root:str, file_list:list, hand_model_path: str, pose_model_path:str, dataset_type:str, dataset_save_path:str) -> None:
    # 동작 라벨 모음 actions_save :  파일 저장용 
    actions = {}
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

    # video file load
    for file in file_list:
        if file[-3:] == "mp4":
            videos.append(file)
            # print(file.split()[0].split("_")[1])
            file_str =file.split()[0].split("_") 

            act_key = file_str[1] if len(file_str) >=2 else ''
            if act_key not in actions.keys():
                actions[act_key] = action_idx
                action_idx += 1

    print(f"videos: {videos}")
    print(f"actions: {actions}")

    # mediapipe hand model load
    hand_base_options = python.BaseOptions(model_asset_path=hand_model_path)
    hand_options = vision.HandLandmarkerOptions(
        base_options=hand_base_options,
        running_mode=vision.RunningMode.VIDEO,
        num_hands=2)

    # mediapipe pose model load
    pose_base_options = python.BaseOptions(model_asset_path=pose_model_path)
    pose_options = vision.PoseLandmarkerOptions(
        base_options=pose_base_options,
        running_mode=vision.RunningMode.VIDEO,
        output_segmentation_masks=True)


    os.makedirs('dataset', exist_ok=True)
    
    for video in videos:
        video_label = video.split()[0].split("_")[1]
        print(video_label)

        # mediapipe model set
        mp_hands = vision.HandLandmarker.create_from_options(hand_options)
        mp_pose = vision.PoseLandmarker.create_from_options(pose_options)

        cap = cv2.VideoCapture(file_root + video)

        # video frame data
        video_data = []


        print(f"action: {actions[video_label]}, file name : {video}")
        print(f'real save label idx is :{label_idx}')
        
        
        while cap.isOpened():
            frame_data = []
            ret, img = cap.read()
            if not ret:
                break
            img = mp.Image(image_format=mp.ImageFormat.SRGB, data=img)
            hand_result = mp_hands.detect_for_video(img, int(cap.get(cv2.CAP_PROP_POS_MSEC)))
            pose_result = mp_pose.detect_for_video(img, int(cap.get(cv2.CAP_PROP_POS_MSEC)))

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

                if dataset_type == 'train':
                    frame_data = np.append(frame_data, actions[video_label])
                # frame의 angle 저장
                video_data.append(frame_data)

        # frame 전체 video 데이터로 저장 후 형태 변환 list -> ndarray
        video_data = np.array(video_data)
        # video_data = np.append(video_data, )
        
        # frame이 10개 이상일 때만 저장
        if video_data.size >= 10:
            seq_length = 5
            full_seq_data = []
            for seq in range(len(video_data) - seq_length):
                full_seq_data.append(video_data[seq:seq + seq_length])

            full_seq_data = np.array(full_seq_data)
            print(actions[video_label], full_seq_data.shape)
            np.save(os.path.join(dataset_save_path, f'seq_{video[0:-4]}'), full_seq_data)

            label_idx += 1
            print(f'next label is :{label_idx}')
        else:
            print("zero landmarkers is not saved!")
    # 라벨 데이터 저장
    if dataset_type == "train":
        np.save(os.path.join('dataset', f'labels'), sorted(list(actions.keys()), key=lambda x:actions[x]))

    return


# 경로 설정
def dir_setting() -> list:
    # default
    file_root = "data/"
    dataset_save_path = "dataset/"
    dataset_type = "train"
    hand_model_path = "mediapipe_models/"
    pose_model_path = "mediapipe_models/"
    pose_model_bundle = "heavy"

    # input 을 받아 설정
    type_input = input("data set type (train / test, default: train) :").strip()
    dataset_save_path_input = input("dataset save path(default : dataset/) :").strip()
    root_input = input("data file root (default : data/): ").strip()
    hand_model_path_input = input("hand model path(mediapipe_models/): ").strip()
    pose_model_path_input = input("pose model path(mediapipe_models/): ").strip()
    pose_model_bundle_input = input("pose model bundle (lite / full / heavy, default: heavy)").strip()

    # input이 있을 경우만 설정
    if type_input != '':
        dataset_type = type_input
    if dataset_save_path_input != '':
        dataset_save_path = dataset_save_path_input
    if root_input != '':
        file_root = root_input
    if hand_model_path_input != '':
        hand_model_path = hand_model_path_input
    if pose_model_path_input != '':
        pose_model_path = pose_model_path_input
    if pose_model_bundle_input != '':
        pose_model_bundle = pose_model_bundle_input
    hand_model_path +=  'hand_landmarker.task'
    pose_model_path +=  f'pose_landmarker_{pose_model_bundle}.task'
    file_list = os.listdir(file_root)

    return [file_root, file_list, hand_model_path, pose_model_path, dataset_type, dataset_save_path]


if __name__ == "__main__":
    create_data(*dir_setting())
