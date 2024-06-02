from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Dict, Tuple
import requests
import itertools

app = FastAPI()

class Coordinates(BaseModel):
    lat: float
    lon: float

class RequestModel(BaseModel):
    start: Coordinates
    points: Dict[str, Coordinates]

fare_cache = {} # API 콜 횟수를 줄이기 위한 딕셔너리

total_fare = 0

def get_distance_from_kakao_api(api_key, coord1, coord2):
    url = "https://apis-navi.kakaomobility.com/v1/directions"
    headers = {
        "Authorization": f"KakaoAK {api_key}"
    }
    params = {
        "origin": f"{coord1[1]},{coord1[0]}",
        "destination": f"{coord2[1]},{coord2[0]}"
    }
    response = requests.get(url, headers=headers, params=params)
    if response.status_code == 200:
        result = response.json()
        return result['routes'][0]['summary']['distance']
    else:
        raise Exception("Error fetching data from Kakao API")

def get_fare_from_kakao_api(api_key, coord1, coord2, waypoints=[]):

    route_key = (tuple(coord1), tuple(coord2), tuple(tuple(wp) for wp in waypoints))

    if route_key in fare_cache:
        return fare_cache[route_key]

    points = "|".join([f"{wp[1]},{wp[0]}" for wp in waypoints])

    url = "https://apis-navi.kakaomobility.com/v1/directions"
    headers = {
        "Authorization": f"KakaoAK {api_key}"
    }
    params = {
        "origin": f"{coord1[1]},{coord1[0]}",
        "destination": f"{coord2[1]},{coord2[0]}",
    }    
    if waypoints:
        params["waypoints"] = points
    response = requests.get(url, headers=headers, params=params)
    if response.status_code == 200:
        result = response.json()
        fare_info = result['routes'][0]['summary']['fare']
        total_fare = fare_info['taxi'] + fare_info['toll']

        fare_cache[route_key] = total_fare

        return total_fare
    
    else:
        raise Exception("Error fetching data from Kakao API")

def calculate_percentage(a, b, c, d, k, l, n, M, x, num_of_person):
    if num_of_person == 4:
        t1 = a / 4 + k / 3 + l / 2 + n
        t2 = a / 4 + k / 3 + l / 2
        t3 = a / 4 + k / 3
    elif num_of_person == 3:
        t1 = 0
        t2 = a / 3 + k / 2 + l
        t3 = a / 3 + k / 2
    elif num_of_person == 2:
        t1 = 0
        t2 = 0
        t3 = a / 2 + k
    
    try:
        percentage1 = round((1 - (t1 - b / 4) / (3 * b / 4 + 3 * M / (4 * x))) * 100) # 네 번째 사람
    except ZeroDivisionError:
        percentage1 = 0
    try:
        percentage2 = round((1 - (t2 - d / 4) / (3 * d / 4 + 3 * M / (4 * x))) * 100) # 세 번째 사람
        if num_of_person == 3:
            percentage2 = round((1 - (t2 - d / 3) / (2 * d / 3 + 2 * M / (3 * x))) * 100) # 세 번째 사람 세 명만 있을 경우
    except ZeroDivisionError:
        percentage2 = 0
    try:
        percentage3 = round((1 - (t3 - c / 4) / (3 * c / 4 + 3 * M / (4 * x))) * 100) # 두 번째 사람
        if num_of_person ==3:
            percentage3 = round((1 - (t3 - c / 3) / (2 * c / 3 + 2 * M / (3 * x))) * 100) # 두 번째 사람
        elif num_of_person ==2:
            percentage3 = round((1 - (t3 - c / 2) / (c / 2 + M / (2 * x))) * 100) # 두 번째 사람
    except ZeroDivisionError:
        percentage3 = 0

    return percentage1, percentage2, percentage3

def calculate_distances(coords, api_key):
    distances = {}
    for i, j in itertools.combinations(coords, 2):
        dist = get_distance_from_kakao_api(api_key, coords[i], coords[j])
        distances[(i, j)] = dist
        distances[(j, i)] = dist
    return distances

def validate_availability(start, points, api_key):
    if len(points) < 2:
        raise ValueError("At least two destinations are required")
    
    M = 4800
    x = 100000 / 131
    
    points_with_start = {'start': start, **points}
    distances = calculate_distances(points_with_start, api_key)
    
    names = list(points.keys())
    
    best_route = None
    min_distance = float('inf')
    for perm in itertools.permutations(names):
        route_distance = distances[('start', perm[0])]
        for i in range(len(perm) - 1):
            route_distance += distances[(perm[i], perm[i+1])]
        #route_distance += distances[(perm[-1], 'start')]
        
        if route_distance < min_distance:
            min_distance = route_distance
            best_route = perm
    
    a = distances[('start', best_route[0])]
    b = distances[('start', best_route[3])] if len(best_route) > 3 else 0
    c = distances[('start', best_route[1])]
    d = distances[('start', best_route[2])] if len(best_route) > 2 else 0
    k = distances[(best_route[0], best_route[1])]
    l = distances[(best_route[1], best_route[2])] if len(best_route) > 2 else 0
    n = distances[(best_route[2], best_route[3])] if len(best_route) > 3 else 0
    
    percentages = calculate_percentage(a, b, c, d, k, l, n, M, x, len(best_route))
    
    result = {}
    for i, name in enumerate(best_route):
        if i == 3:
            result[name] = percentages[0]
        elif i == 2:
            result[name] = percentages[1]
        elif i == 1:
            result[name] = percentages[2]
        else:
            result[name] = 0

    return best_route, result

