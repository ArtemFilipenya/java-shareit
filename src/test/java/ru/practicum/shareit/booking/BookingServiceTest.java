package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingAllDto;
import ru.practicum.shareit.booking.dto.BookingControllerDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingStorage;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.errors.exception.BadParameterException;
import ru.practicum.shareit.errors.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Page.empty;
import static ru.practicum.shareit.enums.States.*;
import static ru.practicum.shareit.enums.Status.*;
import static ru.practicum.shareit.user.mapper.UserMapper.convertDtoToModel;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceTest {
    @Mock
    private BookingStorage bookingStorage;
    private BookingControllerDto bookingControllerDto;
    private BookingService bookingService;
    @Mock
    private UserService userService;
    private UserDto userDto;
    private Booking booking;

    @BeforeEach
    void initialize() {
        bookingService = new BookingServiceImpl(bookingStorage, userService);
        bookingControllerDto = BookingControllerDto.builder()
                .id(1L)
                .start(now())
                .end(now().plusHours(2))
                .itemId(1L)
                .booker(2L)
                .status(WAITING.name())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@mail.com")
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(now())
                .end(now().plusHours(2))
                .item(new Item(1L, "Taz", "blue taz", true, convertDtoToModel(userDto), null))
                .booker(new User(2L, "Alexa", "alexa@mail.com"))
                .status(WAITING)
                .build();
    }

    private BookingAllDto saveBookingDto() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(bookingStorage.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyLong(), any(), any()))
                .thenReturn(of());
        when(bookingStorage.save(any()))
                .thenReturn(booking);
        return bookingService.save(
                bookingControllerDto,
                ItemMapper.convertToItemWithAllFields(
                        booking.getItem(),
                        null,
                        null,
                        of()), 2L);
    }

    @Test
    void saveBookingEmptyEndTimeTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingControllerDto.setEnd(null);
        Exception exception = assertThrows(BadParameterException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Не задана дата окончания бронирования", exception.getMessage());
    }

    @Test
    void saveBookingTest() {
        BookingAllDto bookingAllFieldsDto = saveBookingDto();
        assertEquals(bookingAllFieldsDto.getId(), booking.getId());
        assertEquals(bookingAllFieldsDto.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void saveBookingEmptyStartTimeTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingControllerDto.setStart(null);
        Exception exception = assertThrows(BadParameterException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Не задана дата начала бронирования", exception.getMessage());
    }

    @Test
    void saveBookingStartInPastTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingControllerDto.setStart(now().minusDays(2));
        Exception exception = assertThrows(BadParameterException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Некорректная дата начала броинрования", exception.getMessage());
    }

    @Test
    void saveBookingEmptyEndInPastTest() {
        lenient().when(userService.get(anyLong()))
                .thenReturn(userDto);
        bookingControllerDto.setEnd(now().minusDays(2));
        Exception exception = assertThrows(BadParameterException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("Некорректная дата бронирования", exception.getMessage());
    }

    @Test
    void saveBookingByItemOwnerTest() {
        bookingControllerDto.setBooker(booking.getItem().getOwner().getId());
        Exception exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        bookingControllerDto.getBooker())
        );
        assertEquals("ObjectNotFoundException", exception.getMessage());
    }

    @Test
    void saveBookingTakenItemTest() {
        when(userService.get(any()))
                .thenReturn(userDto);
        when(bookingStorage.findBookingsByItem_IdIsAndStatusIsAndEndIsAfter(anyLong(), any(), any()))
                .thenReturn(of(booking));
        Exception exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        1L)
        );
        assertEquals("ObjectNotFoundException", exception.getMessage());
    }

    @Test
    void saveBookingNotAvailableItemTest() {
        booking.getItem().setAvailable(false);
        Exception exception = assertThrows(BadParameterException.class,
                () -> bookingService.save(
                        bookingControllerDto,
                        ItemMapper.convertToItemWithAllFields(
                                booking.getItem(),
                                null,
                                null,
                                of()),
                        2L)
        );
        assertEquals("BadParameterException", exception.getMessage());
    }

    @Test
    void approveBookingNotItemOwnerTest() {
        when(bookingStorage.findById(anyLong()))
                .thenReturn(ofNullable(booking));
        Exception exception = assertThrows(BadParameterException.class, () -> bookingService.approve(booking.getId(), true, 3L));
        assertEquals("ObjectNotFoundException", exception.getMessage());
    }

    @Test
    void approveBookingTest() {
        Booking approved = new Booking(booking.getStatus(), booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem(), booking.getBooker());
        when(bookingStorage.findById(anyLong())).thenReturn(ofNullable(booking));
        when(bookingStorage.save(any())).thenReturn(approved);
        BookingAllDto approvedFrom = bookingService.approve(booking.getId(), true, userDto.getId());

        assertEquals(approvedFrom.getStatus().name(), approved.getStatus().name());
        assertEquals(approvedFrom.getId(), approved.getId());
    }

    @Test
    void approveBookingByBookerTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(ofNullable(booking));
        Exception exception = assertThrows(ObjectNotFoundException.class, () -> bookingService.approve(booking.getId(),
                true, booking.getBooker().getId()));
        assertEquals("ObjectNotFoundException", exception.getMessage());
    }

    @Test
    void getNotFoundBookingTest() {
        when(bookingStorage.findById(anyLong())).thenThrow(ObjectNotFoundException.class);

        assertThrows(ObjectNotFoundException.class, () -> bookingService.get(7L, 7L));
    }

    @Test
    void approveApprovedBookingTest() {
        booking.setStatus(APPROVED);
        when(bookingStorage.findById(anyLong())).thenReturn(ofNullable(booking));
        Exception exception = assertThrows(BadParameterException.class, () -> bookingService.approve(booking.getId(),
                true, userDto.getId()));

        assertEquals("ObjectNotFoundException", exception.getMessage());
    }

    @Test
    void getBookingTest() {
        BookingAllDto bookingAllFieldsDto = saveBookingDto();
        when(bookingStorage.findById(anyLong())).thenReturn(ofNullable(booking));
        BookingAllDto bookingFrom = bookingService.get(bookingAllFieldsDto.getId(), userDto.getId());

        assertEquals(bookingFrom.getItem().getId(), booking.getItem().getId());
        assertEquals(bookingFrom.getId(), booking.getId());
    }

    @Test
    void getBookingByAnotherUserTest() {
        when(bookingStorage.findById(anyLong())).thenReturn(ofNullable(booking));

        assertThrows(ObjectNotFoundException.class, () -> bookingService.get(booking.getId(), 7L));
    }

    @Test
    void getAllBookingsIncorrectEndPaginationTest() {
        Exception exception = assertThrows(BadParameterException.class, () -> bookingService.getAll(userDto.getId(),
                "Unknown", 0, 0));

        assertEquals("BadParameterException", exception.getMessage());
    }

    @Test
    void getAllBookingsTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsOrderByStartDesc(any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), null, null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsIncorrectStartPaginationTest() {
        Exception exception = assertThrows(BadParameterException.class, () -> bookingService.getAll(userDto.getId(),
                "Unknown", -1, 14));

        assertEquals("BadParameterException", exception.getMessage());
    }

    @Test
    void getAllBookingsWithNotValidStateTest() {
        saveBookingDto();
        Exception exception = assertThrows(BadParameterException.class, () -> bookingService.getAll(userDto.getId(),
                "Unknown", null, null));

        assertEquals("Unknown state: Unknown", exception.getMessage());
    }

    @Test
    void getAllBookingsFutureStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), FUTURE.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsPastStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), PAST.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsCurrentStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CURRENT.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsEmptyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), FUTURE.name(), null, null);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsRejectStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), REJECTED.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsCancelStateEmptyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CANCELED.name(), null, null);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPastStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), PAST.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsOrderByStartDesc(any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), null, null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdAllStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsOrderByStartDesc(any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), ALL.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdFutureStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), FUTURE.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdRejectStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), REJECTED.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByOwnerIdCurrentStateTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), CURRENT.name(), null, null);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getBookingsByItemEmptyTest() {
        when(bookingStorage.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyLong(), anyLong())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getBookingsByItem(1L, 2L);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByItemTest() {
        when(bookingStorage.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(anyLong(), anyLong())).thenReturn(of(booking));
        List<BookingAllDto> bookings = bookingService.getBookingsByItem(1L, 2L);

        assertEquals(bookings.get(0).getId(), booking.getId());
        assertEquals(bookings.size(), 1);
    }

    @Test
    void getAllBookingsPaginationFutureTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), FUTURE.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsPaginationAllTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsOrderByStartDesc(any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), ALL.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsPaginationPastTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), PAST.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsPaginationCurrentTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CURRENT.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsPaginationAnyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CANCELED.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPastTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), PAST.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdCurrentTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), CURRENT.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdFutureTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), FUTURE.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdAnyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), CANCELED.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPaginationNotNullTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsOrderByStartDesc(any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), ALL.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPaginationPastTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), PAST.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPaginationCurrentTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), CURRENT.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPaginationFutureTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), FUTURE.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getBookingsByOwnerIdPaginationAnyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByItemOwnerIsAndStatusIsOrderByStartDesc(any(), any(), any())).thenReturn(empty());
        List<BookingAllDto> bookings = bookingService.getBookingsByOwner(userDto.getId(), CANCELED.name(), 0, 2);

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsAllTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsOrderByStartDesc(any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), ALL.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsCurrentTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CURRENT.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsFutureTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), FUTURE.name());

        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsAnyTest() {
        saveBookingDto();
        when(bookingStorage.findBookingsByBookerIsAndStatusIsOrderByStartDesc(any(), any())).thenReturn(of());
        List<BookingAllDto> bookings = bookingService.getAll(userDto.getId(), CANCELED.name());

        assertEquals(bookings.size(), 0);
    }
}