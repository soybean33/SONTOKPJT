import requests
import urllib3
import os
import time
import re
import sys
from bs4 import BeautifulSoup

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

print('download start ' + time.strftime('%Y.%m.%d - %H:%M:%S'))

# category list: CTE (일상생활), 세부분류 16개 (ex. CTE001)
#                SPE (전문용어), 세부분류 10개 (ex. SPE002)
#                MUE (문화정보), 세부분류 18개 (ex. MUE003)
# use only one of category
sign_top_category = 'SPE'
# resolution type: low, high
video_resolution = 'high'
# start page: default = 1
page_start = 1
# start category: default = 1
start_category = 6
# end category: checkout category list
end_category = 10
# label text encoding
label_encoding = 'UTF-8'
# max connection retry
max_loop = 10
for category_index in range(start_category,end_category + 1):
    
    sign_category = sign_top_category + str(category_index).zfill(3)
    # path where videos downloaded
    download_path = '../sl_video_downloader_data/' + sign_category + '_' + video_resolution + '/'

    if not os.path.exists(download_path):
        os.makedirs(download_path)

    page_index = 1
    params = {'current_pos_index': '', 'origin_no': '0', 'searchWay': '', 'top_category': sign_top_category, 'category': sign_category, 'detailCategory': '', 'searchKeyword': '', 'pageIndex': str(page_index), 'pageJumpIndex': ''}
    headers = {'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36'}
    download_failure = True
    looped = 0
    while download_failure:
        try:
            if max_loop < looped:
                sys.exit(0)
            looped += 1
            download_failure = False
            response = requests.get("https://sldict.korean.go.kr/front/sign/signList.do", params, headers=headers, verify=False)
        except:
            print("Total item list connection failure. Retry download...")
            download_failure = True
    soup = BeautifulSoup(response.text, 'html.parser')

    total_words = int(soup.find("span", "t_orange").text.replace(',', ''))
    print("Total words: ", total_words)
    total_pages = int((total_words + 9) / 10)
    print("Total pages: ", total_pages)

    # access list page
    for page_index in range(page_start, total_pages + 1):
        print("Working on page: ", page_index)
        params = {'current_pos_index': '', 'origin_no': '0', 'searchWay': '', 'top_category': sign_top_category, 'category': sign_category, 'detailCategory': '', 'searchKeyword': '', 'pageIndex': str(page_index), 'pageJumpIndex': ''}
        download_failure = True
        looped = 0
        while download_failure:
            try:
                if max_loop < looped:
                    sys.exit(0)
                looped += 1
                download_failure = False
                get_response = requests.get("https://sldict.korean.go.kr/front/sign/signList.do", params, headers=headers, verify=False)
            except:
                print("Item list connection failure. Retry download...")
                download_failure = True
        soup = BeautifulSoup(get_response.text, 'html.parser')
        soup_main_contents = soup.find("div", "wrap_list")
        soup_items = soup_main_contents.find_all("a", "hand_thumb")

        # access item page
        for soup_item in soup_items:
            values = soup_item['href'].split("'")
            origin_no = values[1]
            current_pos_index = values[3]
            print("Origin No: ", origin_no, "\tCurrent Pos Index: ", current_pos_index)
            post_data = {'origin_no': origin_no, 'current_pos_index': current_pos_index}
            download_failure = True
            looped = 0
            while download_failure:
                try:
                    if max_loop < looped:
                        sys.exit(0)
                    looped += 1
                    download_failure = False
                    post_response = requests.post("https://sldict.korean.go.kr/front/sign/signContentsView.do", post_data, headers=headers, verify=False)
                except:
                    print("Item page connection failure. Retry download...")
                    download_failure = True
            soup_item_page = BeautifulSoup(post_response.text, 'html.parser')
            soup_form = soup_item_page.find("form", {'id': 'signViewForm'})
            soup_inputs = soup_form.find_all("input", {'id': True})
            # get video path
            video_post_data = {input_entity.attrs['id'] : input_entity.attrs['value'] for input_entity in soup_inputs}
            video_post_data['size'] = video_resolution # enable for high resolution
            download_failure = True
            looped = 0
            while download_failure:
                try:
                    if max_loop < looped:
                        sys.exit(0)
                    looped += 1
                    download_failure = False
                    video_post_response = requests.post("https://sldict.korean.go.kr/front/sign/include/controlVideoSpeed.do", video_post_data, headers=headers, verify=False)
                except:
                    print("Video path request connection failure. Retry download...")
                    download_failure = True
            soup_video = BeautifulSoup(video_post_response.text, 'html.parser')
            video_source = soup_video.find("source", {'type': 'video/mp4'}).attrs['src']
            print(video_source)
            # get meaning
            soup_labels = soup_item_page.find_all("dl", {'class': 'content_view_dis'})
            label_text = soup_labels[1].find("dd").text.lstrip().rstrip()
            label_text = re.sub(r'\s+', ' ', label_text)
            print(label_text)
            # save video
            download_failure = True
            looped = 0
            while download_failure:
                try:
                    if max_loop < looped:
                        sys.exit(0)
                    looped += 1
                    download_failure = False
                    video_file = requests.get(video_source, headers=headers, verify=False)
                except:
                    print("Video download connection failure. Retry download...")
                    download_failure = True
            video_name = video_source[video_source.rfind('/') + 1:]
            f = open(download_path + video_name, 'wb')
            f.write(video_file.content)
            f.close()
            # save meaning
            label_name = video_name[:-3] + 'txt'
            f = open(download_path + label_name, 'w', encoding=label_encoding)
            f.write(label_text)
            f.close()