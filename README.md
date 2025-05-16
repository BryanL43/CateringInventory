# Airline Catering Inventory System

## Overview
This project is a mock backend RDBMs java application to simulate an airline catering inventory system.
In particular, it simulates a manager creating a new catering order for a flight.
It uses `Maven framework` and `JDBC` to interact with the `AWS RDS` database with built-in integration testing.

## Installation

### Local Setup
1. **Clone the repository:**
```sh
git clone <your-repo-url>
cd <your-repo-name>
```

2. **Set up environment variables:**
Create a `.env` file in the root directory and add your database's environment:
 ```ini
DB_HOST=your_rds_hostname
DB_PORT=your_port
DB_NAME=your_database_schema_name
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password
 ```
Note: Do not include any quotation marks on any of the environment variables.

3. Run the SQL in the following files to setup and populate the database:
```ini
src/main/resources/InitDatabase.sql # Instantiates the schema and tables
src/main/resources/PopulateDatabase.sql # Populate the tables with test cases and a sample
```


## Run the Program
1. Run the following file with main to create a sample catering order:
```ini
src/main/java/App.java
```

2. Run the following file for complete integration test:
```ini
src/test/MainTestSuite.java
```

3. Run the following file for only entity (dao) integration test:
```ini
src/test/DaoTestSuite.java
```

## Specs
Specs referenced in `pom.xml` under dependencies.

### MySQL Connector
```sh
version: 8.0.33
```
[MySQL Connector 8.0.33 Source](https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.33/)

### JUnit Jupiter
```sh
version: 5.10.0
```
[JUnit Jupiter 5.10.0 Source](https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api/5.10.0)

### JUnit Platform Suite
```sh
version: 1.9.1
```
[JUnit Platform Suite 1.9.1 Source](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-suite/1.9.1)


## Troubleshooting
- Ensure the `.env` file is configured correctly.
- If the project is configured with the `.iml` format file, simply reopen the project to refresh cache.
- Ensure `src/` directory is marked as `Sources Root`.
- Ensure `src/main/resources/` is marked as `Resources Root`.
- Ensure `src/test/` is marked as `Test Sources Root`.
- If there are any potential compilation issues with `test/`, simply clear cache via deleting `target/` and rebuilding.

## License
This project is licensed under the MIT License.