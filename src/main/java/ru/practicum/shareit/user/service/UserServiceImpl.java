package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        log.info("Обрабатываем запрос на получение списка всех пользователей ...");
        List<UserResponseDto> listOfAllUsers = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        log.info("Запрос обработан успешно. Список пользователей отправлен клиенту.");
        return listOfAllUsers;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        log.info("Обрабатываем запрос на получение информации о пользователе с id = {} ...", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует."));
        log.info("Запрос успешно обработан. Информация о пользователе с id = {} отправлена клиенту", userId);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserCreateDto user) {
        log.info("Обрабатываем запрос на добавление пользователя ...");
        checkOnsSameEmail(user.email());
        User newUser = userRepository.save(userMapper.toEntity(user));
        log.info("Запрос успешно обработан. Новый пользователь сохранен в БД.");
        return userMapper.toDto(newUser);
    }

    @Override
    @Transactional
    public UserResponseDto editUser(Long userId, UserUpdateDto user) {
        log.info("Обрабатываем запрос на частичное обновление информации о пользователе с id = {} ...", userId);
        Optional<User> editUser = userRepository.findById(userId);
        editUser.ifPresentOrElse(u -> {
            if (user.getName() != null) {
                u.setName(user.getName());
            }

            if (user.getEmail() != null) {
                checkOnsSameEmail(user.getEmail());
                u.setEmail(user.getEmail());
            }
        }, () -> {
            throw new NotFoundException("Пользователь с id = " + userId + " не существует.");
        });
        log.info("Запрос успешно обработан. Информация о пользователе с id = {} обновлена частично.", userId);
        return userMapper.toDto(editUser.get());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Обрабатываем запрос на удаление пользователе с id = {} ...", userId);
        userRepository.deleteById(userId);
        log.info("Запрос успешно обработан. Пользователь с id = {} удален.", userId);
    }

    private void checkOnsSameEmail(String email) {
        Optional<UserResponseDto> userWithSameEmail = userRepository.findByIdEmail(email);
        if (userWithSameEmail.isPresent()) {
            throw new ValidationException("Email уже занят.");
        }
    }
}
