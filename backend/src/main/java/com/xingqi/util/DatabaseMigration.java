package com.xingqi.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseMigration {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:xingqi.db";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // 添加 payment_status 列
            String sql = "ALTER TABLE booking_order ADD COLUMN payment_status VARCHAR(20) DEFAULT 'unpaid'";
            stmt.execute(sql);
            System.out.println("✅ 成功添加 payment_status 列");

            // 更新现有数据
            String updateSql = "UPDATE booking_order SET payment_status = 'unpaid' WHERE payment_status IS NULL";
            stmt.execute(updateSql);
            System.out.println("✅ 成功更新现有数据");

        } catch (Exception e) {
            if (e.getMessage().contains("duplicate column")) {
                System.out.println("⚠️  payment_status 列已存在，跳过");
            } else {
                System.err.println("❌ 执行失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
