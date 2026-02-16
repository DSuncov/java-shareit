package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.validation.GlobalExceptionHandler;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForComment;
import ru.practicum.shareit.item.dto.update.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    @Mock
    private UserServiceImpl userService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userService.createUser(new UserCreateDto("User №1", "user_1@yandex.ru"));

        ItemCreateDto create1 = new ItemCreateDto();
        create1.setName("Item №1");
        create1.setDescription("Item 1 description");
        create1.setAvailable(true);

        ItemCreateDto create2 = new ItemCreateDto();
        create2.setName("Item №2");
        create2.setDescription("Item 2 description");
        create2.setAvailable(true);

        itemService.createItem(create1, 1L);
        itemService.createItem(create2, 1L);
    }

    @Test
    @DisplayName("Получение вещей по id автора.")
    void getItemsByOwner_Successful() throws Exception {
        List<ItemResponseDto> response = List.of(new ItemResponseDto(1L, "Item №1", "Item 1 description", true),
                new ItemResponseDto(2L, "Item №2", "Item 2 description", true));

        when(itemService.getAllItemsByOwner(1L)).thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("Item 1 description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].description").value("Item 2 description"))
                .andExpect(jsonPath("$[1].available").value(true));

    }

    @Test
    @DisplayName("Получение вещей по id несуществующего автора.")
    void getItemsByNotExistOwner() throws Exception {
        List<ItemResponseDto> response = List.of(new ItemResponseDto(1L, "Item №1", "Item 1 description", true),
                new ItemResponseDto(2L, "Item №2", "Item 2 description", true));

        given(itemService.getAllItemsByOwner(2L)).willThrow(new NotFoundException("Пользователь с id = " + 2L + " не существует."));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение вещи по id.")
    void getItemById_Successful() throws Exception {
        ItemResponseDtoForComment response =
                new ItemResponseDtoForComment(1L, "Item №1", "Item 1 description", true, null, null, null);

        when(itemService.getItemById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item №1"))
                .andExpect(jsonPath("$.description").value("Item 1 description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Получение несуществующей вещи.")
    void getItemByIdItemNotExist() throws Exception {
        given(itemService.getItemById(3L, 1L)).willThrow(new NotFoundException("Вещи с id = " + 3L + " не существует."));

        mockMvc.perform(get("/items/{itemId}", 3L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Обновляет название вещи.")
    void editUser_PatchName_ShouldSuccessful() throws Exception {
        ItemUpdateDto update = new ItemUpdateDto();
        update.setName("new_ItemName");

        ItemResponseDto response = new ItemResponseDto(1L, "new_ItemName", "Item 1 description", true);

        when(itemService.editItem(1L, update, 1L)).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("new_ItemName"))
                .andExpect(jsonPath("$.description").value("Item 1 description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    @DisplayName("Обновляет описание вещи.")
    void editUser_PatchDescription_ShouldSuccessful() throws Exception {
        ItemUpdateDto update = new ItemUpdateDto();
        update.setDescription("new Item 1 description");

        ItemResponseDto response = new ItemResponseDto(1L, "Item №1", "new Item 1 description", true);

        when(itemService.editItem(1L, update, 1L)).thenReturn(response);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item №1"))
                .andExpect(jsonPath("$.description").value("new Item 1 description"))
                .andExpect(jsonPath("$.available").value(true));
    }
}
