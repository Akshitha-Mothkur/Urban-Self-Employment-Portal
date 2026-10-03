# Urban Self-Employment Portal

A Java-based service booking application that connects customers with local service providers such as electricians, plumbers, tailors, and tutors.

The project demonstrates **Core Java, OOP concepts, JDBC, SQL, MySQL, and Java Swing** through a simple service-search and booking workflow.

## Features

* Search for local service providers by service type
* View provider details such as:

  * Name
  * Service
  * Location
  * Experience
  * Price
* Book a service provider
* Store booking details in MySQL
* Simple desktop interface using Java Swing
* Database connectivity using JDBC
* SQL JOIN queries for retrieving provider information

## Tech Stack

* **Java** – Application development
* **Java Swing** – Graphical User Interface
* **JDBC** – Database connectivity
* **MySQL** – Data storage
* **SQL** – Data retrieval and management

## Project Structure

```text
Urban-Self-Employment/
│
├── DBConnection.java
├── Provider.java
├── ProviderDAO.java
├── BookingDAO.java
├── Main.java
│
├── mysql-connector-j-9.3.0.jar
└── README.md
```

## Database

The project uses a MySQL database named:

```text
urban_self_employment
```

### Tables

* `users` – Stores user information
* `providers` – Stores service provider details
* `bookings` – Stores customer bookings

## Setup

### 1. Create the Database

Open MySQL and create the database and tables using the SQL script provided in the project.

### 2. Configure Database Connection

Update the following values in `DBConnection.java`:

```java
private static final String USER = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
```

Enter your local MySQL username and password.

### 3. Add MySQL Connector

Make sure the MySQL Connector/J `.jar` file is available in the project directory.

### 4. Compile the Project

Open the terminal in the project folder and run:

```bash
javac -cp ".;mysql-connector-j-9.3.0.jar" *.java
```

### 5. Run the Application

```bash
java -cp ".;mysql-connector-j-9.3.0.jar" Main
```

## Application Flow

```text
User
  ↓
Java Swing Interface
  ↓
Search Service
  ↓
ProviderDAO
  ↓
JDBC
  ↓
MySQL Database
  ↓
Provider Details
  ↓
Book Provider
  ↓
BookingDAO
  ↓
MySQL
```

## OOP Concepts Used

The project applies fundamental Object-Oriented Programming concepts including:

* **Encapsulation** – Provider data is represented using private fields and getter methods.
* **Classes and Objects** – Provider and DAO classes model application entities and operations.
* **Abstraction** – Database operations are separated into DAO classes.

## Future Enhancements

* User registration and login
* Provider registration
* Booking date and time selection
* Booking history
* Provider ratings and reviews
* Location-based service search
* Web-based version using Spring Boot

## Author

**Akshitha Mothkur**

B.Tech – Computer Science Engineering (Artificial Intelligence & Machine Learning)
