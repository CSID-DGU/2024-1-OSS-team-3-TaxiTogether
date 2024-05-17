import requests

url = "http://localhost:8000/calculate_fare"
data = {
    "start": [37.5838699, 127.0565831],
    "destinations": [
        [37.581618, 127.059368],
        [37.579618, 127.057368],
        [37.577618, 127.055368],
        [37.575618, 127.053368]
    ],
    "num_people": 4
}
response = requests.post(url, json=data)
print(response.json())