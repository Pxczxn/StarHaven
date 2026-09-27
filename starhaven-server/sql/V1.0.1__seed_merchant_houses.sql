-- StarHaven data v1.0.1 商家演示房源（依赖已有 host 账号）
USE starhaven;

UPDATE `user` SET nickname = '星栖商家', role = 'HOST' WHERE username = 'host';

INSERT INTO `house` (`host_id`, `title`, `cover_image`, `description`, `address`, `city`, `price`,
                     `guest_number`, `room_number`, `bathroom_number`, `bed_number`, `house_type`,
                     `status`, `audit_status`, `avg_score`, `comment_count`, `heat`)
SELECT u.id, '湖景阁楼', '/houses/house-hangzhou-cover.png', '挑高阁楼，落地窗朝向内湖。', '阁楼 201', '星栖民宿', 538.00,
       3, 1, 1, 2, 'WHOLE', 1, 1, 4.80, 12, 70
FROM `user` u
WHERE u.username = 'host'
  AND NOT EXISTS (SELECT 1 FROM `house` h WHERE h.title = '湖景阁楼');
