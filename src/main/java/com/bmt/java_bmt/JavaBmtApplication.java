package com.bmt.java_bmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JavaBmtApplication {
    public static void main(String[] args) {
        SpringApplication.run(JavaBmtApplication.class, args);
    }
}
