package com.xingqi.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupSchemaMigration implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        ensureColumn("user", "id_number", "ALTER TABLE `user` ADD COLUMN `id_number` VARCHAR(18) COMMENT '身份证号'");
    }

    private void ensureColumn(String tableName, String columnName, String alterSql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?"
             )) {
            statement.setString(1, tableName);
            statement.setString(2, columnName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return;
                }
            }

            try (Statement alterStatement = connection.createStatement()) {
                alterStatement.execute(alterSql);
                log.info("数据库字段已补齐: {}.{}", tableName, columnName);
            }
        } catch (Exception e) {
            log.warn("数据库字段迁移跳过: {}.{}, reason={}", tableName, columnName, e.getMessage());
        }
    }
}
