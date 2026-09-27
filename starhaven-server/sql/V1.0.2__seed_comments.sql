-- V1.0.2 评价演示数据
-- 1) 为每个房源补齐若干条评价（已有 3 条及以上的房源跳过，可重复执行）
-- 2) 把 house.avg_score / comment_count 校准为 comment 表的真实值
--    说明：house 表这两个字段是冗余字段，此前由种子数据写死（如 28 条），与实际评论数不一致

INSERT INTO `comment` (`user_id`, `house_id`, `order_id`, `score`, `content`, `create_time`)
SELECT u.id,
       h.id,
       910000 + h.id * 100 + t.seq,
       t.score,
       t.content,
       DATE_SUB(NOW(), INTERVAL t.seq DAY)
FROM `house` h
         JOIN `user` u ON u.username = 'staruser'
         JOIN (
    SELECT 1 AS seq, 5 AS score, '落地窗视野比照片还好，床品干净，晚上很安静，会再来。' AS content
    UNION ALL SELECT 2, 5, '房东回复很快，入住指引清楚，位置也方便，整体超出预期。'
    UNION ALL SELECT 3, 4, '房间整洁、设施齐全，唯一小缺点是热水要放一会儿才热。'
    UNION ALL SELECT 4, 5, '带家人一起住，空间够用，厨房能做饭，性价比很高。'
    UNION ALL SELECT 5, 4, '环境不错，交通便利；隔音一般，夜里能听到走廊声音。'
    UNION ALL SELECT 6, 5, '第二次入住了，依旧稳定发挥，下次出差还订这家。'
) t
WHERE (SELECT COUNT(*) FROM `comment` c WHERE c.house_id = h.id) < 3;

UPDATE `house` h
    LEFT JOIN (
    SELECT `house_id`,
           COUNT(*)            AS cnt,
           ROUND(AVG(`score`), 2) AS avg_score
    FROM `comment`
    GROUP BY `house_id`
) s ON s.house_id = h.id
    SET h.comment_count = IFNULL(s.cnt, 0),
    h.avg_score     = IFNULL(s.avg_score, 0);
