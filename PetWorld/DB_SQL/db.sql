-- Create Account Table
CREATE TABLE Account (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    username VARCHAR(100) UNIQUE NOT NULL, 
    passw VARCHAR(255) NOT NULL, 
    account_type VARCHAR(50) CHECK (account_type IN ('ADMIN', 'STAFF')) NOT NULL
);

-- Create PetSpecies Table
CREATE TABLE PetSpecies (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    species_name VARCHAR(100) UNIQUE NOT NULL, 
    avg_min_price FLOAT NOT NULL, 
    avg_max_price FLOAT NOT NULL, 
    avg_weight FLOAT NOT NULL, 
    avg_max_age INT NOT NULL,
    image_url TEXT, -- Cột lưu trữ URL của hình ảnh
	des TEXT -- Cột miêu tả
);

-- Create Pet Table
CREATE TABLE Pet (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    pet_name VARCHAR(100) UNIQUE NOT NULL, 
    price FLOAT NOT NULL, 
    age INT NOT NULL, 
    color VARCHAR(7) NOT NULL,  -- Hexadecimal color code
    description TEXT, 
    state VARCHAR(50) NOT NULL, 
    pet_species_id INT
);
ALTER TABLE Pet
ADD images TEXT,
    weight FLOAT;

-- Create Customer Table
CREATE TABLE Customer (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    phone VARCHAR(15) UNIQUE NOT NULL, 
    name VARCHAR(100) NOT NULL
);

-- Create Order Table
CREATE TABLE [Order] (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    order_date DATETIME NOT NULL, 
    total_amount FLOAT NOT NULL, 
    customer_id INT, 
    staff_id INT
);

-- Create OrderDetail Table
CREATE TABLE OrderDetail (
    id INT IDENTITY(1,1) PRIMARY KEY, 
    sale_price FLOAT NOT NULL, 
    order_id INT, 
    pet_id INT
);

-- Add Foreign Key Constraints

ALTER TABLE Pet
ADD CONSTRAINT FK_PetSpecies FOREIGN KEY (pet_species_id) REFERENCES PetSpecies(id);

ALTER TABLE [Order]
ADD CONSTRAINT FK_Customer FOREIGN KEY (customer_id) REFERENCES Customer(id),
    CONSTRAINT FK_Account FOREIGN KEY (staff_id) REFERENCES Account(id);

ALTER TABLE OrderDetail
ADD CONSTRAINT FK_Order FOREIGN KEY (order_id) REFERENCES [Order](id),
    CONSTRAINT FK_Pet FOREIGN KEY (pet_id) REFERENCES Pet(id);

ALTER TABLE [Order]
ADD CONSTRAINT FK_Order_Customer
FOREIGN KEY (customer_id) REFERENCES Customer(id)
ON DELETE CASCADE
ON UPDATE CASCADE;