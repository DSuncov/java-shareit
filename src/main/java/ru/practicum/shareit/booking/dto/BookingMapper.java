package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemResponseDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;
import ru.practicum.shareit.user.model.User;

@Component
public class BookingMapper {
    public BookingResponseDto toDto(Booking booking, UserResponseDtoForBooking user, ItemResponseDtoForBooking item) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(user);
        dto.setItem(item);
        return dto;
    }

    public Booking toEntity(BookingCreateDto bookingCreateDto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(bookingCreateDto.getStart());
        booking.setEnd(bookingCreateDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }
}
