package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private ItemServiceImpl itemService;

    //private ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userService.createUser(new UserCreateDto("User Owner", "user_owner@yandex.ru"));
        userService.createUser(new UserCreateDto("User Booker", "user_booker@yandex.ru"));

        ItemCreateDto create = new ItemCreateDto();
        create.setName("Item №1");
        create.setDescription("Item 1 description");
        create.setAvailable(true);

        itemService.createItem(create, 1L);

        bookingService.createBooking(2L, new BookingCreateDto(
                LocalDateTime.of(2026, 2, 20, 12, 0),
                LocalDateTime.of(2026, 2, 21, 12, 0),
                1L));

        bookingService.createBooking(2L, new BookingCreateDto(
                LocalDateTime.of(2026, 2, 25, 12, 0),
                LocalDateTime.of(2026, 2, 26, 12, 0),
                1L));
    }

    @Test
    @DisplayName("Получение бронирования по id автора.")
    void getBookingByIdAndUser_Successful() throws Exception {
        BookingResponseDto response = new BookingResponseDto(1L,
                LocalDateTime.of(2026, 2, 20, 12, 0),
                LocalDateTime.of(2026, 2, 21, 12, 0), Status.WAITING, null, null);

        when(bookingService.getBookingByIdAndUser(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

//    @Test
//    @DisplayName("Получение всех бронирований пользователя.")
//    void getAllBookingsByOwner_Successful() throws Exception {
//        UserResponseDtoForBooking user = new UserResponseDtoForBooking(1L);
//        ItemResponseDtoForBooking item = new ItemResponseDtoForBooking(1L, "Item №1");
//
//        List<BookingResponseDto> response = List.of(
//                new BookingResponseDto(1L,
//                        LocalDateTime.of(2026, 2, 20, 12, 0),
//                        LocalDateTime.of(2026, 2, 21, 12, 0), Status.WAITING, user, item),
//                new BookingResponseDto(1L,
//                        LocalDateTime.of(2026, 2, 25, 12, 0),
//                        LocalDateTime.of(2026, 2, 26, 12, 0), Status.WAITING, user, item));
//
//        when(bookingService.getAllBookingsByOwner(1L)).thenReturn(response);
//
//        mockMvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", "1")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].status").value("WAITING"))
//                .andExpect(jsonPath("$[1].id").value(2L))
//                .andExpect(jsonPath("$[1].status").value("WAITING"));
//    }
}
