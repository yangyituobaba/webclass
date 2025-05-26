# CREATE TABLE user (
#                       id INT PRIMARY KEY AUTO_INCREMENT,
#                       name VARCHAR(100),
#                       username VARCHAR(50) NOT NULL UNIQUE,
#                       password VARCHAR(100) NOT NULL,
#                       phone VARCHAR(20),
#                       role VARCHAR(20) NOT NULL CHECK (role IN ('client', 'admin', 'delivery')),
#                       token VARCHAR(255),
#                       is_active BOOLEAN DEFAULT TRUE
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE product (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(100) NOT NULL,
                         description TEXT,
                         price DECIMAL(10,2) NOT NULL,
                         image_url VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE orders (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT NOT NULL,
                        delivery_id INT DEFAULT NULL,
                        status VARCHAR(50) NOT NULL,
                        total_price DECIMAL(10,2) NOT NULL,
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES user(id),
                        FOREIGN KEY (delivery_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE order_item (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            order_id INT NOT NULL,
                            product_id INT NOT NULL,
                            quantity INT NOT NULL,
                            price DECIMAL(10,2) NOT NULL,
                            FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
