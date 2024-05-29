#af3a07081f830adca6b60768135b5e54

from fastapi import FastAPI, HTTPException, Request
import requests
from typing import List, Tuple
import itertools
import math

app = FastAPI()

temp_distance = []  # 거리 계산 데이터를 저장하는 리스트

# 카카오 맵 API를 사용하여 두 지점 간의 거리를 계산하는 함수
def get_distance_from_kakao_map(start: Tuple[float, float], end: Tuple[float, float]) -> float:
    for data in temp_distance:
        if (start, end) == (data[0], data[1]) or (end, start) == (data[0], data[1]):
            return data[2]
    url = "https://apis-navi.kakaomobility.com/v1/directions"
    headers = {
        "Authorization": "KakaoAK af3a07081f830adca6b60768135b5e54",
        "Content-Type": "application/json"
    }
    params = {
        "origin": f"{start[1]},{start[0]}",  # 출발지 (위도,경도)
        "destination": f"{end[1]},{end[0]}",  # 도착지 (위도,경도)
        "priority": "RECOMMEND"  # 추천 경로 탐색
    }
    response = requests.get(url, headers=headers, params=params)
    try:
        data = response.json()
        print(data)
        distance = data["routes"][0]["summary"]["distance"] / 1000  # 미터 단위를 km로 변환
        temp_distance.append([start, end, distance])
        return distance
    except Exception as e:
        try:
            if data["routes"][0]['result_code'] != 0:
                return [400, data["routes"][0]["result_msg"]]
        except:
            try:
                if data["code"] != 0:
                    return [400, data["msg"]]
            except:
                return [500, f"Internal Server Error: {e}"]

# 두 지점 간의 거리를 계산하는 함수
def calculate_distance(start: Tuple[float, float], end: Tuple[float, float]) -> float:
    result = get_distance_from_kakao_map(start, end)
    if type(result) != list:
        return result
    else:
        raise HTTPException(status_code=400, detail=result[1])

# 모든 가능한 경로와 그에 따른 거리를 계산하여 최적의 경로를 찾는 함수
def find_optimal_route(start: Tuple[float, float], destinations: List[Tuple[float, float]]) -> Tuple[List[Tuple[float, float]], float]:
    possible_routes = list(itertools.permutations(destinations))
    min_distance = float('inf')
    optimal_route = []

    for route in possible_routes:
        total_distance = calculate_distance(start, route[0])
        for i in range(len(route) - 1):
            total_distance += calculate_distance(route[i], route[i + 1])
        if total_distance < min_distance:
            min_distance = total_distance
            optimal_route = route

    temp_distance.clear()  # 거리 계산 데이터를 초기화합니다.
    return optimal_route, min_distance

# 경로 유효성 검사 및 최적 경로 탐색 함수
def validate_and_find_optimal_route(start: Tuple[float, float], destinations: List[Tuple[float, float]]) -> Tuple[bool, List[Tuple[float, float]], float]:
    optimal_route, total_distance = find_optimal_route(start, destinations)
    
    # 거리를 바탕으로 요금을 계산합니다.
    M = 4800  # 기본 요금
    x = 800  # 1km당 요금 (예시로 1000원 설정)
    a = calculate_distance(start, optimal_route[0])
    b = calculate_distance(start, optimal_route[-1])
    k = calculate_distance(optimal_route[0], optimal_route[1])
    l = calculate_distance(optimal_route[1], optimal_route[2]) if len(optimal_route) > 2 else 0
    n = calculate_distance(optimal_route[2], optimal_route[3]) if len(optimal_route) > 3 else 0

    t1 = a / 4 + k / 3 + l / 2 + n
    t2 = a / 4 + k / 3 + l / 2
    t3 = a / 4 + k / 3

    percent1 = (1 - (t1 - b / 4) / (3 * b / 4 + 3 * M / (4 * x))) * 100
    percent2 = (1 - (t2 - b / 4) / (3 * b / 4 + 3 * M / (4 * x))) * 100
    percent3 = (1 - (t3 - b / 4) / (3 * b / 4 + 3 * M / (4 * x))) * 100

    if percent1 > 60 and percent2 > 60 and percent3 > 60:
        total_fee = M + total_distance * x
        return True, optimal_route, total_fee
    else:
        return False, [], 0.0

# 요금 분배 알고리즘
def distribute_fees(start: Tuple[float, float], total_fee: float, optimal_route: List[Tuple[float, float]], num_people: int) -> List[float]:
    a = calculate_distance(start, optimal_route[0])
    k = calculate_distance(optimal_route[0], optimal_route[1])
    l = calculate_distance(optimal_route[1], optimal_route[2]) if num_people > 2 else 0
    n = calculate_distance(optimal_route[2], optimal_route[3]) if num_people > 3 else 0

    h = total_fee
    h1 = (a * h) / (a + k + l + n)
    h2 = (k * h) / (a + k + l + n)
    h3 = (l * h) / (a + k + l + n) if num_people > 2 else 0
    h4 = (n * h) / (a + k + l + n) if num_people > 3 else 0

    if num_people == 2:
        return [h1, h2]
    elif num_people == 3:
        return [h1, h2, h3]
    elif num_people == 4:
        return [h1, h2, h3, h4]
    else:
        raise ValueError("Invalid number of people")

@app.post("/calculate_fare")
async def calculate_fare(request: Request):
    data = await request.json()
    start = tuple(data["start"])
    destinations = [tuple(dest) for dest in data["destinations"]]
    num_people = data["num_people"]

    if num_people < 2 or num_people > 4:
        raise HTTPException(status_code=400, detail="사람 수는 2명~4명이어야 합니다.")

    is_valid, optimal_route, total_fee = validate_and_find_optimal_route(start, destinations)
    
    if not is_valid:
        return {"success": False}
    
    fare_distribution = distribute_fees(start, total_fee, optimal_route, num_people)
    
    return {
        "success": True,
        "optimal_route": optimal_route,
        "total_fee": total_fee,
        "fare_distribution": fare_distribution
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
