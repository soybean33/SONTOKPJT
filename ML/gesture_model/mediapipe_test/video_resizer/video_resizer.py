import cv2
import os
import numpy as np

file_root = './231108_Test'
output_root = './231108_Resized_Test'

file_list = os.listdir(file_root)

for file in file_list:
    file_path = f'{file_root}/{file}'
    output_path = f'{output_root}/{file}'

    cap = cv2.VideoCapture(file_path)

    frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    frame_size = (frame_width, frame_height)
    fps, frames = cap.get(cv2.CAP_PROP_FPS), cap.get(cv2.CAP_PROP_FRAME_COUNT)

    target_width = 700
    target_height = 644
    target_size = (target_width, target_height)

    fourcc = cv2.VideoWriter_fourcc('m', 'p', '4', 'v')
    out = cv2.VideoWriter(output_path, fourcc, fps, target_size)

    if not cap.isOpened():
        continue

    # Loop through each frame in the video
    while True:
        ret, frame = cap.read()
        
        if not ret:
            break

        H, W, C = frame.shape

        # crop with ratio
        wr = 0.0
        wl = 0.0
        hu = 0.2
        hd = 0.0

        wratio = 1 - wr - wl
        hratio = 1 - hu - hd

        cutout_frame = frame[int(H * hu):int(H * hu + H * hratio), int(W * wr):int(W * wr + W * wratio)]

        # padding
        new_width = int(W * wr + W * wratio) - int(W * wr)
        new_height = int(H * hu + H * hratio) - int(H * hu)

        average_axis0 = cutout_frame.mean(axis=0)
        average_axis1 = cutout_frame.mean(axis=1)
        edge_color = np.array([average_axis0[0], average_axis0[-1], average_axis1[0], average_axis1[-1]])
        average_color = edge_color.mean(axis=0)

        if target_width / target_height < new_width / new_height:
            padding = int((new_width * target_height / target_width - new_height) / 2)
            padding_added_frame = cv2.copyMakeBorder(cutout_frame, padding, padding, 0, 0, cv2.BORDER_CONSTANT, value=average_color)
        else:
            padding = int((new_height * target_width / target_height - new_width) / 2)
            padding_added_frame = cv2.copyMakeBorder(cutout_frame, 0, 0, padding, padding, cv2.BORDER_CONSTANT, value=average_color)

        # resize
        resized_frame = cv2.resize(padding_added_frame, (target_width, target_height), cv2.INTER_CUBIC)

        out.write(resized_frame)

    cap.release()
    out.release()

    cv2.destroyAllWindows()
