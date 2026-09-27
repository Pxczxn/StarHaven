USE starhaven;
DELETE FROM `house_image` WHERE `house_id` IN (SELECT `id` FROM `house` WHERE `title` IN ('湖景阁楼'));
DELETE FROM `house_facility` WHERE `house_id` IN (SELECT `id` FROM `house` WHERE `title` IN ('湖景阁楼'));
DELETE FROM `house` WHERE `title` IN ('湖景阁楼');
