import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np
import os

file_root = "data/"
file_list = os.listdir(file_root)

actions = []
seq_length = 30
secs_for_action = 4



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

os.makedirs('dataset', exist_ok=True)

for video in videos:
    cap = cv2.VideoCapture(file_root + video)
    index = 0
    print(f"file name : {video}")
    while cap.isOpened():
        # for idx, action in enumerate(actions):
        hand_data = []
        pose_data = []
        face_data = []

        ret, img = cap.read()
        if not ret:
            break
        # img = cv2.flip(img, 1)

        img = mp.Image(image_format=mp.ImageFormat.SRGB, data=img)
        # img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        hand_result = mp_hands.detect(img)
        pose_result = mp_pose.detect(img)
        # img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
        # hand
        if hand_result.hand_landmarks is not None:
            for res in hand_result.hand_landmarks:
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

                angle_label = np.array([angle], dtype=np.float16)
                angle_label = np.append(angle_label, action_idx)

                d = np.concatenate([joint.flatten(), angle_label])

                hand_data.append(d)

                # mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)
        # pose
        if pose_result.pose_landmarks is not None:
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
                angle = np.arccos(np.einsum('nt,nt->n',
                    pose_v[[0,1,3,4],:], 
                    pose_v[[1,2,4,5],:])) # [15,]

                angle = np.degrees(angle) # Convert radian to degree

                angle_label = np.array([angle], dtype=np.float16)
                angle_label = np.append(angle_label, action_idx)

                d = np.concatenate([joint.flatten(), angle_label])

                pose_data.append(d)

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
                face_angle_label = np.append(face_angle_label, action_idx)

                d = np.concatenate([face_joint.flatten(), face_angle_label])

                face_data.append(d)

                # mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)
        print(f'hand_data: {hand_data}')
        print(f'pose_data: {pose_data}')
        print(f'pose_data: {pose_data}')
        
        # cv2.imshow('img', img)
        hand_data = np.array(hand_data)
        pose_data = np.array(pose_data)
        face_data = np.array(face_data)
        print(actions[action_idx], hand_data.shape)
        print(actions[action_idx], pose_data.shape)
        print(actions[action_idx], face_data.shape)
        # np.save(os.path.join('dataset', f'raw_{actions[action_idx]}_{str(index).rjust(5, "0")}'), hand_data)
        # np.save(os.path.join('dataset', f'raw_{actions[action_idx]}_{str(index).rjust(5, "0")}'), pose_data)
        # np.save(os.path.join('dataset', f'raw_{actions[action_idx]}_{str(index).rjust(5, "0")}'), face_data)

        full_seq_data = []
        for seq in range(len(hand_data) - seq_length):
            full_seq_data.append(hand_data[seq:seq + seq_length])
        for seq in range(len(pose_data) - seq_length):
            full_seq_data.append(pose_data[seq:seq + seq_length])
        for seq in range(len(face_data) - seq_length):
            full_seq_data.append(face_data[seq:seq + seq_length])

        full_seq_data = np.array(full_seq_data)
        print(actions[action_idx], full_seq_data.shape)

        # np.save(os.path.join('dataset', f'seq_{actions[action_idx]}'), full_seq_data)
        np.save(os.path.join('dataset', f'seq_{actions[action_idx]}_{str(index).rjust(5, "0")}'), full_seq_data)
        index += 1
    action_idx += 1