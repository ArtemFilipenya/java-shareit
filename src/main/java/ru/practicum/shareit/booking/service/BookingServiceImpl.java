package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingAllDto;
import ru.practicum.shareit.booking.dto.BookingControllerDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingStorage;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.enums.States.*;
import static ru.practicum.shareit.util.PageInfo.createPageRequest;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;

    @Override
    public List<BookingAllDto> getBookingsByOwner(Long userId, String state) {
        User user = UserMapper.convertDtoToModel(userService.get(userId));
        Stream<Booking> stream;

        if (state == null || ALL.name().equals(state)) {
            stream = bookingStorage.findBookingsByItemOwnerIsOrderByStartDesc(user).stream();
        } else if (PAST.name().equals(state)) {
            stream = bookingStorage.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, now()).stream();
        } else if (CURRENT.name().equals(state)) {
            stream = bookingStorage.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, now(), now()).stream();
        } else if (FUTURE.name().equals(state)) {
            stream = bookingStorage.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user, now()).stream();
        } else if (Arrays.stream(Status.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            stream = bookingStorage.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state)).stream();
        } else {
            throw new BadParameterException("Unknown state: " + state);
        }

        return stream
                .map(BookingMapper::convertModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingAllDto approve(Long bookingId, boolean approved, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("This booking does not exist."));
        if (booking.getBooker().getId().equals(userId)) {
            throw new ObjectNotFoundException("The user with id= " + userId + " cannot approve");
        }
        if (!booking.getItem().getOwner().getId().equals(userId) || !booking.getStatus().equals(Status.WAITING)) {
            throw new BadParameterException("Booking cannot be updated");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking savedBooking = bookingStorage.save(booking);
        return BookingMapper.convertModelToDto(savedBooking);
    }


    @Override
    public List<BookingAllDto> getBookingsByOwner(Long userId, String state, Integer from, Integer size) {
        Stream<Booking> stream = null;
        PageRequest pageRequest = createPageRequest(from, size, Sort.by("start").descending());
        User user = UserMapper.convertDtoToModel(userService.get(userId));

        if (state == null || state.equals(ALL.name())) {
            if (pageRequest == null) {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsOrderByStartDesc(user)
                        .stream();
            } else {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsOrderByStartDesc(user, pageRequest)
                        .stream();
            }
        }
        if (PAST.name().equals(state)) {
            if (pageRequest == null) {
                stream = bookingStorage
                        .findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now())
                        .stream();
            } else {
                stream = bookingStorage
                        .findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user, LocalDateTime.now(), pageRequest)
                        .stream();
            }
        }
        if (CURRENT.name().equals(state)) {
            if (pageRequest == null) {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now())
                        .stream();
            } else {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now(), pageRequest)
                        .stream();
            }
        }
        if (FUTURE.name().equals(state)) {
            if (pageRequest == null) {
                stream = bookingStorage
                        .findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user, now())
                        .stream();
            } else {
                stream = bookingStorage
                        .findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user, now(), pageRequest)
                        .stream();
            }
        }
        if (Arrays.stream(Status.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            if (pageRequest == null) {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state))
                        .stream();
            } else {
                stream = bookingStorage
                        .findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state), pageRequest)
                        .stream();
            }
        }
        if (stream != null)
            return stream
                    .map(BookingMapper::convertModelToDto)
                    .collect(toList());
        else
            throw new BadParameterException("Unknown state: " + state);
    }

    @Override
    @Transactional
    public BookingAllDto save(BookingControllerDto bookingControllerDto, ItemAllDto itemDto, Long id) {
        if (itemDto.getOwnerId().equals(id))
            throw new ObjectNotFoundException("The item with id= " + id + " cannot be rented");
        if (!itemDto.getAvailable())
            throw new BadParameterException("Item with id= " + id + " already rented");
        checkToValid(bookingControllerDto);
        User booker = UserMapper.convertDtoToModel(userService.get(id));
        Item item = ItemMapper.convertDtoToModel(itemDto);
        Booking booking = BookingMapper.convertDtoToModel(bookingControllerDto);
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
        return BookingMapper.convertModelToDto(bookingStorage.save(booking));
    }

    @Override
    public List<BookingAllDto> getBookingsByItem(Long itemId, Long userId) {
        return bookingStorage.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(
                        itemId, userId)
                .stream()
                .map(BookingMapper::convertModelToDto)
                .collect(toList());
    }

    @Override
    public BookingAllDto get(Long bookingId, Long userId) {
        Booking booking = bookingStorage.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("This booking does not exist."));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("This user cannot get booking information");
        }
        return BookingMapper.convertModelToDto(booking);
    }

    @Override
    public List<BookingAllDto> getAll(Long bookerId, String state) {
        Stream<Booking> stream = null;
        User user = UserMapper.convertDtoToModel(userService.get(bookerId));
        if (state == null || ALL.name().equals(state))
            stream = bookingStorage
                    .findBookingsByBookerIsOrderByStartDesc(user)
                    .stream();
        if (PAST.name().equals(state))
            stream = bookingStorage
                    .findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, LocalDateTime.now())
                    .stream();
        if (CURRENT.name().equals(state))
            stream = bookingStorage
                    .findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now())
                    .stream();
        if (FUTURE.name().equals(state))
            stream = bookingStorage
                    .findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, LocalDateTime.now())
                    .stream();
        if (Arrays.stream(Status.values()).anyMatch(bookingState -> bookingState.name().equals(state)))
            stream = bookingStorage
                    .findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state))
                    .stream();
        if (stream != null)
            return stream
                    .map(BookingMapper::convertModelToDto)
                    .collect(toList());
        else
            throw new BadParameterException("Unknown state: " + state);
    }

    @Override
    public List<BookingAllDto> getAll(Long bookerId, String state, Integer from, Integer size) {
        Stream<Booking> stream = null;
        PageRequest pageRequest = createPageRequest(from, size, Sort.by("start").descending());
        User user = UserMapper.convertDtoToModel(userService.get(bookerId));
        if (state == null || ALL.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingStorage
                        .findBookingsByBookerIsOrderByStartDesc(user)
                        .stream();
            else
                stream = bookingStorage
                        .findBookingsByBookerIsOrderByStartDesc(user, pageRequest)
                        .stream();
        }
        if (PAST.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingStorage
                        .findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, LocalDateTime.now())
                        .stream();
            else
                stream = bookingStorage
                        .findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user, LocalDateTime.now(), pageRequest)
                        .stream();
        }
        if (CURRENT.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingStorage
                        .findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now())
                        .stream();
            else
                stream = bookingStorage
                        .findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now(), pageRequest)
                        .stream();
        }
        if (FUTURE.name().equals(state)) {
            if (pageRequest == null)
                stream = bookingStorage
                        .findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, LocalDateTime.now())
                        .stream();
            else
                stream = bookingStorage
                        .findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user, LocalDateTime.now(), pageRequest)
                        .stream();
        }
        if (Arrays.stream(Status.values()).anyMatch(bookingState -> bookingState.name().equals(state))) {
            if (pageRequest == null)
                stream = bookingStorage
                        .findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state))
                        .stream();
            else
                stream = bookingStorage
                        .findBookingsByBookerIsAndStatusIsOrderByStartDesc(user, Status.valueOf(state), pageRequest)
                        .stream();
        }
        if (stream != null)
            return stream
                    .map(BookingMapper::convertModelToDto)
                    .collect(toList());
        else
            throw new BadParameterException("Unknown state: " + state);
    }

    private void checkToValid(BookingControllerDto bookingSavingDto) {
        if (bookingSavingDto.getStart() == null)
            throw new BadParameterException("Не задана дата начала бронирования");
        if (bookingSavingDto.getEnd() == null)
            throw new BadParameterException("Не задана дата окончания бронирования");
        if (bookingSavingDto.getStart().equals(bookingSavingDto.getEnd()))
            throw new BadParameterException("Не задана дата начала бронирования");
        if (bookingSavingDto.getStart().toLocalDate().isBefore(LocalDate.now()))
            throw new BadParameterException("Некорректная дата начала броинрования");
        if (bookingSavingDto.getEnd().isBefore(bookingSavingDto.getStart())
                || bookingSavingDto.getEnd().toLocalDate().isBefore(LocalDate.now()))
            throw new BadParameterException("Некорректная дата бронирования");
    }
}