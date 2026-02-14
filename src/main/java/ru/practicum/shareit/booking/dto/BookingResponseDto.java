package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemResponseDtoForBooking;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;

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
