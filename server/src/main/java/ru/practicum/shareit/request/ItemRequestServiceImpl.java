package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository repository, UserService userService, ItemRepository itemRepository) {
        this.repository = repository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public SimpleItemRequestDto create(SimpleItemRequestDto itemRequestDto, long ownerId) {
        User checkUser = userService.findUser(ownerId);
        ItemRequest itemRequest = ItemRequestMapper.fromSimpleDto(itemRequestDto);
        itemRequest.setRequestor(ownerId);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest createdItemRequest = repository.save(itemRequest);
        return ItemRequestMapper.toSimpleDto(createdItemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByOwner(long ownerId, PageRequest pageRequest) {
        User checkUser = userService.findUser(ownerId);
        Page<ItemRequest> page = repository.findAllByRequestorOrderByCreated(ownerId, pageRequest);
        return getItemRequestDtos(page);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long ownerId, PageRequest pageRequest) {
        Page<ItemRequest> page = repository.findAll(pageRequest);
        return getItemRequestDtos(page);
    }

    @Override
    public ItemRequestDto getRequestsById(long ownerId, long id) {
        User checkUser = userService.findUser(ownerId);
        ItemRequest itemRequest = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        itemRequest.setItems(itemRepository.findAllByRequestId(id));

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private List<ItemRequestDto> getItemRequestDtos(Page<ItemRequest> page) {
        List<ItemRequest> list = page.getContent();

        // Собираем все id запросов
        List<Long> requestIds = list.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        // Загружаем все связанные элементы по id запросов
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        // Создаем список ItemRequestDto и связываем элементы с соответствующими ItemRequest
        List<ItemRequestDto> dtoList = list.stream()
                .map(itemRequest -> {
                    List<Item> items = itemsByRequestId.getOrDefault(itemRequest.getId(), Collections.emptyList());
                    itemRequest.setItems(items);
                    return ItemRequestMapper.toItemRequestDto(itemRequest);
                })
                .collect(Collectors.toList());

        return dtoList;
    }
}
