CREATE DATABASE catering_logistics;
USE catering_logistics;

CREATE TABLE `airline_companies` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `contact_info` varchar(255) NOT NULL
);

CREATE TABLE `flights` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `airline_company_id` int NOT NULL,
  `flight_number` varchar(255) NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL
);

CREATE TABLE `catering_facilities` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL
);

CREATE TABLE `catering_orders` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `flight_id` int NOT NULL,
  `facility_id` int NOT NULL,
  `delivery_time` datetime NOT NULL
);

CREATE TABLE `transport` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `vehicle_type` varchar(255) NOT NULL,
  `driver_name` varchar(255) NOT NULL,
  `license_plate` varchar(255) NOT NULL,
  `contact_info` varchar(255) NOT NULL,
  `available` boolean NOT NULL
);

CREATE TABLE `catering_facilities_transport` (
  `facility_id` int NOT NULL,
  `transport_id` int NOT NULL,
  PRIMARY KEY (`facility_id`, `transport_id`)
);

CREATE TABLE `employees` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `facility_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL
);

CREATE TABLE `works_in` (
  `employee_id` int NOT NULL,
  `facility_id` int NOT NULL,
  PRIMARY KEY (`employee_id`, `facility_id`)
);

CREATE TABLE `audit_logs` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `employee_id` int NOT NULL,
  `action` varchar(255) NOT NULL,
  `timestamp` datetime NOT NULL
);

CREATE TABLE `inventory_stock` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `facility_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `unit` varchar(255) NOT NULL
);

CREATE TABLE `snacks` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `brand` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `weight` float NOT NULL,
  `delivered_date` date NOT NULL,
  `expiration_date` date NOT NULL,
  `description` varchar(255) NOT NULL
);

CREATE TABLE `beverages` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `brand` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `weight` float NOT NULL,
  `delivered_date` date NOT NULL,
  `expiration_date` date NOT NULL,
  `description` varchar(255) NOT NULL
);

CREATE TABLE `meals` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `meal_type` varchar(255) NOT NULL,
  `is_vegetarian` boolean NOT NULL,
  `quantity` int NOT NULL,
  `weight` float NOT NULL,
  `created_date` date NOT NULL,
  `description` varchar(255) NOT NULL
);

CREATE TABLE `misc_items` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `weight` float NOT NULL,
  `delivered_date` date NOT NULL,
  `description` varchar(255) NOT NULL
);

CREATE TABLE `fresh_produce` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `weight` float NOT NULL,
  `delivered_date` date NOT NULL,
  `expiration_date` date NOT NULL,
  `description` varchar(255) NOT NULL
);

CREATE TABLE `restock_alerts` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `alert_level` int NOT NULL,
  `created_at` datetime NOT NULL
);

CREATE TABLE `food_waste` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `inventory_id` int NOT NULL,
  `reason` varchar(255) NOT NULL,
  `discarded_at` datetime NOT NULL
);

CREATE TABLE `suppliers` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `contact_info` varchar(255) NOT NULL
);

CREATE TABLE `supplier_orders` (
  `id` int UNIQUE PRIMARY KEY AUTO_INCREMENT,
  `supplier_id` int NOT NULL,
  `facility_id` int NOT NULL,
  `order_date` datetime NOT NULL,
  `status` varchar(255) NOT NULL
);

CREATE TABLE `suppliers_supplies_facilities` (
  `supplier_id` int NOT NULL,
  `facility_id` int NOT NULL,
  PRIMARY KEY (`supplier_id`, `facility_id`)
);

CREATE TABLE `supplier_order_beverages` (
  `supplier_order_id` int NOT NULL,
  `beverage_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`supplier_order_id`, `beverage_id`)
);

CREATE TABLE `supplier_order_snacks` (
  `supplier_order_id` int NOT NULL,
  `snack_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`supplier_order_id`, `snack_id`)
);

