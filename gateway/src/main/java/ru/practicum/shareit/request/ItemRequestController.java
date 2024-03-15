package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    private final RequestFeignClient feignClient;

    @Autowired
    public ItemRequestController(RequestFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@Validated(OnCreate.class) @RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return feignClient.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<RequestDtoResponse> getRequestsForRequester(@RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return feignClient.getRequestsForRequester(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDtoResponse getRequestById(@PathVariable Integer requestId,
                                             @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return feignClient.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDtoResponse> getRequests(
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(value = 0) Integer from,
            @RequestParam(value = "size", defaultValue = "1", required = false) @Min(value = 0) Integer size,
            @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return feignClient.getRequests(from, size, userId);
    }
}