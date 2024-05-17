import requests

url = "http://localhost:8000/calculate_fare"
data = {
    "start": [35.0806128, 128.8987007],
    "destinations": [
        [35.09580837, 128.92444805],
        [35.11191835,128.9217078],
        [35.11508664, 128.92223847],
        [35.11811858, 128.91652848]
    ],
    "num_people": 4
}
response = requests.post(url, json=data)
print(response.json())