package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static ru.practicum.shareit.item.CommentMapper.convertToComment;
import static ru.practicum.shareit.item.CommentMapper.convertToCommentDto;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto create(long userId, long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User ID = " + userId + " not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item ID = " + itemId + " not found."));
        Booking booking = findBooking(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (booking == null) {
            throw new BadRequestException("Bad request");
        }
        Comment comment = convertToComment(commentDto);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        return convertToCommentDto(commentRepository.save(comment));
    }

    private Booking findBooking(long userId, long itemId, LocalDateTime time, BookingStatus status) {
        return bookingRepository.findFirstByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(userId, itemId,
                time, status);
    }
}