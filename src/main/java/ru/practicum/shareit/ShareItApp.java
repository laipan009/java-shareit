package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }
}