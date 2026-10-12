-- Check if the database schema of the pharmacy already existed and deleting it
DROP DATABASE IF EXISTS pharmacy_inventory_db;

-- creating a database schema for HealthFirst Pharmacy
CREATE DATABASE pharmacy_inventory_db;
USE pharmacy_inventory_db;

-- creating table for user to store their login details
CREATE TABLE users (
    user_id     INT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('Admin', 'Cashier') NOT NULL,
    full_name   VARCHAR(100) NOT NULL
);

-- Creating table for suppliers to store information about the companies that supply medicines.
CREATE TABLE suppliers (
    supplier_id     INT PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    contact_person  VARCHAR(100),
    phone           VARCHAR(20),
    email           VARCHAR(100),
    address         TEXT
);

-- Creating table to store medicines froms suppliers
CREATE TABLE medicines (
    medicine_id         INT PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(150) NOT NULL,
    company             VARCHAR(100),
    medicine_type       VARCHAR(50),
    price               DECIMAL(10,2) NOT NULL,
    quantity_in_stock   INT NOT NULL,
    reorder_level       INT NOT NULL,
    expiry_date         DATE NOT NULL,
    supplier_id         INT,
    CONSTRAINT fk_medicine_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
        ON DELETE SET NULL
);

-- creating table for sales to store completed sale/transaction
CREATE TABLE sales (
    sale_id       INT PRIMARY KEY AUTO_INCREMENT,
    sale_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount  DECIMAL(10,2) NOT NULL,
    user_id       INT NOT NULL,
    CONSTRAINT fk_sale_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE RESTRICT
);

-- TABLE 5: sale_items
-- Line items belonging to a sale (the "receipt lines").
-- This keeps the design normalized: a sale can have many items,
-- and we record the price AT THE TIME OF SALE so it never changes
-- even if the medicine's price changes later.
CREATE TABLE sale_items (
    sale_item_id    INT PRIMARY KEY AUTO_INCREMENT,
    sale_id         INT NOT NULL,
    medicine_id     INT NOT NULL,
    quantity_sold   INT NOT NULL,
    price_at_sale   DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_saleitem_sale
        FOREIGN KEY (sale_id) REFERENCES sales(sale_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_saleitem_medicine
        FOREIGN KEY (medicine_id) REFERENCES medicines(medicine_id)
        ON DELETE RESTRICT
);

-- insering sample data to the pharamacy database

-- creating sample users and passwords for user to able to accwaa the pharmacy system
INSERT INTO users (username, password, role, full_name) VALUES
('admin',   'admin123', 'Admin',   'Mashudu Netshisaulu'),
('cashier', 'cash123',  'Cashier', 'Wamashudu Netshisaulu');

-- Sample data for suppliers
INSERT INTO suppliers (name, contact_person, phone, email, address) VALUES
('MediSupply Co.','Sam Maluleke','068 586 3134','sales@medisupply.co.za', '12 Main Rd, Johannesburg'),
('PharmaCorp Ltd.','Thandaza Mukwevho','072 7273 897','orders@pharmacorp.co.za','112 Long St, Cape Town'),
('HealthLine Wholesale','James Motsamai','072 187 7332','info@healthline.co.za','78 Ridge Rd, Durban');

-- Sample medicines
INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) VALUES
('Paracetamol 500mg',    'Adcock Ingram',  'Tablet',    25.50, 200, 50, '2027-06-30', 1), 
('Amoxicillin 250mg',    'Aspen Pharma',   'Capsule',   65.00, 15,  30, '2027-01-15', 1), 
('Cough Syrup 100ml',    'Sandoz',         'Syrup',     45.75, 80,  20, '2026-09-25', 2), 
('Ibuprofen 200mg',      'GSK',            'Tablet',    32.00, 120, 40, '2027-03-10', 2), 
('Insulin Injection',    'Novo Nordisk',   'Injection', 210.00,10,  15, '2026-10-01', 3), 
('Hydrocortisone Cream', 'Aspen Pharma',   'Cream',     55.20, 60,  20, '2027-08-20', 3), 
('Vitamin C 1000mg',     'Pharma Dynamics','Tablet',    40.00, 5,   25, '2026-09-20', 1), 
('Antacid Suspension',   'Sandoz',         'Syrup',     28.90, 90,  25, '2027-05-05', 2); 

-- sanple data for sale items
INSERT INTO sales (total_amount, user_id) VALUES
(96.25, 2);

INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES
(1, 1, 2, 25.50), 
(1, 3, 1, 45.75);  

-- updating the sale items in the medicine
UPDATE medicines SET quantity_in_stock = quantity_in_stock - 2 WHERE medicine_id = 1;
UPDATE medicines SET quantity_in_stock = quantity_in_stock - 1 WHERE medicine_id = 3;


-- creating a sales report
SELECT
    sa.sale_id      AS "Sale ID",
    sa.sale_date    AS "Sale Date",
    u.full_name     AS "Cashier",
    sa.total_amount AS "Total Amount"
FROM sales sa
JOIN users u ON sa.user_id = u.user_id
ORDER BY sa.sale_date DESC;

-- Creating an item-Wise Report 
SELECT
    m.name                                    AS "Medicine",
    SUM(si.quantity_sold)                     AS "Quantity Sold",
    SUM(si.quantity_sold * si.price_at_sale)  AS "Revenue"
FROM sale_items si
JOIN medicines m ON si.medicine_id = m.medicine_id
GROUP BY m.medicine_id, m.name
ORDER BY Revenue DESC;

-- creating a low stock report
SELECT
    m.name              AS "Medicine",
    m.quantity_in_stock AS "Current Stock",
    m.reorder_level     AS "Reorder Level",
    s.name              AS "Supplier"
FROM medicines m
LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id
WHERE m.quantity_in_stock <= m.reorder_level
ORDER BY m.quantity_in_stock ASC;

-- ----- Creating an expiry report for items that are about to expire
SELECT
    m.name              AS "Medicine",
    m.expiry_date       AS "Expiry Date",
    m.quantity_in_stock AS "Current Stock",
    s.name              AS "Supplier"
FROM medicines m
LEFT JOIN suppliers s ON m.supplier_id = s.supplier_id
WHERE m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH)
ORDER BY m.expiry_date ASC;
