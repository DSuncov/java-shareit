package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Обрабатываем запрос на получение списка всех пользователей ...");
        List<UserDto> listOfAllUsers = inMemoryUserStorage.getAllUsers().values().stream()
                .map(userMapper::toDto)
                .toList();
        log.info("Запрос обработан успешно. Список пользователей отправлен клиенту.");
        return listOfAllUsers;
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Обрабатываем запрос на получение информации о пользователе с id = {} ...", userId);
        User user = inMemoryUserStorage.getUserById(userId);
        log.info("Запрос успешно обработан. Информация о пользователе с id = {} отправлена клиенту", userId);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto createUser(User user) {
        log.info("Обрабатываем запрос на добавление пользователя ...");
        User newUser = inMemoryUserStorage.createUser(user);
        log.info("Запрос успешно обработан. Новый пользователь сохранен в БД.");
        return userMapper.toDto(newUser);
    }

    @Override
    public UserDto updateUser(User user) {
        log.info("Обрабатываем запрос на обновление информации о пользователе с id = {} ...", user.getId());
        User updatedUser = inMemoryUserStorage.updateUser(user);
        log.info("Запрос успешно обработан. Информация о пользователе с id = {} обновлена.", user.getId());
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto editUser(Long userId, User user) {
        log.info("Обрабатываем запрос на обновление информации о пользователе с id = {} ...", userId);
        User editUser = inMemoryUserStorage.editUser(userId, user);
        log.info("Запрос успешно обработан. Информация о пользователе с id = {} обновлена.", userId);
        return userMapper.toDto(editUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Обрабатываем запрос на удаление пользователе с id = {} ...", userId);
        inMemoryUserStorage.deleteUser(userId);
        log.info("Запрос успешно обработан. Пользователь с id = {} удален.", userId);
    }
}
