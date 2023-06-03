package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    SimpleItemRequestDto create(SimpleItemRequestDto itemRequest, long ownerId);

    List<ItemRequestDto> getAllRequestsByOwner(long ownerId, PageRequest pageRequest);

    List<ItemRequestDto> getAllRequests(long ownerId, PageRequest pageRequest);

    ItemRequestDto getRequestsById(long ownerId, long id);
}
