package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(value = "X-Sharer-User-Id") long ownerId) {
        ItemRequest itemRequest = ItemRequestMapper.fromDto(itemRequestDto);
        ItemRequest createdItemRequest = itemRequestService.create(itemRequest, ownerId);
        return ItemRequestMapper.toItemRequestDto(createdItemRequest);
    }

    @GetMapping(value = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(ownerId, PageRequest.of(from, size));
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByOwner(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequestsByOwner(ownerId, PageRequest.of(from, size));
    }

    @GetMapping(value = "/{id}")
    public ItemRequestDto getRequestsById(@RequestHeader(value = "X-Sharer-User-Id") long ownerId,
                                          @PathVariable("id") Long id) {
        return itemRequestService.getRequestsById(ownerId, id);
    }
}