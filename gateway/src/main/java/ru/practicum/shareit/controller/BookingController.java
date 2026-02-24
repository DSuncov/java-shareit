package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.dto.booking.BookingCreateDto;
import ru.practicum.shareit.client.BookingClient;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByIdAndUser(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id бронирования должно быть задано") Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        return bookingClient.getBookingByIdAndUser(bookingId, userId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long ownerId) {
        return bookingClient.getAllBookingsByOwner(ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        return bookingClient.getAllBookingsByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long bookerId,
            @Valid @NotNull(message = "В качестве booking передан null.") @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingClient.createBooking(bookerId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id бронирования должно быть задано") Long bookingId,
            @RequestParam Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}
