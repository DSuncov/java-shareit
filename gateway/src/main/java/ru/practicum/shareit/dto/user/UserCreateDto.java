package ru.practicum.shareit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Имя пользователя должно быть указано.")
    String name;
    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Указан некорректный E-mail.")
    String email;
}
