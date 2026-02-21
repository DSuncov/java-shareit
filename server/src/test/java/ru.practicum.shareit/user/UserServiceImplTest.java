package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.mappers.UserMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private User user;
    private User updatedUserName;
    private User updatedUserEmail;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("User №1");
        userCreateDto.setEmail("user_1@yandex.ru");

        user = new User();
        user.setId(1L);
        user.setName("User №1");
        user.setEmail("user_1@yandex.ru");

        updatedUserName = new User();
        updatedUserName.setId(1L);
        updatedUserName.setName("New_Name");
        updatedUserName.setEmail("user_1@yandex.ru");

        updatedUserEmail = new User();
        updatedUserEmail.setId(1L);
        updatedUserEmail.setName("User №1");
        updatedUserEmail.setEmail("user_1@gmail.com");
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserById_ShouldReturnExistingUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserById(1L);
        verify(userRepository).findById(anyLong());

        assertEquals(userMapper.toDto(user), result);
    }

    @Test
    @DisplayName("Получение пользователя по id")
    void getUserById_ShouldReturnNotExistingUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserResponseDto> result = userService.getAllUsers();
        verify(userRepository).findAll();

        assertNotNull(result);
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser_ShouldReturnNewUser() {
        when(userMapper.toEntity(userCreateDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.createUser(userCreateDto);
        verify(userMapper).toEntity(userCreateDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);

        assertEquals(userMapper.toDto(user), result);
    }

    @Test
    @DisplayName("Попытка создания пользователя с одинаковым email")
    void createUser_ShouldThrowException() {
        when(userRepository.findByIdEmail(userCreateDto.getEmail())).thenReturn(Optional.of(user));
        assertThrows(DuplicatedDataException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    @DisplayName("Успешное обновление имени пользователя")
    void editUser_ShouldReturnUserWithNewName() {
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("New_Name");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("New_Name");
        userResponseDto.setEmail("user_1@yandex.ru");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUserName)).thenReturn(updatedUserName);
        when(userMapper.toDto(updatedUserName)).thenReturn(userResponseDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(updatedUserName));

        UserResponseDto result = userService.editUser(1L, userUpdateDto);
        verify(userRepository).save(updatedUserName);
        verify(userRepository, times(2)).findById(anyLong());
        verify(userMapper).toDto(updatedUserName);

        assertEquals("New_Name", result.getName());
    }

    @Test
    @DisplayName("Успешное обновление почты пользователя")
    void editUser_ShouldReturnUserWithNewEmail() {
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setEmail("user_1@gmail.com");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("User №1");
        userResponseDto.setEmail("user_1@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(updatedUserEmail)).thenReturn(updatedUserEmail);
        when(userMapper.toDto(updatedUserEmail)).thenReturn(userResponseDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(updatedUserEmail));

        UserResponseDto result = userService.editUser(anyLong(), userUpdateDto);
        verify(userRepository).save(updatedUserEmail);
        verify(userRepository, times(2)).findById(anyLong());
        verify(userMapper).toDto(updatedUserEmail);

        assertEquals("user_1@gmail.com", result.getEmail());
    }

    @Test
    @DisplayName("Попытка обновить информацию несуществующего пользователя")
    void editUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.editUser(1L, userUpdateDto));
    }

    @Test
    void deleteUser_ShouldDeleteExistingUser() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
