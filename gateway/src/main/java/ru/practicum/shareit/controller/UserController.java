package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.user.UserCreateDto;
import ru.practicum.shareit.dto.user.UserUpdateDto;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(
            @PathVariable("id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        return userClient.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody UserCreateDto userCreateDto) {
        return userClient.createUser(userCreateDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> editUser(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userClient.editUser(userId, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable("id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        return userClient.deleteUser(userId);
    }
}
