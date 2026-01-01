package ru.practicum.shareit.booking;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    private Long id;

    @NotNull
    @FutureOrPresent(message = "Дата и время создания бронирования не может быть в прошлом.")
    private LocalDateTime start;

    @NotNull
    @Future(message = "Дата и время окончания бронирования не может быть в прошлом и настоящем.")
    private LocalDateTime end;

    @NotNull(message = "Вещь, которая бронируется должна быть указана.")
    private Item item;

    @NotNull(message = "Пользователь, который бронирует вещь, должен быть указан.")
    private User booker;

    @NotNull(message = "Статус бронирования должен быть указан.")
    private Status status;
}
