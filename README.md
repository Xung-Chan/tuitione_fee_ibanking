# Tuition Fee App

## Overview
**Tuition Fee** is an application that assists students and related parties in **looking up and paying tuition fees** conveniently, transparently, and securely. The application is designed to address common problems in the free tuition payment process, such as: fast and continuous payment methods, difficulty tracking payment status, and limitations in making payments on behalf of other student users.

The system focuses on user experience, ensuring the accuracy of tuition fee data and clear access to payment history.

## Feature
### System Login
- Authenticate users through personal accounts.

- Ensure the safety and security of student information.

- Serves as a prerequisite for accessing the system's operational functions.

### Tuition Fee Lookup by Student ID
- Allows searching for tuition fee information based on **student ID**.

- Displays detailed tuition fees for each semester (if applicable).

- Serves both the student's own lookup needs and those paying on their behalf.

###  Tuition Fee Payment
- Pay tuition fees for:

- **The logged-in student**

- **Other students** (e.g., parents, guardians, friends)
- Supports online payment processes, minimizing manual operations.

- Updates payment status immediately after the transaction is completed.

###  View payment history
- Stores all completed transactions.

- Displays information:

- Student ID

- Payment amount

- Payment time

- Transaction status
- Supports reconciliation and verification of paid amounts.

## How to Start
### Backend Server
#### 1. Config
  Go to each service in `backend` folder and update your `.env` 
#### 2. Run Backend Server 
Open `CMD` in `backend` folder. There are 2 options:

- **_Dev Environment_**

  ```cmd 
  docker-compose -f docker-compose.dev.yml up --build 
  ```

- **_Production Environment_**

  ```cmd 
  docker-compose up --build
  ```
Hosting backend server is required.

_For example with_ `ngrok`:
  ```cmd 
  ngrok http localhost:<your gateway port>
  ```

### Install Apk
  Prerequisite:
  - Connected Android Device / Emulator 

  Open `secret.gradle` in root project
  Modify value of `GATEWAY_URL` into your hosting backend server url

  Open `CMD` in root project:
  ```cmd 
  ./gradlew installDebug
  ```
