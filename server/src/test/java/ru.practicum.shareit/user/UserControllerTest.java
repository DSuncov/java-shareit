package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.validation.GlobalExceptionHandler;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    void createUser_Successful() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "User №1", "user_1@yandex.ru");

        when(userService.createUser(any(UserCreateDto.class))).thenReturn(response);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User №1"))
                .andExpect(jsonPath("$.email").value("user_1@yandex.ru"));

        verify(userService, times(1)).createUser(any(UserCreateDto.class));
    }

    @Test
    @DisplayName("Возвращает список всех пользователей.")
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        UserResponseDto response1 = new UserResponseDto(1L, "User №1", "user_1@yandex.ru");
        UserResponseDto response2 = new UserResponseDto(2L, "User №2", "user_2@yandex.ru");
        when(userService.getAllUsers()).thenReturn(List.of(response1, response2));

        List<UserResponseDto> usersTest = userService.getAllUsers();
        assertEquals(List.of(response1, response2), usersTest);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User №1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user_2@yandex.ru"));

        verify(userService, times(2)).getAllUsers();
    }

    @Test
    @DisplayName("Возвращает пользователя по его id.")
    void getUserById_shouldReturnUserById() throws Exception {
        UserResponseDto response = new UserResponseDto(1L, "User №1", "user_1@yandex.ru");
        given(userService.getUserById(1L)).willReturn(response);

        mockMvc.perform(get("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("User №1"))
                .andExpect(jsonPath("$.email").value("user_1@yandex.ru"));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @DisplayName("Выбрасывает исключение, если пользователя не существует.")
    void getUserById_shouldThrowException() throws Exception {
        given(userService.getUserById(anyLong())).willThrow(new NotFoundException("Пользователя с id = " + anyLong() + " не существует."));

        mockMvc.perform(get("/users/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @DisplayName("Ошибка создания пользователей с одинаковыми email.")
    void createUser_shouldThrowException() throws Exception {
        UserCreateDto create = new UserCreateDto("User №2", "user_1@yandex.ru");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setName("User №2");
        responseDto.setEmail("user_1@yandex.ru");

        when(userService.createUser(create)).thenThrow(new DuplicatedDataException("Email уже занят."));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Удаляет существующего пользователя.")
    void deleteUser_ShouldSuccessful() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    @DisplayName("Обновляет имя пользователя.")
    void editUser_ShouldSuccessful() throws Exception {
        UserUpdateDto update = new UserUpdateDto();
        update.setName("new_Name");

        UserResponseDto response = new UserResponseDto(1L, "new_Name", "user_1@yandex.ru");
        when(userService.editUser(1L, update)).thenReturn(response);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("new_Name"))
                .andExpect(jsonPath("$.email").value("user_1@yandex.ru"));

        verify(userService, times(1)).editUser(anyLong(), any(UserUpdateDto.class));
    }
}
