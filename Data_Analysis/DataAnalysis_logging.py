import requests
import random
import json
import os

# JSON 파일에서 데이터 읽기
with open('Data_Analysis/seoul_coordinate.json', 'r', encoding='utf-8') as file:
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

# state.json 파일이 존재하면 상태 불러오기
if os.path.exists("Data_Analysis/state.json"):
    with open("Data_Analysis/state.json", "r") as f:
        state = json.load(f)
    matchedcount = state["matchedcount"]
    i = state["i"]
else:
    matchedcount = 0
    i = 1

# random_station_data.json 파일이 존재하면 불러오기
if os.path.exists("Data_Analysis/random_station_data.json"):
    with open("Data_Analysis/random_station_data.json", "r", encoding='utf-8') as f:
        random_station_data = json.load(f)
else:
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
        response_data = response.json()
        print("Response received:")
        print(f"CASE #{i} : Start = {S['coordinate'][0]}, {S['coordinate'][1]}")
        print(response_data)

        # 성공적인 응답을 추가할 파일 생성/열기
        with open("Data_Analysis/successful_responses.txt", "a") as response_file:
            # 성공적인 응답을 파일에 쓰기
            response_file.write(f"CASE #{i} : Start = {S['coordinate'][0]}, {S['coordinate'][1]}\n")
            response_file.write(json.dumps(response_data) + "\n")
        
        matchedcount += 1

        # 성공한 데이터는 successed_data.json에 저장
        with open("Data_Analysis/successed_data.json", "a", encoding='utf-8') as success_file:
            success_file.write(json.dumps({'A': A['station'], 'B': B['station'], 'C': C['station'], 'D': D['station']}) + "\n")

    else:
        print(f"Failed to get a valid response: {response.status_code}")
        print(response.json())
        print(f"실패 : {A['station']}, {B['station']}, {C['station']}, {D['station']} ")
        random_station_data.append(A)
        random_station_data.append(B)
        random_station_data.append(C)
        random_station_data.append(D)

    # 상태 업데이트
    state = {"matchedcount": matchedcount, "i": i}
    with open("Data_Analysis/state.json", "w") as f:
        json.dump(state, f)

    # 현재 random_station_data 상태 저장
    with open("Data_Analysis/random_station_data.json", "w", encoding='utf-8') as f:
        json.dump(random_station_data, f)
    
    i += 1
