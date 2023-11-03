import cv2
import numpy as np

# Prepare video
fileName = "20231103_103532"
filePath = f'videos/{fileName}.mp4'
cap = cv2.VideoCapture(filePath)

frameWidth = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
frameHeight = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))

frameSize = (frameWidth, frameHeight)

print(f'frameSize = {frameSize}')

cnt = 0

target_width = 700
target_height = 644

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

    # save frame to image
    cv2.imwrite(f'./images/{fileName}{str(cnt).zfill(3)}_output.jpg', resized_frame)

    cnt += 1

if cap.isOpened():
    cap.release()

cv2.destroyAllWindows()