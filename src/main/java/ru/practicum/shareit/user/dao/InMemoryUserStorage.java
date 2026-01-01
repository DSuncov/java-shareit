package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface InMemoryUserStorage {

    Map<Long, User> getAllUsers();

    User getUserById(Long userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    User editUser(Long userId, User user);
}
