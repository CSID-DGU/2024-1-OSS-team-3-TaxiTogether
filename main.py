from fastapi import FastAPI, HTTPException, Request
import requests
from typing import List, Tuple
import itertools
import math

app = FastAPI()

# 카카오 맵 API를 사용하여 두 지점 간의 거리를 계산하는 함수
def get_distance_from_kakao_map(start: Tuple[float, float], end: Tuple[float, float]) -> float:
    return 0.0

# 두 지점 간의 거리를 계산하는 함수
def calculate_distance(start: Tuple[float, float], end: Tuple[float, float]) -> float:
    return get_distance_from_kakao_map(start, end)

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

    return optimal_route, min_distance

# 경로 유효성 검사 및 최적 경로 탐색 함수
def validate_and_find_optimal_route(start: Tuple[float, float], destinations: List[Tuple[float, float]]) -> Tuple[bool, List[Tuple[float, float]], float]:
    optimal_route, total_distance = find_optimal_route(start, destinations)
    
    # 거리를 바탕으로 요금을 계산합니다.
    M = 4800  # 기본 요금
    x = 100000 / 131  # 1km당 요금
    a = calculate_distance(start, optimal_route[0])
    b = calculate_distance(start, optimal_route[-1])
    k = calculate_distance(optimal_route[0], optimal_route[1])
    l = calculate_distance(optimal_route[1], optimal_route[2])
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
def distribute_fees(total_fee: float, optimal_route: List[Tuple[float, float]], num_people: int) -> List[float]:
    if num_people == 2:
        return [total_fee / 2, total_fee / 2]
    elif num_people == 3:
        return [total_fee / 3, total_fee / 3, total_fee / 3]
    elif num_people == 4:
        return [total_fee / 4, total_fee / 4, total_fee / 4, total_fee / 4]
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
    
    fare_distribution = distribute_fees(total_fee, optimal_route, num_people)
    
    return {
        "success": True,
        "optimal_route": optimal_route,
        "total_fee": total_fee,
        "fare_distribution": fare_distribution
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
