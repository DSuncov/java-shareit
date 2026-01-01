package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InMemoryUserStorageImpl implements InMemoryUserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Map<Long, User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(Long userId) {
        userExistInStorage(userId);
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        emailExistInStorage(user);
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        userExistInStorage(user.getId());
        emailExistInStorage(user);
        return User.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    @Override
    public void deleteUser(Long userId) {
        userExistInStorage(userId);
        users.remove(userId);
    }

    @Override
    public User editUser(Long userId, User user) {
        userExistInStorage(userId);
        emailExistInStorage(user);
        User editUser = users.get(userId);

        if (editUser.getName() != null) {
            editUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            editUser.setEmail(user.getEmail());
        }

        return editUser;
    }

    private Long generateUserId() {
        Long currentMaxId = users.keySet().stream()
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }

    private void userExistInStorage(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        }
    }

    private void emailExistInStorage(User user) {
        Optional<String> findEmail = users.values()
                .stream()
                .map(User::getEmail)
                .filter(u -> u.equals(user.getEmail()))
                .findFirst();
        if (findEmail.isPresent()) {
            throw new DuplicatedDataException("E-mail = " + user.getEmail() + " используется другим пользователем.");
        }
    }
}
