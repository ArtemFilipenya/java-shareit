package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private static final int MIN_ID_VALUE = 1;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private static final String NULL_ITEM_ID_MESSAGE = "itemID is null";
    private static final String NULL_USER_ID_MESSAGE = "userID is null";

    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    public ItemDto createItem(@Validated({Create.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = (NULL_ITEM_ID_MESSAGE))
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemMapper.toModel(itemDto, userId);
        return itemMapper.toDto(itemService.createItem(item));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Validated({Update.class})
                              @RequestBody ItemDto itemDto,
                              @NotNull(message = NULL_ITEM_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @PathVariable Long itemId,
                              @NotNull(message = NULL_USER_ID_MESSAGE)
                              @Min(MIN_ID_VALUE)
                              @RequestHeader(USER_ID_HEADER) Long userId) {
        Item item = itemMapper.toModel(itemDto, userId);
        item.setId(itemId);
        return itemMapper.toDto(itemService.updateItem(item));
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@NotNull(message = NULL_ITEM_ID_MESSAGE)
                                @Min(MIN_ID_VALUE)
                                @PathVariable Long itemId) {
        return itemMapper.toDto(itemService.findItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> findAllItems(@NotNull(message = NULL_USER_ID_MESSAGE)
                                      @Min(MIN_ID_VALUE)
                                      @RequestHeader(USER_ID_HEADER) Long userId) {
        List<Item> userItems = itemService.findAllItems(userId);
        return itemMapper.mapItemListToItemDtoList(userItems);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByRequest(@RequestParam String text) {
        List<Item> foundItems = itemService.findItemsByRequest(text);
        return itemMapper.mapItemListToItemDtoList(foundItems);
    }
}
