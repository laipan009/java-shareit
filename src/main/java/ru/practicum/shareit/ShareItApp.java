package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ShareItApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(ShareItApp.class, args);
	}
}