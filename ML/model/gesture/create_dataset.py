import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np
import os

#  ★default★
# left1 = np.zeros((1,99))
# left = np.append(left, 1)
# default = np.zeros(15)
# default = np.append(default, 1)
# print(default.shape)
# aa = np.concatenate([np.zeros((21,4)).flatten(), default])
# print(aa)
# print(aa.shape)
file_root = "data/"
file_list = os.listdir(file_root)

actions = []
# seq_length = 30
# secs_for_action = 4

action_idx = 0

# hand
hand_model_path = "../../preprocessing/models/hand_landmarker.task"
hand_base_options = python.BaseOptions(model_asset_path=hand_model_path)
hand_options = vision.HandLandmarkerOptions(base_options=hand_base_options,
                                       num_hands=2)
mp_hands = vision.HandLandmarker.create_from_options(hand_options)


# pose
pose_model_path = "../../preprocessing/models/pose_landmarker_lite.task"
pose_base_options = python.BaseOptions(model_asset_path=pose_model_path)
pose_options = vision.PoseLandmarkerOptions(
    base_options=pose_base_options,
    output_segmentation_masks=True)
mp_pose = vision.PoseLandmarker.create_from_options(pose_options)



videos = []

for file in file_list:
    if file[-3:] == "mp4":
        videos.append(file)
    else:
        f = open(file_root+file, "r",encoding='UTF-8')
        # print(f.readline())
        words = f.readline().replace("(", ",").replace(")", ",").split(",")
        # print(f"words : {words[0]}")
        if words[0]:
            actions.append(words[0])
        elif words[2]:
            actions.append(words[2])
        else:
            actions.append(words[1])



print(f"videos: {videos}")
print(f"actions: {actions}")


