package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toShortBookingDto;
import static ru.practicum.shareit.item.ItemMapper.*;

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
        Item item = fromItemDtoRequest(itemDtoRequest, owner);
        item.setOwner(owner);
        Item result = itemRepository.save(item);
        return toItemDtoResponse(result);
    }

    @Override
    @Transactional
    public ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item ID = " + itemId + " not found."));
        if (item.getOwner().getId() != userId) {
            throw new AccessException("User ID = " + userId + " is not owner.");
        }
        if (itemDtoRequest.getDescription() != null && !itemDtoRequest.getDescription().isBlank()) {
            item.setDescription(itemDtoRequest.getDescription());
        }
        if (itemDtoRequest.getName() != null && !itemDtoRequest.getName().isBlank()) {
            item.setName(itemDtoRequest.getName());
        }
        if (itemDtoRequest.getAvailable() != null) {
            item.setAvailable(itemDtoRequest.getAvailable());
        }
        return toItemDtoResponse(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoResponse getById(long userId, long itemId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User ID = " + userId + " not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item ID = " + itemId + " not found."));
        ItemDtoResponse result = toItemDtoResponse(item);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
        result.setComments(comments);

        if (userId == item.getOwner().getId()) {
            LocalDateTime now = LocalDateTime.now();
            Booking lastBooking = bookingRepository.findFirstByItemIdAndStartLessThanEqualOrderByStartDesc(itemId, now);
            Booking nextBooking = bookingRepository.findFirstByItemIdAndStartGreaterThanEqualAndStatusIsOrderByStartAsc(
                    itemId, now, BookingStatus.APPROVED);
            result.setLastBooking(lastBooking == null ? null : toShortBookingDto(lastBooking));
            result.setNextBooking(nextBooking == null ? null : toShortBookingDto(nextBooking));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> getAll(long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        Map<Item, List<Comment>> comments = commentRepository
                .findAllByItemIn(items, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        Map<Item, List<Booking>> bookings = bookingRepository
                .findAllByItemInAndStatus(items, BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));
        return items.stream().map(item -> {
                    ItemDtoResponse itemDtoResponse = toItemDtoResponse(item);
                    List<CommentDto> commentsForResult = comments.getOrDefault(item, List.of()).stream()
                            .map(CommentMapper::toCommentDto).collect(toList());
                    List<Booking> bookingsForResult = bookings.getOrDefault(item, List.of());
                    Booking lastBooking = bookingsForResult.stream()
                            .filter(booking -> !booking.getStart().isAfter(now))
                            .findFirst().orElse(null);
                    Booking nextBooking = bookingsForResult.stream()
                            .filter(booking -> booking.getStart().isAfter(now)
                                    || booking.getStart().isEqual(now))
                            .reduce((first, second) -> second)
                            .orElse(null);
                    itemDtoResponse.setComments(commentsForResult);
                    itemDtoResponse.setLastBooking(lastBooking == null ? null : toShortBookingDto(lastBooking));
                    itemDtoResponse.setNextBooking(nextBooking == null ? null : toShortBookingDto(nextBooking));
                    return itemDtoResponse;
                })
                .collect(toList());
    }

    @Override
    @Transactional
    public void deleteById(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoResponse> search(String text) {
        return toItemDtoList(itemRepository.search(text.toLowerCase()));
    }
}