# trading
Generates OHLC charts


# Getting Started

### Download Source code
1. Go to: https://github.com/FailedCommit/trading
2. Unzip at your preferred location

![img.png](img.png)

### Prepare the terminal

cd into the root of the project

refer: ![img_1.png](img_1.png)

### Start the project

./gradlew bootRun

![img_2.png](img_2.png)


### Hit the API

cURL:

curl -X POST \
http://localhost:8080/ \
-H 'Content-Type: application/json' \
-d '{
"event": "subscribe",
"symbol": "XXBTZUSD",
"interval": 5
}'

![img_3.png](img_3.png)


