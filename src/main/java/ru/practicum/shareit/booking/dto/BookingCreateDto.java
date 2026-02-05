package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreateDto {
    @NotNull
    @FutureOrPresent(message = "Дата и время создания бронирования не может быть в прошлом.")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата и время окончания бронирования не может быть в прошлом и настоящем.")
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}
