package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {

    @Mock
    private ItemRequestServiceImpl itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Mock
    private UserServiceImpl userService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Успешное создание запроса.")
    void createRequest_Successful() throws Exception {
        ItemRequestCreateDto create = new ItemRequestCreateDto("request_description");
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                1L, "request_description", Instant.now(), null);

        when(itemRequestService.createRequest(create, 1L)).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("request_description"));
    }

    @Test
    @DisplayName("Получение запроса по id.")
    void getRequestById_Successful() throws Exception {
        ItemRequestCreateDto create = new ItemRequestCreateDto("request_description");
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                1L, "request_description", Instant.now(), null);

        when(itemRequestService.getRequestsById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("request_description"));
    }

    @Test
    @DisplayName("Попытка получить несуществующий запрос по id.")
    void getRequestById_UnSuccessfulByRequestId() throws Exception {
        given(itemRequestService.getRequestsById(1L, 1L)).willThrow(new NotFoundException("Запрос с id = " + 1L + " отсутствует."));

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Получение запросов по автору.")
    void getAllRequestsByAuthor_Successful() throws Exception {
        ItemRequestCreateDto create = new ItemRequestCreateDto("request_description");
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                1L, "request_description", Instant.now(), null);

        itemRequestService.createRequest(create, 1L);

        given(itemRequestService.getAllRequestsByAuthor(1L)).willReturn(List.of(response));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("request_description"));
    }

    @Test
    @DisplayName("Получение запросов по несуществующему автору.")
    void getAllRequestsByAuthor_UnSuccessful() throws Exception {
        ItemRequestCreateDto create = new ItemRequestCreateDto("request_description");
        ItemRequestResponseDto response = new ItemRequestResponseDto(
                1L, "request_description", Instant.now(), null);

        itemRequestService.createRequest(create, 1L);

        given(itemRequestService.getAllRequestsByAuthor(2L)).willThrow(new NotFoundException("Пользователь с id = " + 2L + " не существует."));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
