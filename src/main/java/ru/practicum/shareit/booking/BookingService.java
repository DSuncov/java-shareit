package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto getBookingByIdAndUser(Long bookingId, Long bookerId);

    BookingResponseDto createBooking(Long bookerId, BookingCreateDto bookingCreateDto);

    BookingResponseDto approveBooking(Long bookingId, Long userId, Boolean bookingStatus);

    List<BookingResponseDto> getAllBookingsByOwnerAndState(Long userId, String state);

    List<BookingResponseDto> getAllBookingsByUser(Long userId);

    List<BookingResponseDto> getAllBookingsByOwner(Long ownerId);
}
