package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.validation_markers.Create;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String DEFAULT_STATE = "ALL";
    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDtoResponse findById(@PathVariable long bookingId,
                                       @RequestHeader(HEADER) long userId) {
        log.info("BookingController.findById() id={}", bookingId);
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> findByOwner(@RequestParam(defaultValue = DEFAULT_STATE) String state,
                                                @RequestHeader(HEADER) long userId) {
        log.info("BookingController.findByOwner() userId ={}", userId);
        return bookingService.findByOwner(userId, state);
    }

    @GetMapping
    public List<BookingDtoResponse> findByBooker(@RequestParam(defaultValue = DEFAULT_STATE) String state,
                                                 @RequestHeader(HEADER) long userId) {
        log.info("BookingController.findByBooker() userId={}", userId);
        return bookingService.findByBooker(userId, state);
    }

    @PostMapping()
    public BookingDtoResponse create(@Validated(Create.class) @RequestBody BookingDtoRequest bookingDtoRequest,
                                     @RequestHeader(HEADER) long userId) {
        log.info("BookingController.create() id={}", bookingDtoRequest.getItemId());
        return bookingService.create(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approve(@PathVariable long bookingId,
                                      @RequestParam boolean approved,
                                      @RequestHeader(HEADER) long userId) {
        log.info("BookingController.approve() bookingId={}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }
}