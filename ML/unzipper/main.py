import os
import zipfile
import sys

input = sys.stdin.readline

path_dir = input("경로 입력: (예시- ./data/) ").strip()
file_list = os.listdir(path_dir)

for zip_file in file_list:
    if not zip_file.endswith('.zip'):
        continue
    zipfile.ZipFile(path_dir + zip_file, 'r').extractall(path_dir + zip_file[:-4])