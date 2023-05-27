package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @GetMapping()
    public List<ItemRequestDto> getItemRequests(@RequestHeader(value = HEADER, required = false) Long userId) {
        log.info("ItemRequestController.getItemRequests()");
        return itemRequestService.getAllItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(value = HEADER, required = false) Long userId,
                                         @PathVariable Long requestId) {
        log.info("ItemRequestController.getItemRequest(userId:{}, requestId:{})", userId, requestId);
        return itemRequestService.getItemRequestById(requestId, userId);
    }

    @PostMapping()
    public ItemRequestDto createItemRequest(@RequestHeader(value = HEADER, required = false) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("ItemRequestController.createItemRequest(userId:{})", userId);
        return itemRequestService.save(itemRequestDto, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestHeader(value = HEADER, required = false) Long userId,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        log.info("ItemRequestController.getAllItemRequests(userId:{})", userId);
        Pageable pageable = createPageable(from, size);
        return itemRequestService.getAllItemRequests(pageable, userId);
    }

    private Pageable createPageable(Integer from, Integer size) {
        if (from != null && from < 0) {
            throw new IllegalArgumentException("Invalid 'from' value: " + from);
        }
        if (size != null && size <= 0) {
            throw new IllegalArgumentException("Invalid 'size' value: " + size);
        }
        int pageNumber = from != null ? from : 0;
        int pageSize = size != null ? size : 10;
        return PageRequest.of(pageNumber, pageSize, Sort.by("created").descending());
    }
}