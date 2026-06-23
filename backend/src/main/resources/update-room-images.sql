-- 为已有房型和房间补充本地图片地址
UPDATE `room_type`
SET `image_url` = '/uploads/rooms/room-default.png'
WHERE `id` IN (1, 2, 3);

UPDATE `room`
SET `image_url` = CASE `room_no`
    WHEN '101' THEN '/uploads/rooms/room-101.png'
    WHEN '102' THEN '/uploads/rooms/room-102.png'
    WHEN '201' THEN '/uploads/rooms/room-201.png'
    WHEN '202' THEN '/uploads/rooms/room-202.png'
    WHEN '203' THEN '/uploads/rooms/room-203.png'
    WHEN '301' THEN '/uploads/rooms/room-301.png'
    WHEN '302' THEN '/uploads/rooms/room-302.png'
    ELSE `image_url`
END
WHERE `room_no` IN ('101', '102', '201', '202', '203', '301', '302');
