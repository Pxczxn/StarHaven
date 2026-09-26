-- StarHaven schema v1.0.0
CREATE DATABASE IF NOT EXISTS starhaven DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE starhaven;

CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `username`      VARCHAR(64)  NOT NULL,
    `nickname`      VARCHAR(64)  DEFAULT NULL,
    `avatar`        VARCHAR(512) DEFAULT NULL,
    `phone`         VARCHAR(20)  DEFAULT NULL,
    `password`      VARCHAR(128) NOT NULL,
    `gender`        TINYINT      DEFAULT 0 COMMENT '0未知 1男 2女',
    `birthday`      DATE         DEFAULT NULL,
    `role`          VARCHAR(32)  NOT NULL DEFAULT 'USER' COMMENT 'USER/HOST/ADMIN',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0冻结',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`),
    UNIQUE KEY `uk_user_phone` (`phone`)
) COMMENT='用户';

CREATE TABLE IF NOT EXISTS `house` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT,
    `host_id`          BIGINT         NOT NULL,
    `title`            VARCHAR(128)   NOT NULL,
    `cover_image`      VARCHAR(512)   DEFAULT NULL,
    `description`      TEXT           DEFAULT NULL,
    `address`          VARCHAR(255)   DEFAULT NULL,
    `city`             VARCHAR(64)    DEFAULT NULL,
    `latitude`         DECIMAL(10, 6) DEFAULT NULL,
    `longitude`        DECIMAL(10, 6) DEFAULT NULL,
    `price`            DECIMAL(10, 2) NOT NULL,
    `guest_number`     INT            NOT NULL DEFAULT 2,
    `room_number`      INT            NOT NULL DEFAULT 1,
    `bathroom_number`  INT            NOT NULL DEFAULT 1,
    `bed_number`       INT            NOT NULL DEFAULT 1,
    `house_type`       VARCHAR(32)    NOT NULL DEFAULT 'WHOLE' COMMENT 'WHOLE/ROOM/HOTEL',
    `status`           TINYINT        NOT NULL DEFAULT 0 COMMENT '0下架 1上架',
    `audit_status`     TINYINT        NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    `avg_score`        DECIMAL(3, 2)  NOT NULL DEFAULT 0,
    `comment_count`    INT            NOT NULL DEFAULT 0,
    `heat`             INT            NOT NULL DEFAULT 0,
    `create_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_house_city` (`city`),
    KEY `idx_house_host` (`host_id`),
    KEY `idx_house_status` (`status`, `audit_status`)
) COMMENT='房源';

CREATE TABLE IF NOT EXISTS `house_image` (
    `id`        BIGINT       NOT NULL AUTO_INCREMENT,
    `house_id`  BIGINT       NOT NULL,
    `image_url` VARCHAR(512) NOT NULL,
    `sort`      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `idx_house_image_house` (`house_id`)
) COMMENT='房源图片';

CREATE TABLE IF NOT EXISTS `house_facility` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `house_id`      BIGINT      NOT NULL,
    `facility_name` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_house_facility_house` (`house_id`)
) COMMENT='房源设施';

CREATE TABLE IF NOT EXISTS `favorite` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL,
    `house_id`    BIGINT   NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_favorite_user_house` (`user_id`, `house_id`)
) COMMENT='收藏';

CREATE TABLE IF NOT EXISTS `search_history` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `keyword`     VARCHAR(128) NOT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_search_user` (`user_id`)
) COMMENT='搜索历史';

CREATE TABLE IF NOT EXISTS `booking_order` (
    `id`              BIGINT         NOT NULL AUTO_INCREMENT,
    `order_no`        VARCHAR(32)    NOT NULL,
    `user_id`         BIGINT         NOT NULL,
    `house_id`        BIGINT         NOT NULL,
    `check_in_date`   DATE           NOT NULL,
    `check_out_date`  DATE           NOT NULL,
    `guest_count`     INT            NOT NULL,
    `room_count`      INT            NOT NULL DEFAULT 1,
    `contact_name`    VARCHAR(64)    DEFAULT NULL,
    `contact_phone`   VARCHAR(20)    DEFAULT NULL,
    `house_price`     DECIMAL(10, 2) NOT NULL,
    `service_fee`     DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `total_amount`    DECIMAL(10, 2) NOT NULL,
    `order_status`    VARCHAR(32)    NOT NULL DEFAULT 'WAIT_PAY',
    `payment_status`  TINYINT        NOT NULL DEFAULT 0 COMMENT '0未支付 1已支付',
    `create_time`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_order_user` (`user_id`),
    KEY `idx_order_house` (`house_id`)
) COMMENT='订单';

