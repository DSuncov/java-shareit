package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(User user);

    UserDto updateUser(User user);

    UserDto editUser(Long userId, User user);

    void deleteUser(Long userId);
}
