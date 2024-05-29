#af3a07081f830adca6b60768135b5e54

import requests
import itertools

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

def calculate_percentage(a, b, c, d, k, l, n, M, x):
    t1 = a / 4 + k / 3 + l / 2 + n
    t2 = a / 4 + k / 3 + l / 2
    t3 = a / 4 + k / 3
    
    try:
        percentage1 = round((1 - (t1 - b / 4) / (3 * b / 4 + 3 * M / (4 * x))) * 100)
    except ZeroDivisionError:
        percentage1 = 0
    try:
        percentage2 = round((1 - (t2 - c / 4) / (3 * c / 4 + 3 * M / (4 * x))) * 100)
    except ZeroDivisionError:
        percentage2 = 0
    try:
        percentage3 = round((1 - (t3 - d / 4) / (3 * d / 4 + 3 * M / (4 * x))) * 100)
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
        route_distance += distances[(perm[-1], 'start')]
        
        if route_distance < min_distance:
            min_distance = route_distance
            best_route = perm
    
    a = distances[('start', best_route[0])]
    b = distances[('start', best_route[-1])]
    c = distances[('start', best_route[1])]
    d = distances[('start', best_route[2])] if len(best_route) > 2 else 0
    k = distances[(best_route[0], best_route[1])]
    l = distances[(best_route[1], best_route[2])] if len(best_route) > 2 else 0
    n = distances[(best_route[2], best_route[3])] if len(best_route) > 3 else 0
    
    percentages = calculate_percentage(a, b, c, d, k, l, n, M, x)
    
    result = {}
    for i, name in enumerate(best_route):
        if i == 0:
            result[name] = percentages[0]
        elif i == 1:
            result[name] = percentages[1]
        elif i == 2:
            result[name] = percentages[2]
        else:
            result[name] = 0
    
    return result

# 테스트 케이스
start = [37.5665, 126.9780]  # 서울 시청 좌표 (위도, 경도)
points = {
    'first': [37.5775, 126.9768],  # 경복궁 좌표 (위도, 경도)
    'second': [37.5714, 126.9658],  # 청와대 좌표 (위도, 경도)
    'third': [37.5512, 126.9882],  # 남산타워 좌표 (위도, 경도)
    'fourth': [37.5796, 126.9770]  # 광화문 좌표 (위도, 경도)
}

api_key = "af3a07081f830adca6b60768135b5e54"

result = validate_availability(start, points, api_key)
print(result)
