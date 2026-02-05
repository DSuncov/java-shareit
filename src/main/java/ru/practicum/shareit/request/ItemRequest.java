package ru.practicum.shareit.request;

/**
 * TODO Sprint add-item-requests.
 */

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {

    private Long id;

    @NotBlank(message = "Описание вещи должно быть указано.")
    private String description;

    @NotNull(message = "Пользователь, создавший запрос, должен быть указан.")
    private User requestor;

    @FutureOrPresent(message = "Дата создания запроса не может быть в прошлом.")
    private LocalDateTime created;
}
