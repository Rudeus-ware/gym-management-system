# 🏋️ Gym Management System

A Java-based gym management application built to demonstrate object-oriented analysis, design, and programming concepts. The project manages members, memberships, classes, sessions, bookings, attendance, trainers, and basic reporting.

## 📋 Project Overview

This system is designed to support the core operations of a gym business, including:
- Member profile management
- Membership plan handling for Basic, Premium, and Family users
- Class management for Yoga, Spin, and Strength sessions
- Session scheduling and booking workflow
- Attendance tracking
- Trainer assignment and management
- Reporting and statistics

## 🛠️ Technologies Used

- Java 17
- Maven
- Gson for JSON persistence
- JavaFX (UI structure included, though the current runnable entry point is the console application)

## 🧱 Project Structure

```text
src/main/java/
├── com/gym/
│   ├── Main.java                     # Console application entry point
│   ├── controller/                  # Business logic controllers
│   ├── model/                       # Domain classes and enums
│   │   ├── attendance/
│   │   ├── booking/
│   │   ├── classes/
│   │   ├── membership/
│   │   ├── payment/
│   │   ├── user/
│   │   └── Profile.java
│   ├── persistence/                 # Data loading and saving logic
│   └── view/                        # JavaFX UI controllers and FXML resources
src/main/resources/
├── css/
├── fxml/
├── database/
└── i18n/
data/
└── JSON files for persistence
```

## 🚀 How to Run

### Prerequisites
- Java 17 or higher
- Maven installed and available in PATH

### Build the project
```bash
mvn -q -DskipTests compile
```

### Run the console application
```bash
printf '0\n' | mvn -q -DskipTests exec:java -Dexec.mainClass=com.gym.Main
```

### Expected behavior
When the application starts, it:
1. Loads saved data from the JSON files in the data folder.
2. Displays the main menu.
3. Allows the user to view profiles, classes, bookings, sessions, create records, mark attendance, and save data.

## 🧠 Core Features

### 1. Profile Management
Users can create and view member profiles.

### 2. Membership Management
Supports different membership types:
- Basic
- Premium
- Family

### 3. Class Management
The system supports different class types:
- Yoga
- Spin
- Strength

### 4. Booking System
Members can be booked into sessions based on availability.

### 5. Attendance Tracking
Attendance can be recorded for a selected session and profile.

### 6. Persistence
All main data is stored in JSON files under the data folder so the application can reload previous records.

## 🧩 Main Classes

- Main: Console-based application entry point
- Profile: Represents a gym member profile
- Membership: Abstract base class for membership types
- Basic, Premium, Family: Concrete membership implementations
- GymClass: Abstract base class for class types
- Yoga, Spin, Strength: Concrete class implementations
- BookingController: Handles booking-related operations
- DataManager: Central access point for application data
- FileManager: Reads and writes JSON files

## 📂 Data Files

The application stores its sample and user data in the data directory:
- profiles.json
- memberships.json
- classes.json
- sessions.json
- bookings.json
- attendance.json
- trainers.json

## 🧪 Verification

The project was verified with the following build command:
```bash
mvn -q -DskipTests compile
```

This completed successfully.

## 📝 Notes

- The current runnable version is the console application.
- A JavaFX UI layer is also present in the project structure, but the console entry point is the one currently verified and working.
- The system is intended for academic learning and demonstration purposes.

## ✅ Summary

This project demonstrates a practical implementation of object-oriented principles such as inheritance, abstraction, composition, encapsulation, and data persistence in a real-world gym management scenario.

