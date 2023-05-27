package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAllDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentStorage;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.enums.States.PAST;
import static ru.practicum.shareit.util.PageInfo.createPageRequest;


@Slf4j
@Service
@Transactional
public class ItemServiceImpl implements ItemService {
    private final BookingService bookingService;
    private final ItemStorage itemStorage;
    private final CommentStorage commentStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, CommentStorage commentStorage,
                           BookingService bookingService) {
        this.commentStorage = commentStorage;
        this.bookingService = bookingService;
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long id, Long userId) {
        if (userId == null) {
            throw new BadParameterException("User id not set");
        }
        Item item = itemStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("User with id= " + userId + " is not the owner");
        }
        String patchName = itemDto.getName();
        if (patchName != null && !patchName.isEmpty()) {
            item.setName(patchName);
        }
        String patchDescription = itemDto.getDescription();
        if (patchDescription != null && !patchDescription.isEmpty()) {
            item.setDescription(patchDescription);
        }
        Boolean patchAvailable = itemDto.getAvailable();
        if (patchAvailable != null) {
            item.setAvailable(patchAvailable);
        }

        return ItemMapper.convertModelToDto(itemStorage.save(item));
    }

    @Override
    public ItemAllDto get(Long id, Long userId) {
        Item item = itemStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("Item with id= " + id + " not found"));
        List<Comment> comments = commentStorage.findByItem(item, Sort.by(DESC, "created"));
        List<BookingAllDto> bookings = bookingService.getBookingsByItem(item.getId(), userId);
        BookingAllDto lastBooking = getLastItem(bookings);
        BookingAllDto nextBooking = getNextItem(bookings);
        List<CommentDto> commentDtos = comments
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        return ItemMapper.convertToItemWithAllFields(item, lastBooking, nextBooking, commentDtos);
    }

    @Override
    @Transactional
    public ItemDto save(ItemDto itemDto, ItemRequestDto itemRequestDto, Long userId) {
        checkToValid(itemDto);
        User owner = UserMapper.convertDtoToModel(userService.get(userId));
        Item item = ItemMapper.convertDtoToModel(itemDto);
        item.setOwner(owner);

        if (itemRequestDto != null) {
            UserDto requester = userService.get(itemRequestDto.getRequesterId());
            ItemRequest itemRequest = ItemRequestMapper.convertDtoToModel(itemRequestDto, requester);
            item.setRequest(itemRequest);
        }
        return ItemMapper.convertModelToDto(itemStorage.save(item));
    }

    @Override
    public List<ItemAllDto> getAll(Long id, Integer from, Integer size) {
        List<Item> items;
        User owner = UserMapper.convertDtoToModel(userService.get(id));

        if (owner != null) {
            PageRequest pageRequest = createPageRequest(from, size, Sort.by("id").ascending());
            if (pageRequest == null) {
                items = itemStorage.findAllByOwner_IdIs(id);
            } else {
                items = itemStorage.findAllByOwner_IdIs(id, pageRequest);
            }
            List<Comment> comments = commentStorage.findByItemIn(items, Sort.by(DESC, "created"));
            Map<Long, List<BookingAllDto>> bookings = bookingService.getBookingsByOwner(id, null).stream()
                    .collect(groupingBy((BookingAllDto bookingExtendedDto) -> bookingExtendedDto.getItem().getId()));

            return items.stream()
                    .map(item -> getItemAllFieldsDto(comments, bookings, item))
                    .sorted(Comparator.comparingLong(ItemAllDto::getId))
                    .collect(toList());
        } else {
            throw new ObjectNotFoundException("User with id= " + id + " not found");
        }
    }

    @Override
    public List<ItemDto> getByText(String text, Long userId, Integer from, Integer size) {
        List<Item> items;

        if (text.isBlank()) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = createPageRequest(from, size, Sort.by("id").ascending());
        if (pageRequest == null) {
            items = itemStorage.getAllText(text);
        } else {
            items = itemStorage.getAllText(text, pageRequest);
        }
        return items.stream().map(ItemMapper::convertModelToDto).collect(toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadParameterException("Text of the comment cannot be empty");
        }
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item with id= " + itemId + "not found"));
        User user = UserMapper.convertDtoToModel(userService.get(userId));
        List<BookingAllDto> bookings = bookingService.getAll(userId, PAST.name());
        if (bookings.isEmpty()) {
            throw new BadParameterException("Can't leave a comment");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentStorage.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Long requestId) {
        return itemStorage.findAllByRequest_IdIs(requestId)
                .stream()
                .map(ItemMapper::convertModelToDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<ItemRequest> requests) {
        return itemStorage.findAllByRequestIn(requests)
                .stream()
                .map(ItemMapper::convertModelToDto)
                .collect(toList());
    }

    @Override
    public List<CommentDto> getAllComments() {
        return commentStorage.findAll()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private ItemAllDto getItemAllFieldsDto(List<Comment> comments, Map<Long, List<BookingAllDto>> bookings, Item item) {
        return ItemMapper.convertToItemWithAllFields(item, getLastItem(bookings.get(item.getId())),
                getNextItem(bookings.get(item.getId())),
                comments.stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(toList()));
    }

    private BookingAllDto getLastItem(List<BookingAllDto> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingAllDto::getEnd)).orElse(null);
    }

    private BookingAllDto getNextItem(List<BookingAllDto> bookings) {
        if (bookings == null) {
            return null;
        }
        return bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()) &&
                        !booking.getStatus().equals(Status.REJECTED))
                .min(Comparator.comparing(BookingAllDto::getEnd))
                .orElse(null);

    }

    private void checkToValid(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new BadParameterException("Tool availability not defined");
        } else if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new BadParameterException("Item name cannot be empty");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new BadParameterException("Item description cannot be empty");
        }
    }
}