
-- 用户表（存储所有系统用户）
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户唯一标识',
                                      `name` VARCHAR(50) NOT NULL COMMENT '用户真实姓名',
                                      `username` VARCHAR(30) NOT NULL UNIQUE COMMENT '登录用户名',
                                      `password` VARCHAR(100) NOT NULL COMMENT '加密后的密码',
                                      `phone` VARCHAR(20) NOT NULL COMMENT '手机号码',
                                      `role` ENUM('client', 'admin', 'delivery') NOT NULL DEFAULT 'client' COMMENT '用户角色',
                                      `token` VARCHAR(255) COMMENT '认证令牌',
                                      `image_url` VARCHAR(255) COMMENT '头像URL',
                                      `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                      `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统用户表';

-- 商品表（存储所有商品信息）
CREATE TABLE IF NOT EXISTS `product` (
                                         `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '商品唯一标识',
                                         `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
                                         `description` TEXT COMMENT '商品详细描述',
                                         `price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '商品单价',
                                         `amount` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '库存数量',
                                         `image_url` VARCHAR(255) COMMENT '商品主图URL',
                                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='商品信息表';

-- 订单表（存储所有订单信息）
CREATE TABLE IF NOT EXISTS `orders` (
                                        `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '订单唯一标识',
                                        `user_id` INT UNSIGNED NOT NULL COMMENT '下单用户ID',
                                        `delivery_id` INT UNSIGNED COMMENT '配送员ID',
                                        `status` ENUM('pending', 'delivering', 'completed') NOT NULL DEFAULT 'pending' COMMENT '订单状态',
                                        `total_price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '订单总金额',
                                        `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单创建时间',
                                        `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
                                        `remark` VARCHAR(255) COMMENT '订单备注',
                                        `address` VARCHAR(255) NOT NULL COMMENT '配送地址',
                                        `contact_phone` VARCHAR(20) NOT NULL COMMENT '收货人联系电话',
                                        INDEX `idx_user_id` (`user_id`),
                                        INDEX `idx_delivery_id` (`delivery_id`),
                                        FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
                                        FOREIGN KEY (`delivery_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='订单主表';

-- 订单项表（存储订单中的商品明细）
CREATE TABLE IF NOT EXISTS `order_item` (
                                            `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '订单项唯一标识',
                                            `order_id` INT UNSIGNED NOT NULL COMMENT '关联订单ID',
                                            `product_id` INT UNSIGNED NOT NULL COMMENT '商品ID',
                                            `quantity` INT UNSIGNED NOT NULL COMMENT '购买数量',
                                            `price` DECIMAL(10,2) UNSIGNED NOT NULL COMMENT '购买时单价',
                                            INDEX `idx_order_id` (`order_id`),
                                            INDEX `idx_product_id` (`product_id`),
                                            FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
                                            FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='订单商品明细表';

