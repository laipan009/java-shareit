package ru.practicum.shareit.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;

import javax.validation.constraints.Min;
import java.util.List;

@FeignClient(name = "request-server", url = "http://server:8090/requests")
public interface RequestFeignClient {

    String HEADER_WITH_USER_ID = "X-Sharer-User-Id";


    @PostMapping
    ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping
    List<RequestDtoResponse> getRequestsForRequester(@RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping("/{requestId}")
    RequestDtoResponse getRequestById(@PathVariable Integer requestId,
                                      @RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping("/all")
    List<RequestDtoResponse> getRequests(
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(value = 0) Integer from,
            @RequestParam(value = "size", defaultValue = "1", required = false) @Min(value = 0) Integer size,
            @RequestHeader(HEADER_WITH_USER_ID) Integer userId);
}