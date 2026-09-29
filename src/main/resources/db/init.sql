CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(60) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0)
);

INSERT INTO books(title, author, price, stock)
SELECT 'Java 核心技术 卷 I', 'Cay S. Horstmann', 118.00, 20
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Java 核心技术 卷 I');
INSERT INTO books(title, author, price, stock)
SELECT 'Head First Java', 'Kathy Sierra', 89.00, 15
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = 'Head First Java');
INSERT INTO books(title, author, price, stock)
SELECT '代码整洁之道', 'Robert C. Martin', 69.00, 8
WHERE NOT EXISTS (SELECT 1 FROM books WHERE title = '代码整洁之道');

CREATE TABLE IF NOT EXISTS inventories (
    book_id BIGINT PRIMARY KEY,
    available_quantity INT NOT NULL CHECK (available_quantity >= 0),
    reserved_quantity INT NOT NULL DEFAULT 0 CHECK (reserved_quantity >= 0),
    sold_quantity INT NOT NULL DEFAULT 0 CHECK (sold_quantity >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_inventory_book FOREIGN KEY (book_id) REFERENCES books(id)
);

INSERT INTO inventories (book_id, available_quantity, reserved_quantity, sold_quantity, version)
SELECT id, stock, 0, 0, 0 FROM books
WHERE NOT EXISTS (SELECT 1 FROM inventories WHERE inventories.book_id = books.id);

-- 兼容库存表创建前已存在的图书。仅修复尚未产生订单流转的空库存，避免覆盖真实库存状态。
UPDATE inventories i
JOIN books b ON b.id = i.book_id
SET i.available_quantity = b.stock,
    i.version = i.version + 1
WHERE i.available_quantity = 0
  AND i.reserved_quantity = 0
  AND i.sold_quantity = 0
  AND b.stock > 0;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL CHECK (total_amount >= 0),
    created_at TIMESTAMP(6) NOT NULL,
    paid_at TIMESTAMP(6) NULL,
    cancelled_at TIMESTAMP(6) NULL
);

CREATE TABLE IF NOT EXISTS order_lines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    book_title VARCHAR(100) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    quantity INT NOT NULL CHECK (quantity > 0),
    line_amount DECIMAL(10, 2) NOT NULL CHECK (line_amount >= 0),
    CONSTRAINT fk_order_line_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
