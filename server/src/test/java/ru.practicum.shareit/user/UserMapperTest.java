package ru.practicum.shareit.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    @DisplayName("Маппинг User в UserResponseDto")
    void toDto_ShouldMapToDto() {
        User user = new User(1L, "User № 1", "user_1@yandex.ru");

        UserResponseDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    @DisplayName("Маппинг из UserCreateDto в User")
    void toEntity_ShouldReturnEntity() {
        UserCreateDto dto = new UserCreateDto("User № 1", "user_1@yandex.ru");

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getEmail(), user.getEmail());
    }
}
