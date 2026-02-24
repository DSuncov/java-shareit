package ru.practicum.shareit.dto.user;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    String name;
    @Email(message = "Указан некорректный E-mail.")
    String email;
}
