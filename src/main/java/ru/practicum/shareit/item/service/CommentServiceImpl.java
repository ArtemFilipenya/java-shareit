package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDtoResponse create(long userId, long itemId, CommentDtoRequest commentDtoRequest) {
        User author = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User ID = " + userId + " not found."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item ID = " + itemId + " not found."));

        boolean isBookingExists = bookingExists(userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);

        if (!isBookingExists) {
            throw new BadRequestException("Bad request");
        }
        Comment comment = CommentMapper.convertToComment(commentDtoRequest);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        return CommentMapper.convertToCommentDtoResponse(commentRepository.save(comment));
    }

    private boolean bookingExists(long userId, long itemId, LocalDateTime time, BookingStatus status) {
        return bookingRepository.existsByBookerIdAndItemIdAndEndIsBeforeAndStatusIs(userId, itemId,
                time, status);
    }
}