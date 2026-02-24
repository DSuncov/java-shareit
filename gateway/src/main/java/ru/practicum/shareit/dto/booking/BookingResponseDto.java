package ru.practicum.shareit.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.dto.item.ItemResponseDtoForBooking;
import ru.practicum.shareit.dto.user.UserResponseDtoForBooking;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private UserResponseDtoForBooking booker;
    private ItemResponseDtoForBooking item;
}
