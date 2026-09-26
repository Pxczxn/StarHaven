package top.pxczxn.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * StarHaven 唯一 Spring Boot 入口：用户端、管理端与定时任务同一进程。
 */
@EnableScheduling
@MapperScan({"top.pxczxn.system.mapper", "top.pxczxn.business.mapper"})
@SpringBootApplication(scanBasePackages = "top.pxczxn")
public class StarhavenApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarhavenApiApplication.class, args);
    }
}
