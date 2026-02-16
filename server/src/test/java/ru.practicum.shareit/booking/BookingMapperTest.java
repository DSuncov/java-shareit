package ru.practicum.shareit.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapper();

    private final User author = new User(1L, "User № 1", "user_1@yandex.ru");
    private final User owner = new User(2L, "User № 2", "user_2@yandex.ru");

    private final Item item = new Item(1L, "Item № 1 name", "Item № 1 description", true, owner, null);

    @Test
    @DisplayName("Маппинг из Entity в DTO")
    void toDto_ShouldReturnBookingResponseDto() {
        Booking booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item, author, Status.WAITING);

        BookingResponseDto dto = bookingMapper.toDto(booking, null, null);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
    }

    @Test
    @DisplayName("Маппинг из DTO в Entity")
    void toEntity_ShouldReturnBooking() {
        BookingCreateDto dto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);

        Booking booking = bookingMapper.toEntity(dto, item, author);

        assertNotNull(dto);
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertEquals(dto.getItemId(), booking.getItem().getId());
    }
}
