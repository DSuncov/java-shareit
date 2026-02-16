package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getBookingByIdAndUser(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var booking = bookingService.getBookingByIdAndUser(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        var listOfBooking = bookingService.getAllBookingsByOwner(ownerId);
        return ResponseEntity.ok(listOfBooking);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        var listOfBooking = bookingService.getAllBookingsByUser(userId);
        return ResponseEntity.ok(listOfBooking);
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestBody BookingCreateDto bookingCreateDto) {
        var newBooking = bookingService.createBooking(bookerId, bookingCreateDto);
        return ResponseEntity.ok(newBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        var approvedBooking = bookingService.approveBooking(bookingId, userId, approved);
        return ResponseEntity.ok(approvedBooking);
    }
}
