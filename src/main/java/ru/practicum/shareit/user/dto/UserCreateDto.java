package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateDto(
        @NotBlank(message = "Имя пользователя должно быть указано.")
        String name,
        @NotBlank(message = "E-mail не может быть пустым")
        @Email(message = "Указан некорректный E-mail.")
        String email
) {
}
