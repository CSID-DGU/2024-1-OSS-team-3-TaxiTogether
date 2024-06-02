import requests

start = {"lat": 37.5615, "lon": 126.9956} # 충무로
points = {
    'first': {"lat": 37.5723, "lon": 126.96}, # 독립문
    'second': {"lat": 37.6112, "lon": 126.917}, # 구산역
    #'third': {"lat": 37.5559, "lon": 127.0293}, # 서울 성동구 행당동 346
    'third': {"lat": 37.6376, "lon": 126.9167}, # 구파발역
    #'fourth': {"lat": 37.6536, "lon": 126.895} # 삼송역
    #'fourth': {"lat": 37.6833, "lon": 126.9224} # 경기 양주시 장흥면 삼하리 43-2
}

payload = {
    "start": start,
    "points": points
}
response = requests.post("http://127.0.0.1:8000/validate_route", json=payload)
# response = requests.post("http://beatmania.app:8000/validate_route", json=payload)

if response.status_code == 200:
    print("Response received:")
    print(response.json())
else:
    print(f"Failed to get a valid response: {response.status_code}")
    print(response.json())
