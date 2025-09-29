package com.lcms.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@MapperScan("com.lcms.system.mapper")
public class BackendApplication {
    @Value("${server.port}")
    private String port;
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    @EventListener(ApplicationReadyEvent.class)
    public void logUrl() {
        System.out.println("Backend started at: http://localhost:" + port);
    }

}
