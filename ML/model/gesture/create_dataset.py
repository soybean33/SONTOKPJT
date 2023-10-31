import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
import numpy as np
import os

file_root = "./data/"
file_list = os.listdir(file_root)

actions = []
seq_length = 30
secs_for_action = 4

pose_model_path = "../../preprocessing/models/pose_landmarker.task"
face_model_path = "../../preprocessing/models/face_landmarker.task"
hand_model_path = "../../preprocessing/models/hand_landmarker.task"

mp_hands = mp.solutions.hands
mp_face = mp.solutions.face_mesh
mp_pose = mp.solutions.pose
mp_drawing = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    max_num_hands=1, min_detection_confidence=0.5, min_tracking_confidence=0.5
)
# faces = mp_face
# poses = mp_pose

videos = []

for file in file_list:
    if file[-3:] == "mp4":
        videos.append(file)
    else:
        f = open(file, "r")
        words = f[0].split(",")
        print(f"words : {words}")
        actions.append(words[0])


print(f"videos: {videos}")
print(f"actions: {actions}")

for video in videos:
    cap = cv2.VideoCapture(file_root + video)
    while True:
        if cap.isOpened():
            for idx, action in enumerate(actions):
                data = []

                ret, img = cap.read()

                img = cv2.flip(img, 1)
                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                result = hands.process(img)
                img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)