CREATE TABLE IF NOT EXISTS `payment` (
    `id`          BIGINT         NOT NULL AUTO_INCREMENT,
    `order_id`    BIGINT         NOT NULL,
    `payment_no`  VARCHAR(32)    NOT NULL,
    `pay_type`    VARCHAR(32)    NOT NULL COMMENT 'WECHAT/ALIPAY',
    `amount`      DECIMAL(10, 2) NOT NULL,
    `status`      TINYINT        NOT NULL DEFAULT 0 COMMENT '0待支付 1成功 2失败',
    `pay_time`    DATETIME       DEFAULT NULL,
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_payment_order` (`order_id`)
) COMMENT='支付记录';

CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL,
    `house_id`    BIGINT   NOT NULL,
    `order_id`    BIGINT   NOT NULL,
    `score`       TINYINT  NOT NULL,
    `content`     TEXT     DEFAULT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_order` (`order_id`),
    KEY `idx_comment_house` (`house_id`)
) COMMENT='评论';

CREATE TABLE IF NOT EXISTS `comment_image` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `comment_id` BIGINT       NOT NULL,
    `image_url`  VARCHAR(512) NOT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_comment_image` (`comment_id`)
) COMMENT='评论图片';

CREATE TABLE IF NOT EXISTS `message` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `type`        VARCHAR(32)  NOT NULL COMMENT 'ORDER/SYSTEM/ACTIVITY',
    `title`       VARCHAR(128) NOT NULL,
    `content`     VARCHAR(512) DEFAULT NULL,
    `read_status` TINYINT      NOT NULL DEFAULT 0 COMMENT '0未读 1已读',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_message_user` (`user_id`)
) COMMENT='消息通知';

CREATE TABLE IF NOT EXISTS `coupon` (
    `id`               BIGINT         NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(64)    NOT NULL,
    `discount`         DECIMAL(10, 2) NOT NULL,
    `condition_amount` DECIMAL(10, 2) NOT NULL DEFAULT 0,
    `start_time`       DATETIME       NOT NULL,
    `end_time`         DATETIME       NOT NULL,
    `status`           TINYINT        NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) COMMENT='优惠券';

CREATE TABLE IF NOT EXISTS `coupon_user` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `coupon_id`   BIGINT   NOT NULL,
    `user_id`     BIGINT   NOT NULL,
    `used`        TINYINT  NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_coupon_user` (`user_id`)
) COMMENT='用户优惠券';

CREATE TABLE IF NOT EXISTS `banner` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `title`       VARCHAR(128) NOT NULL,
    `subtitle`    VARCHAR(255) DEFAULT NULL,
    `image_url`   VARCHAR(512) NOT NULL,
    `link_url`    VARCHAR(255) DEFAULT NULL,
    `sort`        INT          NOT NULL DEFAULT 0,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`)
) COMMENT='首页 Banner';

CREATE TABLE IF NOT EXISTS `browse_history` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL,
    `house_id`    BIGINT   NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_browse_user` (`user_id`)
) COMMENT='浏览历史';

CREATE TABLE IF NOT EXISTS `host_apply` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL,
    `real_name`   VARCHAR(64)  NOT NULL,
    `id_card`     VARCHAR(32)  DEFAULT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    `remark`      VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_host_apply_user` (`user_id`)
) COMMENT='房东认证';