def make_hand_angle(res):
    joint = np.zeros((21, 4))
    for j, lm in enumerate(res):
        joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

    # Compute angles between joints
    hand_v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
    hand_v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
    hand_v = hand_v2 - hand_v1 # [20, 3]
    # Normalize v
    hand_v = hand_v / np.linalg.norm(hand_v, axis=1)[:, np.newaxis]

    # Get angle using arcos of dot product
    angle = np.arccos(np.einsum('nt,nt->n',
        hand_v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:], 
        hand_v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

    angle = np.degrees(angle) # Convert radian to degree

    # angle_label = np.array([angle], dtype=np.float16)
    angle_label = np.array(angle, dtype=np.float16)
    # angle_label = np.append(angle_label, action_idx)
    # print(np.shape(angle_label))
    # print(angle_label)
    d = np.concatenate([joint.flatten(), angle_label])
    return d


os.makedirs('dataset', exist_ok=True)

for video in videos:
    cap = cv2.VideoCapture(file_root + video)
    video_data = []
    print(f"action: {actions[action_idx]}, file name : {video}")
    while cap.isOpened():
        # for idx, action in enumerate(actions):
        frame_data = []

        ret, img = cap.read()
        if not ret:
            break
        # img = cv2.flip(img, 1)

        img = mp.Image(image_format=mp.ImageFormat.SRGB, data=img)
        # img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        hand_result = mp_hands.detect(img)
        pose_result = mp_pose.detect(img)
        # img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)


# Hand
        if hand_result.hand_landmarks and pose_result.pose_landmarks:
            right_check = left_check = 0
            # print(np.shape(hand_result.hand_landmarks))
            # print(np.shape(pose_result.pose_landmarks))
            # print("hand 갯수", len(hand_result.hand_landmarks))
            # print("hand 방향", hand_result.handedness)
            # res = [res[1],res[0]] ★ 재홍님 코드 ★
            # print(hand_result.handedness[0][0].index) 
            # print(hand_result.handedness[0][0].category_name) 
            # hand_result.handedness[0].category_name -> left/right 
            
            if len(hand_result.hand_landmarks) == 2:
                if hand_result.handedness[0][0].category_name == hand_result.handedness[1][0].category_name:
                    if hand_result.handedness[0][0].score >= hand_result.handedness[1][0].score:
                        hand_result.handedness[1][0].category_name = 'Left' if hand_result.handedness[1][0].category_name == 'Right' else 'Right'
                    else:
                        hand_result.handedness[0][0].category_name = 'Left' if hand_result.handedness[0][0].category_name == 'Right' else 'Right'

            for hand_idx in range(len(hand_result.hand_landmarks)):
                res = hand_result.hand_landmarks[hand_idx]
                if len(hand_result.hand_landmarks) == 1:
                    default = np.zeros(15)
                    # default = np.append(default, action_idx)
                    aa = np.concatenate([np.zeros((21,4)).flatten(), default])
                    # ★★★★ 다시보기
                    if hand_result.handedness[0][0].category_name == 'Left':
                        # left_hand_data.append(make_hand_angle(res))
                        # frame_data += make_hand_angle(res)
                        frame_data = np.append(frame_data,make_hand_angle(res))
                        # right_hand_data.append(aa)
                        left_check += 1
                    else:
                        # right_hand_data += make_hand_angle(res)
                        frame_data = np.append(frame_data,make_hand_angle(res))
                        # frame_data += make_hand_angle(res)
                        right_check += 1
                        # left_hand_data.append(aa)
                else:
                    # print(hand_result.handedness[hand_idx][0].category_name,"12")
                    # print(hand_result.handedness[hand_idx][0].score,"34")
                    
                    if hand_result.handedness[hand_idx][0].category_name == 'Left' and hand_result.handedness[hand_idx][0].score >= 0.8:
                        # left_hand_data.append(make_hand_angle(res))
                        frame_data = np.append(frame_data,make_hand_angle(res))
                        # frame_data += make_hand_angle(res)
                        left_check += 1
                    elif hand_result.handedness[hand_idx][0].category_name == 'Right' and hand_result.handedness[hand_idx][0].score >= 0.8:
                        # right_hand_data.append(make_hand_angle(res))
                        frame_data = np.append(frame_data,make_hand_angle(res))
                        # frame_data += make_hand_angle(res)
                        right_check += 1
                    # print(f'left_check: {left_check}, right_check: {right_check}')
            if left_check == 0:
                default = np.zeros(15)
                # default = np.append(default, action_idx)
                aa = np.concatenate([np.zeros((21,4)).flatten(), default])
                # left_hand_data.append(aa)
                # frame_data += aa
                frame_data = np.append(frame_data, aa)
            if right_check == 0:
                default = np.zeros(15)
                # default = np.append(default, action_idx)
                aa = np.concatenate([np.zeros((21,4)).flatten(), default])
                frame_data = np.append(frame_data, aa)
                # frame_data += aa
                # right_hand_data.append(aa)

# pose
            for res in pose_result.pose_landmarks:
                joint = np.zeros((33, 4))
                for j, lm in enumerate(res):
                    joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

                # Compute angles between joints
                pose_v1 = joint[[14,12,24,13,11,23], :3] # Parent joint
                pose_v2 = joint[[16,14,12,15,13,11], :3] # Child joint
                pose_v = pose_v2 - pose_v1 # [20, 3]
                # Normalize v
                pose_v = pose_v / np.linalg.norm(pose_v, axis=1)[:, np.newaxis]

                # Get angle using arcos of dot product
                pose_angle = np.arccos(np.einsum('nt,nt->n',
                    pose_v[[0,1,3,4],:], 
                    pose_v[[1,2,4,5],:])) # [15,]

                pose_angle = np.degrees(pose_angle) # Convert radian to degree

                # pose_angle_label = np.array([pose_angle], dtype=np.float16)
                pose_angle_label = np.array(pose_angle, dtype=np.float16)
                # pose_angle_label = np.append(pose_angle_label, action_idx)

                d = np.concatenate([joint.flatten(), pose_angle_label])

                frame_data = np.append(frame_data, d)

                # face
                face_joint = np.zeros((33, 4))
                for j, lm in enumerate(res):
                    face_joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

                # Compute angles between joints
                face_v1 = joint[[0,4,5,6,0,1,2,3], :3] # Parent joint
                face_v2 = joint[[4,5,6,8,1,2,3,7], :3] # Child joint
                face_v = face_v2 - face_v1 # [20, 3]
                # Normalize v
                face_v = face_v / np.linalg.norm(face_v, axis=1)[:, np.newaxis]

                # Get angle using arcos of dot product
                face_angle = np.arccos(np.einsum('nt,nt->n',
                    face_v[[0,1,2,4,5,6],:], 
                    face_v[[1,2,3,5,6,7],:])) # [15,]

                face_angle = np.degrees(face_angle) # Convert radian to degree

                face_angle_label = np.array([face_angle], dtype=np.float16)
                # face_angle_label = np.array(face_angle, dtype=np.float16)
                face_angle_label = np.append(face_angle_label, action_idx)

                d = np.concatenate([face_joint.flatten(), face_angle_label])
                # print("face",d)
                # face_data.append(d)
                frame_data = np.append(frame_data,d) 

            video_data.append(frame_data)

    video_data = np.array(video_data)
    
    if video_data.size != 0:
        print("save!")
        np.save(os.path.join('dataset', f'seq_{actions[action_idx]}'), video_data)
        # np.save(os.path.join('dataset', f'seq_{actions[action_idx]}_{str(index).rjust(5, "0")}'), full_seq_data)
    action_idx += 1