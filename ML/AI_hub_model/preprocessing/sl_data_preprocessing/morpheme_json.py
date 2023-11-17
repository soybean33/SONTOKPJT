import cv2
import os
import re
import json

regex = re.compile('(.*mp4$)')

for category in os.listdir('./video'):
    if not os.path.exists(f'./morpheme/{category}'):
        os.makedirs(f'./morpheme/{category}')
    
    for file in os.listdir(f'./video/{category}'):
        if regex.match(file):
            cap = cv2.VideoCapture(f'./video/{category}/{file}')

            fileName = file.split('.')[0]
            textFile = open(f'./video/{category}/{fileName}.txt', 'r', encoding="UTF-8")

            metaData = {}
            data = []

            fps = cap.get(cv2.CAP_PROP_FPS)
            frameCount = cap.get(cv2.CAP_PROP_FRAME_COUNT)
            duration = round(frameCount / fps, 3)

            metaData['url'] = ''
            metaData['name'] = file
            metaData['duration'] = duration
            metaData['exportedOn'] = "2023/10/31"

            start = 1.813
            end = 2.619
            name = textFile.readline()
            attributes = [{'name': name}]

            data.append({'start': start, 'end': end, 'attributes': attributes})

            textFile.close()

            with open(f'./morpheme/{category}/{fileName}_F_morpheme.json', "w", encoding="UTF-8") as json_file:
                json.dump({'metaData': metaData, 'data': data}, json_file, indent=4, ensure_ascii=False)
