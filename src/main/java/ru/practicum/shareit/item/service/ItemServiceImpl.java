package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDtoResponse create(long userId, ItemDtoRequest itemDtoRequest) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User ID = " + userId + " not found."));
        Item item = ItemMapper.fromItemDtoRequest(itemDtoRequest, owner);
        item.setOwner(owner);
        Item result = itemRepository.save(item);
        return ItemMapper.toItemDtoResponse(result);
    }

    @Override
    @Transactional
    public ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Item with id=%s not found", itemId)));

        if (item.getOwner().getId() != userId) {
            throw new AccessException(String.format("User with id=%s is not owner", userId));
        }
        if (itemDtoRequest.getName() != null && !itemDtoRequest.getName().isBlank()) {
            item.setName(itemDtoRequest.getName());
        }
        if (itemDtoRequest.getAvailable() != null) {
            item.setAvailable(itemDtoRequest.getAvailable());
        }
        if (itemDtoRequest.getDescription() != null && !itemDtoRequest.getDescription().isBlank()) {
            item.setDescription(itemDtoRequest.getDescription());
        }
        return ItemMapper.toItemDtoResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getById(long userId, long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Not Found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Not Found"));
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item);

        itemDtoResponse.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::convertToCommentDto)
                .collect(toList()));

        if (userId == item.getOwner().getId()) {
            LocalDateTime timeNow = LocalDateTime.now();
            Booking nextBooking = findNextBooking(itemId, timeNow, BookingStatus.APPROVED);
            Booking lastBooking = findLastBooking(itemId, timeNow);

            if (lastBooking == null) {
                itemDtoResponse.setLastBooking(null);
            } else {
                itemDtoResponse.setLastBooking(BookingMapper.toShortBookingDto(lastBooking));
            }
            if (nextBooking == null) {
                itemDtoResponse.setNextBooking(null);
            } else {
                itemDtoResponse.setNextBooking(BookingMapper.toShortBookingDto(nextBooking));
            }
        }
        return itemDtoResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> findAll(long userId) {
        LocalDateTime timeNow = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        Map<Item, List<Comment>> comments = commentRepository.findAllByItemIn(items, Sort.by(Sort.Direction.DESC,
                        "created"))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem));

        Map<Item, List<Booking>> bookings = bookingRepository
                .findAllByItemInAndStatus(items, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem));

        return items.stream().map(item -> {
            ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponse(item);
            itemDtoResponse.setComments(comments.getOrDefault(item, new ArrayList<>()).stream()
                    .map(CommentMapper::convertToCommentDto).collect(toList()));

            List<Booking> bookingsForResult = bookings.getOrDefault(item, new ArrayList<>());
            Booking lastBooking = bookingsForResult.stream().filter(booking -> !booking.getStart()
                            .isAfter(timeNow))
                    .findFirst().orElse(null);

            if (lastBooking != null) {
                itemDtoResponse.setLastBooking(BookingMapper.toShortBookingDto(lastBooking));
            }

            Booking nextBooking = bookingsForResult.stream().filter(booking -> booking.getStart()
                            .isAfter(timeNow) || booking.getStart().isEqual(timeNow))
                    .reduce((first, second) -> second).orElse(null);

            if (nextBooking != null) {
                itemDtoResponse.setNextBooking(BookingMapper.toShortBookingDto(nextBooking));
            }
            return itemDtoResponse;
        }).collect(toList());
    }

    @Override
    @Transactional
    public void deleteById(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> search(String text) {
        return ItemMapper.toItemDtoList(itemRepository.search(text));
    }

    private Booking findLastBooking(long itemId, LocalDateTime time) {
        return bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(itemId, time);
    }

    private Booking findNextBooking(long itemId, LocalDateTime time, BookingStatus status) {
        return bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusIsOrderByStartAsc(
                itemId, time, status);
    }
}