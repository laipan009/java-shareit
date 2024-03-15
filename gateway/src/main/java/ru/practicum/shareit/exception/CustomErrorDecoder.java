package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            Map<String, String> errorAttributes = objectMapper.readValue(response.body().asInputStream(), Map.class);

            String errorMessage = errorAttributes.values().stream()
                    .map(String::valueOf)
                    .findFirst()
                    .get();
            System.out.println("!!!!!!!!!!!!!! " + errorMessage);

            HttpStatus status = HttpStatus.valueOf(response.status());
            return new ResponseStatusException(status, errorMessage);
        } catch (IOException e) {
            return new Exception("Ошибка при десериализации ответа об ошибке");
        }
    }
}
