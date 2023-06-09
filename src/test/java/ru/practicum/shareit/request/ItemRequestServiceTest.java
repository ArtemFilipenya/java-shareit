package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestStorage;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.request.mapper.ItemRequestMapper.convertDtoToModel;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    @Mock
    private ItemRequestStorage itemRequestRepository;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void initialize() {
        userDto = new UserDto(1L, "marry", "marry@mail.com");
        itemRequestDto = new ItemRequestDto(1L, "my request", 1L, now(), of());
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemService);
        itemRequest = convertDtoToModel(itemRequestDto, userDto);
    }

    ItemRequestDto saveItemRequestDto() {
        when(userService.get(any())).thenReturn(userDto);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        return itemRequestService.save(itemRequestDto, userDto.getId());
    }

    @Test
    void saveItemEmptyDescriptionTest() {
        Exception exception = assertThrows(BadParameterException.class, () -> itemRequestService.save(new ItemRequestDto(1L,
                "", 1L, now(), of()), 1L));
        assertEquals("Description cannot be empty or null", exception.getMessage());
    }

    @Test
    void saveItemRequestTest() {
        ItemRequestDto dto = saveItemRequestDto();
        assertEquals(dto.getRequesterId(), itemRequest.getRequester().getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertEquals(dto.getId(), itemRequest.getId());
    }

    @Test
    void getItemRequestsTest() {
        saveItemRequestDto();
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any()))
                .thenReturn(of(itemRequest));
        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(allItemRequests.get(0).getItems().size(), 0);
        assertEquals(allItemRequests.size(), 1);
    }

    @Test
    void getItemRequestsItemsTest() {
        saveItemRequestDto();
        when(itemService.getItemsByRequests(any()))
                .thenReturn(of(new ItemDto(1L, "Toy", "my toy", true, 3L)));
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any()))
                .thenReturn(of(itemRequest));
        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.get(0).getId(), itemRequest.getId());
        assertEquals(allItemRequests.size(), 1);
    }

    @Test
    void getItemRequestsEmptyTest() {
        saveItemRequestDto();
        when(itemRequestRepository.findItemRequestByRequesterOrderByCreatedDesc(any())).thenReturn(of());
        List<ItemRequestDto> allItemRequests = itemRequestService.getAllItemRequests(userDto.getId());
        assertEquals(allItemRequests.size(), 0);
    }
}