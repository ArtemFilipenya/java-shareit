package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @GetMapping("/{itemId}")
    public ItemAllDto get(@RequestHeader(value = HEADER) Long userId,
                          @PathVariable Long itemId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping()
    public List<ItemAllDto> getAllItems(@RequestHeader(value = HEADER) Long userId,
                                        @RequestParam(required = false) Integer from,
                                        @RequestParam(required = false) Integer size) {
        return itemService.getAll(userId, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(value = HEADER, required = false)
                                    Long userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }

    @PostMapping()
    public ItemDto save(@RequestHeader(HEADER) Long userId,
                        @RequestBody ItemDto itemDto) {
        ItemRequestDto itemRequestDto = itemDto.getRequestId() != null ? itemRequestService.getItemRequestById(itemDto.getRequestId(), userId) : null;

        return itemService.save(itemDto, itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(value = HEADER) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(value = HEADER) Long userId,
                                @RequestParam String text,
                                @RequestParam(required = false) Integer from,
                                @RequestParam(required = false) Integer size) {
        return itemService.getByText(text.toLowerCase(), userId, from, size);
    }
}