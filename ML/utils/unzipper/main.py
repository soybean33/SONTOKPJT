import os
import zipfile
import sys

input = sys.stdin.readline


def unzipping(path_dir, save_dir):
    file_list = os.listdir(path_dir)

    for zip_file in file_list:
        if not zip_file.endswith('.zip'):
            continue
        zipfile.ZipFile(path_dir + zip_file, 'r').extractall(save_dir + zip_file[:-4])

if __name__ == "__main__":
    path_dir = input("압축 데이터 경로 입력 (예시- ./ai-data/) : ").strip()
    save_dir = input("저장 경로 입력 (예시- ./ai-data/) : ").strip()

    unzipping(path_dir, save_dir)