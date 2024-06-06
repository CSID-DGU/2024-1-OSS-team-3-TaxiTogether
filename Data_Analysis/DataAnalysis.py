import requests
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
        station = random.choices(data, k=1)[0]  # random.choices로 무작위로 선택
        attempts += 1
        try:
            formatted_data.append({'station': station['name'], 'coordinate': (station['lat'], station['lng'])})
        except KeyError:
            continue
    return formatted_data


def pop_random_element(data):
    if data:
        element = random.choice(data)
        data.remove(element)
        return element
    else:
        return None
    

# 무작위 역 데이터 가져오기
random_station_data = get_random_station_data(data, num_samples=100)
matchedcount = 0
i = 1

# 무작위 좌표 생성 예시
while random_station_data:
    S = {'station': '강남', 'coordinate': (37.497175, 127.027926)}
    A = pop_random_element(random_station_data)
    B = pop_random_element(random_station_data)
    C = pop_random_element(random_station_data)
    D = pop_random_element(random_station_data)

    start = {"lat": S['coordinate'][0], "lon": S['coordinate'][1]}
    points = {
        'first': {"lat": A['coordinate'][0], "lon": A['coordinate'][1]},
        'second': {"lat": B['coordinate'][0], "lon": B['coordinate'][1]},
        'third': {"lat": C['coordinate'][0], "lon": C['coordinate'][1]},
        'fourth': {"lat": D['coordinate'][0], "lon": D['coordinate'][1]}

    }

    payload = {
    "start": start,
    "points": points
    }

    response = requests.post("http://beatmania.app:8181/validate_route", json=payload)

    if response.status_code == 200:
        print("Response received:")
        print(f"CASE #{i} : Start = {S['coordinate'][0]}, {S['coordinate'][1]}")
        print(response.json())
        matchedcount = matchedcount + 1
    else:
        print(f"Failed to get a valid response: {response.status_code}")
        print(response.json())
        print(f"실패 : {A['station']}, {B['station']}, {C['station']}, {D['station']} ")
    
    i = i + 1