def calculate_each_fare(best_route, start, coords, api_key):
    num_points = len(best_route)
    global total_fare
    if num_points == 2:
        r1 = get_fare_from_kakao_api(api_key, start, coords[best_route[0]])
        p1 = r1 / 3
        r2 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]]) - r1
        p2 = r2 / 2 + p1
        p3 = p2

        q1 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]])
        q2 = get_fare_from_kakao_api(api_key, start, coords[best_route[0]])
        q3 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]])

        total_fare = q3

        b1 = q3 / q1
        b2 = q3 / q2

        total_b = b1 + b2

        fare1 = p1 + p3 * b1 / total_b
        fare2 = p2 + p3 * b2 / total_b

        return {
            best_route[0]: fare1,
            best_route[1]: fare2
        }

    elif num_points == 3:
        r1 = get_fare_from_kakao_api(api_key, start, coords[best_route[0]])
        p1 = r1 / 4
        r2 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]]) - r1
        p2 = r2 / 3 + p1
        r3 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]], coords[best_route[1]]]) - \
            get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]])
        p3 = r3 / 2 + p2
        p4 = p3

        q1 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[1]]])
        
        q2 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]]])
        
        q3 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]])

        q4 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]], coords[best_route[1]]])

        total_fare = q4

        b1 = q4 / q1
        b2 = q4 / q2
        b3 = q4 / q3

        total_b = b1 + b2 + b3

        fare1 = p1 + p4 * b1 / total_b
        fare2 = p2 + p4 * b2 / total_b
        fare3 = p3 + p4 * b3 / total_b

        return {
            best_route[0]: fare1,
            best_route[1]: fare2,
            best_route[2]: fare3
        }

    elif num_points == 4:
        r1 = get_fare_from_kakao_api(api_key, start, coords[best_route[0]])
        p1 = r1/5
        r2 = get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]]) - r1
        p2 = r2/4 + p1
        r3 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]], coords[best_route[1]]]) - \
            get_fare_from_kakao_api(api_key, start, coords[best_route[1]], [coords[best_route[0]]])
        p3 = r3/3 + p2
        r4 = get_fare_from_kakao_api(api_key, start, coords[best_route[3]], [coords[best_route[0]], coords[best_route[1]], coords[best_route[2]]]) - \
            get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]], coords[best_route[1]]])
        p4 = r4/2 + p3
        p5 = p4

        q1 = get_fare_from_kakao_api(api_key, start, coords[best_route[3]], [coords[best_route[1]], coords[best_route[2]]])
        
        q2 = get_fare_from_kakao_api(api_key, start, coords[best_route[3]], [coords[best_route[0]], coords[best_route[2]]])
        
        q3 = get_fare_from_kakao_api(api_key, start, coords[best_route[3]], [coords[best_route[0]], coords[best_route[1]]])
        
        q4 = get_fare_from_kakao_api(api_key, start, coords[best_route[2]], [coords[best_route[0]], coords[best_route[1]]])

        q5 = get_fare_from_kakao_api(api_key, start, coords[best_route[3]], [coords[best_route[0]], coords[best_route[1]], coords[best_route[2]]])

        total_fare = q5

        b1 = q5 / q1
        b2 = q5 / q2
        b3 = q5 / q3
        b4 = q5 / q4

        total_b = b1 + b2 + b3 + b4

        fare1 = p1 + p5 * b1 / total_b
        fare2 = p2 + p5 * b2 / total_b
        fare3 = p3 + p5 * b3 / total_b
        fare4 = p4 + p5 * b4 / total_b

        return {
            best_route[0]: round(fare1),
            best_route[1]: fare2,
            best_route[2]: fare3,
            best_route[3]: fare4
        }

@app.post("/validate_route")
def validate_route(request: RequestModel):
    start = [request.start.lat, request.start.lon]
    points = {key: [point.lat, point.lon] for key, point in request.points.items()}
    api_key = "af3a07081f830adca6b60768135b5e54"

    try:
        result = validate_availability(start, points, api_key)

        is_available = True
        num_of_value = len(result[1])
        if num_of_value==4:
            for value in result[1].values():
                if value != 0 and value < 40:
                    is_available = False
                    break
        elif num_of_value==3:
            for value in result[1].values():
                if value != 0 and value < 20:
                    is_available = False
                    break
        elif num_of_value==2: # 4명일 때는 문제 없으나 두 명일 때는 크게 꺾이는 경우 퍼센티지가 매우 낮게 나옴
            for value in result[1].values():
                if value != 0 and value < 10:
                    is_available = False
                    break

        if not is_available:
            raise HTTPException(status_code=400, detail="Invalid route: one or more points have less than 60% availability")

        fares = calculate_each_fare(result[0], start, points, api_key)
        return {"best_route": result[0], "fares": fares, "total_fare": total_fare, "percentage": result[1], "points": points}

    except ValueError as ve:
        raise HTTPException(status_code=400, detail=str(ve))

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# 코드를 실행하려면 다음 명령어를 실행해주세요: uvicorn main:app --reload
