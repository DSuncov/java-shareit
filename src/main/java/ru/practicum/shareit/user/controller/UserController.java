package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        var listOfAllUsers = userService.getAllUsers();
        return ResponseEntity.ok(listOfAllUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable("id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto newUser = userService.createUser(userCreateDto);
        return ResponseEntity.ok(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> editUser(
            @PathVariable @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId,
            @NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody UserUpdateDto userUpdateDto) {
        var editUser = userService.editUser(userId, userUpdateDto);
        return ResponseEntity.ok(editUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable("id") @Positive(message = "id должно быть положительным числом") @NotNull(message = "id пользователя должно быть задано") Long userId) {
        userService.deleteUser(userId);
    }
}
