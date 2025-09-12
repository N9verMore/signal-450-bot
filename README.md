# 📩 Bot-450 + Signal CLI REST API

This project is a Spring Boot service for automated message delivery to Signal groups using [signal-cli-rest-api](https://github.com/bbernhard/signal-cli-rest-api).
The application supports scheduled message sending.

## ⚙️ Tech Stack

- Java 21 (Eclipse Temurin JDK)

- Spring Boot 3

- Docker + Docker Compose

- signal-cli-rest-api for Signal integration

## 🔑 Configuration

Create a .env file in the project root:
```
SIGNAL_API_URL=http://signal-api:8080
SIGNAL_GROUP_NAME=MyGroupName
SIGNAL_SENDER_NUMBER=+380648391293,+380847361392
SIGNAL_MESSAGE=450
CRON_TIME="0 50 5,19 * * *"
TZ=Europe/Kiev
MODE=native
```
## Variables

- SIGNAL_API_URL — Signal API container address, don`t change. Have to be: http://signal-api:8080

- SIGNAL_GROUP_NAME — Target Signal group name

- SIGNAL_SENDER_NUMBER — Sender phone number (starts with +380, separated by ",")

- SIGNAL_MESSAGE — Default message text

- CRON_TIME — Cron expression for scheduling (e.g. every day at 05:50 and 19:00)

- TZ - Your TimeZone
- MODE - signal-api work mode. Don`t change or look for [doc](https://github.com/bbernhard/signal-cli-rest-api)

## 🚀 Run
Build and start the services:
```
docker-compose up --build -d
```
After startup:

- Spring Boot service → http://localhost:8080
  
- Signal REST API → http://localhost:8081

## 🔒 Authorization
To authorize your account you have to send **GET** method http://signal-api:8080/v1/qrcodelink?device_name=signal-api localy from machine and then scan QR code from browser through Signal app.
Functionality for authorisation and get qr remotely will be added soon.
