package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestStorage;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.util.PageInfo.createPageRequest;

@Slf4j
@Service
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestStorage itemRequestStorage;
    private final ItemService itemService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestStorage itemRequestStorage, UserService userService, ItemService itemService) {
        this.itemRequestStorage = itemRequestStorage;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ItemRequestDto save(ItemRequestDto itemRequestDto, Long requesterId) {
        checkToValid(itemRequestDto);
        User user = UserMapper.convertDtoToModel(userService.get(requesterId));
        ItemRequest itemRequest = ItemRequestMapper.convertDtoToModel(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(now());
        ItemRequest requestForSave = itemRequestStorage.save(itemRequest);
        return ItemRequestMapper.convertModelToDto(requestForSave);
    }


    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Long userId) {
        List<ItemRequest> requests = getItemRequests(from, size, userId);
        List<ItemDto> items = itemService.getItemsByRequests(requests);
        return mapToItemRequestDto(requests, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Long userId) {
        User user = UserMapper.convertDtoToModel(userService.get(userId));
        List<ItemRequest> itemRequests = itemRequestStorage.findItemRequestByRequesterOrderByCreatedDesc(user);
        List<ItemDto> items = itemService.getItemsByRequests(itemRequests);
        Map<Long, List<ItemDto>> itemsByRequest = items
                .stream()
                .collect(groupingBy(ItemDto::getRequestId, toList()));
        return itemRequests.stream()
                .map(itemRequest -> ItemRequestMapper.convertModelToDto(itemRequest, itemsByRequest.get(itemRequest.getId())))
                .collect(toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(long requestId, Long userId) {
        User owner = UserMapper.convertDtoToModel(userService.get(userId));
        if (owner != null) {
            List<ItemDto> items = itemService.getItemsByRequestId(requestId);
            ItemRequest itemRequest = itemRequestStorage.findItemRequestById(requestId);

            if (itemRequest == null) {
                throw new ObjectNotFoundException("Request with id= " + requestId + " not found");
            }
            return ItemRequestMapper.convertModelToDto(itemRequest, items);
        } else {
            throw new ObjectNotFoundException("User with id= " + userId + " not found");
        }
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Pageable pageable, Long userId) {
        List<ItemRequest> requests = getItemRequests(pageable, userId);
        List<ItemDto> items = itemService.getItemsByRequests(requests);
        return mapToItemRequestDto(requests, items);
    }

    private List<ItemRequest> getItemRequests(Pageable pageable, Long userId) {
        if (pageable.isPaged()) {
            return itemRequestStorage.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId, pageable)
                    .stream()
                    .collect(Collectors.toList());
        } else {
            return itemRequestStorage.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId);
        }
    }

    private List<ItemRequest> getItemRequests(Integer from, Integer size, Long userId) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 10;
        }
        PageRequest pageRequest = createPageRequest(from, size, Sort.by("created").descending());
        if (pageRequest == null) {
            return itemRequestStorage.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId);
        } else {
            return itemRequestStorage.findItemRequestByRequester_IdIsNotOrderByCreatedDesc(userId, pageRequest)
                    .stream()
                    .collect(Collectors.toList());
        }
    }

    private List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> requests, List<ItemDto> items) {
        return requests.stream()
                .map(itemRequest -> ItemRequestMapper.convertModelToDto(itemRequest, items))
                .collect(Collectors.toList());
    }

    private void checkToValid(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new BadParameterException("Description cannot be empty or null");
        }
    }
}