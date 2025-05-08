# Airline Catering Inventory System

## Overview
This project is a mock backend RDBMs java application to simulate an airline catering inventory system.
It uses Maven framework and JDBC to interact with the `AWS RDS` database.

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


## Specs

### MySQL Connector
Reference in `pom.xml` under dependencies.
```sh
version: 8.0.33
```

[MySQL Connector 8.0.33 Source](https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.33/)


## Troubleshooting
- Ensure the `.env` file is configured correctly.


## License
This project is licensed under the MIT License.