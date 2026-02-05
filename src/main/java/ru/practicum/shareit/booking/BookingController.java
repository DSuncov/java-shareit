package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingByIdAndUser(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id бронирования должно быть задано") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var booking = bookingService.getBookingByIdAndUser(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long ownerId) {
        var listOfBooking = bookingService.getAllBookingsByOwner(ownerId);
        return ResponseEntity.ok(listOfBooking);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var listOfBooking = bookingService.getAllBookingsByUser(userId);
        return ResponseEntity.ok(listOfBooking);
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long bookerId,
            @Valid @NotNull(message = "В качестве booking передан null.") @RequestBody BookingCreateDto bookingCreateDto) {
        var newBooking = bookingService.createBooking(bookerId, bookingCreateDto);
        return ResponseEntity.ok(newBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id бронирования должно быть задано") Long bookingId,
            @RequestParam("approved") Boolean status) {
        var approvedBooking = bookingService.approveBooking(bookingId, userId, status);
        return ResponseEntity.ok(approvedBooking);
    }
}
