import requests
import random
import json
import os

def generate_random_seoul_coordinates():
    # 서울시의 위도와 경도 범위
    lat_min, lat_max = 37.413294, 37.715133
    lon_min, lon_max = 126.734086, 127.269311
    
    # 무작위 위도와 경도 생성
    latitude = random.uniform(lat_min, lat_max)
    longitude = random.uniform(lon_min, lon_max)
    
    return latitude, longitude

# state.json 파일이 존재하면 상태 불러오기
if os.path.exists("Data_Analysis/state.json"):
    with open("Data_Analysis/state.json", "r") as f:
        state = json.load(f)
    matchedcount = state["matchedcount"]
    i = state["i"]
else:
    matchedcount = 0
    i = 1

# 무작위 좌표 생성 예시
while matchedcount < 250:
    S = generate_random_seoul_coordinates()
    A = generate_random_seoul_coordinates()
    B = generate_random_seoul_coordinates()
    C = generate_random_seoul_coordinates()
    D = generate_random_seoul_coordinates()

    start = {"lat": S[0], "lon": S[1]}
    points = {
        'first': {"lat": A[0], "lon": A[1]},
        'second': {"lat": B[0], "lon": B[1]},
        'third': {"lat": C[0], "lon": C[1]},
        'fourth': {"lat": D[0], "lon": D[1]}
    }

    payload = {
        "start": start,
        "points": points
    }

    response = requests.post("http://beatmania.app:8000/validate_route", json=payload)

    if response.status_code == 200:
        response_data = response.json()
        print("Response received:")
        print(f"CASE #{i} : Start = {S[0]}, {S[1]}")
        print(response_data)

        # 성공적인 응답을 추가할 파일 생성/열기
        with open("Data_Analysis/successful_responses.txt", "a") as response_file:
            # 성공적인 응답을 파일에 쓰기
            response_file.write(f"CASE #{i} : Start = {S[0]}, {S[1]}\n")
            response_file.write(json.dumps(response_data) + "\n")
        
        matchedcount += 1
    else:
        print(f"Failed to get a valid response: {response.status_code}")
        print(response.json())

    # 상태 업데이트
    state = {"matchedcount": matchedcount, "i": i}
    with open("Data_Analysis/state.json", "w") as f:
        json.dump(state, f)
    
    i += 1
