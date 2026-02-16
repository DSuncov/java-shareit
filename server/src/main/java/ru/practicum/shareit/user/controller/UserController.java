package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        var listOfAllUsers = userService.getAllUsers();
        return ResponseEntity.ok(listOfAllUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable("id") Long userId) {
        var user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @RequestBody UserCreateDto userCreateDto) {
        UserResponseDto newUser = userService.createUser(userCreateDto);
        return ResponseEntity.ok(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> editUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto userUpdateDto) {
        var editUser = userService.editUser(userId, userUpdateDto);
        return ResponseEntity.ok(editUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable("id") Long userId) {
        userService.deleteUser(userId);
    }
}
