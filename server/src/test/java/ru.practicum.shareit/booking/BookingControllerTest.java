package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.validation.GlobalExceptionHandler;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    private MockMvc mockMvc;

    private ItemRequest itemRequest;
    private User owner;
    private Item item;
    private User booker;

    private BookingResponseDto response1;
    private BookingResponseDto response2;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        itemRequest = new ItemRequest(1L, "Request description", booker, Instant.now());
        owner = new User(1L, "User №1", "user_1@yandex.ru");
        item = new Item(1L, "Item № 1", "description", true, owner, itemRequest);
        booker = new User(2L, "User №2", "user_2@yandex.ru");

        response1 = new BookingResponseDto(1L, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(5), Status.WAITING,
                new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        response2 = new BookingResponseDto(2L, LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(2), Status.APPROVED,
                new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));
    }

    @Test
    @DisplayName("Получение бронирования по id автора.")
    void getBookingByIdAndUser_Successful() throws Exception {
        when(bookingService.getBookingByIdAndUser(1L, 1L)).thenReturn(response1);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Получение всех бронирований владельца вещи")
    void getAllBookingsByOwner_ShouldReturnAllBookingsByItemOwner() throws Exception {
        List<BookingResponseDto> response = new ArrayList<>();
        response.add(response1);
        response.add(response2);

        when(bookingService.getAllBookingsByOwner(anyLong())).thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @DisplayName("Создание бронирования")
    void createBooking_ShouldReturnNewBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingCreateDto.class))).thenReturn(response1);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void approveBooking_ShouldReturnApprovedBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(response2);

        mockMvc.perform(patch("/bookings/{bookingId}", 2)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Возввращает бронирования определенного пользователя")
    void getAllBookingByUser_ShouldReturnAllBookingsByUser() throws Exception {
        List<BookingResponseDto> response = new ArrayList<>();
        response.add(response1);
        response.add(response2);

        when(bookingService.getAllBookingsByUser(anyLong())).thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }
}
