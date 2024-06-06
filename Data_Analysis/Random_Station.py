import json
import random

# JSON 파일에서 데이터 읽기
with open('Data_Analysis/station_coordinate.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# 무작위 역 데이터를 가져오는 함수
def get_random_station_data(data, num_samples=5):
    formatted_data = []
    attempts = 0
    
    while len(formatted_data) < num_samples and attempts < len(data) * 2:
        station = random.choice(data)
        attempts += 1
        try:
            formatted_data.append({'station': station['name'], 'coordinate': (station['lat'], station['lng'])})
        except KeyError:
            continue
    return formatted_data

# 무작위 역 데이터 가져오기
random_station_data = get_random_station_data(data, num_samples=100)

# 무작위 역 데이터 출력
for entry in random_station_data:
    print(entry)
