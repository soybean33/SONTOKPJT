import os
import zipfile

path_dir = './data/'
file_list = os.listdir(path_dir)

for zip_file in file_list:
    if not zip_file.endswith('.zip'):
        continue
    zipfile.ZipFile(path_dir + zip_file, 'r').extractall(path_dir + zip_file[:-4])