CREATE TABLE `supplier_order_misc_items` (
  `supplier_order_id` int NOT NULL,
  `misc_item_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`supplier_order_id`, `misc_item_id`)
);

CREATE TABLE `supplier_order_fresh_produce` (
  `supplier_order_id` int NOT NULL,
  `fresh_produce_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`supplier_order_id`, `fresh_produce_id`)
);

CREATE TABLE `catering_order_beverages` (
  `catering_order_id` int NOT NULL,
  `beverage_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`catering_order_id`, `beverage_id`)
);

CREATE TABLE `catering_order_meals` (
  `catering_order_id` int NOT NULL,
  `meal_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`catering_order_id`, `meal_id`)
);

CREATE TABLE `catering_order_snacks` (
  `catering_order_id` int NOT NULL,
  `snack_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`catering_order_id`, `snack_id`)
);

CREATE TABLE `catering_order_misc_items` (
  `catering_order_id` int NOT NULL,
  `misc_item_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`catering_order_id`, `misc_item_id`)
);

CREATE TABLE `catering_order_fresh_produce` (
  `catering_order_id` int NOT NULL,
  `fresh_produce_id` int NOT NULL,
  `quantity` int NOT NULL,
  PRIMARY KEY (`catering_order_id`, `fresh_produce_id`)
);

ALTER TABLE `flights` ADD FOREIGN KEY (`airline_company_id`) REFERENCES `airline_companies` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_orders` ADD FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_orders` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_facilities_transport` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_facilities_transport` ADD FOREIGN KEY (`transport_id`) REFERENCES `transport` (`id`) ON DELETE CASCADE;

ALTER TABLE `audit_logs` ADD FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

ALTER TABLE `works_in` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `works_in` ADD FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`) ON DELETE CASCADE;

ALTER TABLE `inventory_stock` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `beverages` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `snacks` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `meals` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `fresh_produce` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `misc_items` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `restock_alerts` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `food_waste` ADD FOREIGN KEY (`inventory_id`) REFERENCES `inventory_stock` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_orders` ADD FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_orders` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `suppliers_supplies_facilities` ADD FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE CASCADE;

ALTER TABLE `suppliers_supplies_facilities` ADD FOREIGN KEY (`facility_id`) REFERENCES `catering_facilities` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_beverages` ADD FOREIGN KEY (`supplier_order_id`) REFERENCES `supplier_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_beverages` ADD FOREIGN KEY (`beverage_id`) REFERENCES `beverages` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_snacks` ADD FOREIGN KEY (`supplier_order_id`) REFERENCES `supplier_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_snacks` ADD FOREIGN KEY (`snack_id`) REFERENCES `snacks` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_misc_items` ADD FOREIGN KEY (`supplier_order_id`) REFERENCES `supplier_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_misc_items` ADD FOREIGN KEY (`misc_item_id`) REFERENCES `misc_items` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_fresh_produce` ADD FOREIGN KEY (`supplier_order_id`) REFERENCES `supplier_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `supplier_order_fresh_produce` ADD FOREIGN KEY (`fresh_produce_id`) REFERENCES `fresh_produce` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_beverages` ADD FOREIGN KEY (`catering_order_id`) REFERENCES `catering_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_beverages` ADD FOREIGN KEY (`beverage_id`) REFERENCES `beverages` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_meals` ADD FOREIGN KEY (`catering_order_id`) REFERENCES `catering_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_meals` ADD FOREIGN KEY (`meal_id`) REFERENCES `meals` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_snacks` ADD FOREIGN KEY (`catering_order_id`) REFERENCES `catering_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_snacks` ADD FOREIGN KEY (`snack_id`) REFERENCES `snacks` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_misc_items` ADD FOREIGN KEY (`catering_order_id`) REFERENCES `catering_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_misc_items` ADD FOREIGN KEY (`misc_item_id`) REFERENCES `misc_items` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_fresh_produce` ADD FOREIGN KEY (`catering_order_id`) REFERENCES `catering_orders` (`id`) ON DELETE CASCADE;

ALTER TABLE `catering_order_fresh_produce` ADD FOREIGN KEY (`fresh_produce_id`) REFERENCES `fresh_produce` (`id`) ON DELETE CASCADE;
