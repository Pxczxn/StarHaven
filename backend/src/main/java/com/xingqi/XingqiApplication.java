package com.xingqi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 星栖民宿管理系统
 *
 * @author Xingqi Team
 */
@SpringBootApplication
public class XingqiApplication {

    public static void main(String[] args) {
        ensureDataDirectory();
        SpringApplication.run(XingqiApplication.class, args);
    }

    private static void ensureDataDirectory() {
        try {
            Path dataDirectory = resolveDataDirectory();
            Files.createDirectories(dataDirectory);
            System.setProperty("xingqi.data-dir", dataDirectory.toString().replace("\\", "/"));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create SQLite data directory", e);
        }
    }

    private static Path resolveDataDirectory() {
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        if (Files.exists(workingDirectory.resolve("backend").resolve("pom.xml"))) {
            return workingDirectory.resolve("data");
        }
        if ("backend".equals(workingDirectory.getFileName().toString()) && Files.exists(workingDirectory.resolve("pom.xml"))) {
            return workingDirectory.getParent().resolve("data");
        }
        return workingDirectory.resolve("data");
    }
}
