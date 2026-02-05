package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    @NotBlank(message = "Имя пользователя должно быть указано.")
    private String name;

    @NotBlank(message = "E-mail не может быть пустым")
    @Email(message = "Указан некорректный E-mail.")
    private String email;
}
