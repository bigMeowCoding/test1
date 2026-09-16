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
