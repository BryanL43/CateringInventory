/*
Do not alter this file. Order of execution is important for integration test cases.
*/

-- Insert airline data
INSERT INTO catering_logistics.airline_companies VALUES (null, "Test Airline", "Admin@example.com");
INSERT INTO catering_logistics.airline_companies VALUES (null, "Delta Air Lines", "Delta@example.com");

-- Insert flight data
INSERT INTO catering_logistics.flights VALUES (null, 1, "TS1234", NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR));
INSERT INTO catering_logistics.flights VALUES (null, 1, "TS5678", NOW(), DATE_ADD(NOW(), INTERVAL 3 HOUR));
INSERT INTO catering_logistics.flights VALUES (null, 2, "DL1234", NOW(), DATE_ADD(NOW(), INTERVAL 6 HOUR));

-- Insert facility data
INSERT INTO catering_logistics.catering_facilities VALUES (null, "Test Facility", "Test Street");
INSERT INTO catering_logistics.catering_facilities VALUES (null, "Primary Facility", "Main Street");

-- Insert inventory stock data
INSERT INTO catering_logistics.inventory_stock VALUES (null, 2, "Main Inventory", "kg");

-- Insert beverage data
INSERT INTO catering_logistics.beverages VALUES (null, 1, "Test Beverage", "Test Brand", 80, 2.54, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), "Test Description");
INSERT INTO catering_logistics.beverages VALUES (null, 1, "Sprite", "Coca-cola", 70, 3.27, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 2 DAY), "Lemon-lime flavored soft drink");

-- Insert meal data
INSERT INTO catering_logistics.meals VALUES (null, 1, "Chicken rice", "entree", FALSE, 120, 2.5, CURRENT_DATE, "Default meal option");

-- Insert catering order data
INSERT INTO catering_logistics.catering_orders VALUES (null, 1, 1, NOW());