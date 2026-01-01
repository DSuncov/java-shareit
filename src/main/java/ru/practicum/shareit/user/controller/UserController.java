package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
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
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var listOfAllUsers = userService.getAllUsers();
        return ResponseEntity.ok(listOfAllUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable("id")
            @Positive(message = "id должно быть положительным числом")
            @NotNull(message = "id пользователя должно быть задано") Long userId) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody User user) {
        var newUser = userService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@NotNull(message = "В качестве User в запросе передан null.") @Valid @RequestBody User user) {
        var updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> editUser(
            @PathVariable
            @Positive(message = "id должно быть положительным числом")
            @NotNull(message = "id пользователя должно быть задано") Long userId,
            @NotNull @RequestBody User user) {
        var editUser = userService.editUser(userId, user);
        return ResponseEntity.ok(editUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable("id")
            @Positive(message = "id должно быть положительным числом")
            @NotNull(message = "id пользователя должно быть задано") Long userId) {
        userService.deleteUser(userId);
    }
}
