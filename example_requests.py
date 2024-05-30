import requests

start = {"lat": 35.0806128, "lon": 128.8987007}
points = {
    'first': {"lat": 35.08773181281280, "lon": 128.90700099747264},
    'second': {"lat": 35.11191835, "lon": 128.9217078},
    'third': {"lat": 35.11508664, "lon": 128.92223847},
    #'fourth': {"lat": 35.11811858, "lon": 128.91652848}
}

payload = {
    "start": start,
    "points": points
}

response = requests.post("http://127.0.0.1:8000/validate_route", json=payload)

if response.status_code == 200:
    print("Response received:")
    print(response.json())
else:
    print(f"Failed to get a valid response: {response.status_code}")
    print(response.json())
