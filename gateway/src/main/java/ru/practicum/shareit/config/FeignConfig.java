package ru.practicum.shareit.config;

import feign.Client;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.shareit.exception.CustomErrorDecoder;

@Configuration
public class FeignConfig {
    @Bean
    public Client feignClient() {
        return new feign.okhttp.OkHttpClient();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}