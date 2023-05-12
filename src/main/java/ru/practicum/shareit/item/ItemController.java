package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private static final String HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping("/{id}")
    public ItemDtoResponse getItemById(@PathVariable long id, @RequestHeader(HEADER) long userId) {
        log.info("ItemController.getItemById() itemId={} userId={}", id, userId);
        return itemService.getById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> search(@RequestParam String text) {
        return text.isBlank() ? new ArrayList<>() : itemService.search(text);
    }

    @GetMapping
    public List<ItemDtoResponse> findAll(@RequestHeader(HEADER) long userId) {
        log.info("ItemController.findAll() userId={}", userId);
        return itemService.findAll(userId);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse update(@PathVariable long id,
                                  @Validated(Update.class) @RequestBody ItemDtoRequest itemDtoRequest,
                                  @RequestHeader(HEADER) long userId) {
        log.info("ItemController.update() itemId={}, userId={} ", id, userId);
        return itemService.update(userId, id, itemDtoRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id,
                           @RequestHeader(HEADER) long userId) {
        log.info("ItemController.deleteById() itemId= {} userId={}", id, userId);
        itemService.deleteById(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createNewComment(@PathVariable long itemId,
                                       @Valid @RequestBody CommentDto commentDto,
                                       @RequestHeader(HEADER) long userId) {
        log.info("ItemController.createNewComment() itemId={} userId={}", itemId, userId);
        return commentService.create(userId, itemId, commentDto);
    }

    @PostMapping
    public ItemDtoResponse create(@Validated(Create.class) @RequestBody ItemDtoRequest itemDtoRequest,
                                  @RequestHeader(HEADER) long userId) {
        log.info("ItemController.create() with id={}", userId);
        return itemService.create(userId, itemDtoRequest);
    }
}