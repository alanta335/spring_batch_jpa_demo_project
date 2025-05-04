# Spring Batch Demo

A **Spring Batch** application that processes user data from CSV files and stores it in a PostgreSQL database. This project demonstrates batch processing, fault tolerance, and database integration using Spring Batch and Spring Data JPA.

## Features

- **Batch Processing**: Read and write data between CSV files and a PostgreSQL database.
- **Fault Tolerance**: Skip and retry mechanisms for handling errors during batch processing.
- **Database Integration**: Use Spring Data JPA for seamless database operations.
- **Custom Mappers**: Map CSV fields to Java objects for processing.
- **Output Generation**: Export database records back to a CSV file.

## Prerequisites

- **Java 21** or higher
- **Maven** for dependency management
- **Docker** and **Docker Compose** for running PostgreSQL and Flyway
- **PostgreSQL** database

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/alanta335/spring_batch_jpa_demo_project.git
cd spring_batch_demo
```

### 2. Start the Database and Flyway

Use Docker Compose to start the PostgreSQL database and run Flyway migrations:

```bash
docker-compose up
```

### 3. Configure the Application

Update the database credentials in `src/main/resources/application.yaml` if necessary:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/mydb
    username: user
    password: password
```

### 4. Build and Run the Application

Build the project using Maven and run the application:

```bash
mvn clean install
java -jar target/spring_batch_demo-0.0.1-SNAPSHOT.jar
```

## How It Works

### Input CSV Files

- **User CSV (`csv/user.csv`)**:
  ```csv
  id,name,email
  1,John Doe,john.doe@example.com
  2,Jane Smith,jane.smith@example.com
  ```

- **Address CSV (`csv/address.csv`)**:
  ```csv
  id,address
  1,123 Main St
  2,456 Elm St
  ```

### Batch Jobs

1. **CSV to Database**:
   - Reads user and address data from the input CSV files.
   - Inserts the data into the `users` table in the PostgreSQL database.

2. **Database to CSV**:
   - Reads user data from the database.
   - Exports the data to an output CSV file (`output/users.csv`).

### Output CSV File

- **Output CSV (`output/users.csv`)**:
  ```csv
  id,name,email,address
  1,John Doe,john.doe@example.com,123 Main St
  2,Jane Smith,jane.smith@example.com,456 Elm St
  ```

## Project Structure

```
spring_batch_demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/spring_batch_demo/
│   │   │   ├── batch/          # Batch configuration, readers, writers, and mappers
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── repository/     # Spring Data JPA repositories
│   │   │   └── SpringBatchDemoApplication.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── csv/            # Input CSV files
│   │       └── output/         # Output CSV files
├── docker-compose.yml          # Docker Compose configuration
└── README.md                   # Project documentation
```

## Technologies Used

- **Spring Boot**: Application framework
- **Spring Batch**: Batch processing framework
- **PostgreSQL**: Relational database
- **Flyway**: Database migrations
- **Docker**: Containerization
- **Lombok**: Simplified Java code

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For questions or feedback, feel free to reach out:

- **Email**: alanta335@gmail.com
- **GitHub**: [alanta335](https://github.com/alanta335)
