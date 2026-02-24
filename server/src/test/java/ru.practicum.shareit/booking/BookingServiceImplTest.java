package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.mappers.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.NotAvailableException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private ItemRequest itemRequest;
    private User owner;
    private Item item;
    private User booker;
    private Booking booking;

    @BeforeEach
    void setUp() {
        itemRequest = new ItemRequest(1L, "Request description", booker, Instant.now());
        owner = new User(1L, "User №1", "user_1@yandex.ru");
        item = new Item(1L, "Item № 1", "description", true, owner, itemRequest);
        booker = new User(2L, "User №2", "user_2@yandex.ru");
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, Status.WAITING);
    }

    @Test
    @DisplayName("Получение существующего бронирования по id")
    void getBookingByIdAndUser_ShouldReturnBooking() {
        BookingResponseDto expected =
                new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), Status.WAITING,
                        new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        when(bookingRepository.findByIdAndUser(anyLong(), anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findItemForBooking(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(expected);

        BookingResponseDto result = bookingService.getBookingByIdAndUser(1L, 2L);
        verify(bookingRepository).findByIdAndUser(anyLong(), anyLong());
        verify(itemRepository).findItemForBooking(anyLong());
        verify(bookingMapper).toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Создание бронирования")
    void createBooking_ShouldReturnNewBooking() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(10), 1L);
        BookingResponseDto expected =
                new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), Status.WAITING,
                        new UserResponseDtoForBooking(booking.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.toEntity(bookingCreateDto, item, booker)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(expected);

        BookingResponseDto result = bookingService.createBooking(2L, bookingCreateDto);
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingMapper).toEntity(bookingCreateDto, item, booker);
        verify(bookingRepository).save(booking);
        verify(bookingMapper).toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void approveBooking_ShouldReturnApprovedBooking() {
        Booking approved = new Booking(1L, booking.getStart(), booking.getEnd(), item, booker, Status.APPROVED);

        BookingResponseDto expected =
                new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), Status.APPROVED,
                        new UserResponseDtoForBooking(booking.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(approved)).thenReturn(approved);
        when(bookingMapper.toDto(approved, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(expected);

        BookingResponseDto result = bookingService.approveBooking(1L, 1L, true);
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).save(approved);
        verify(bookingMapper).toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Отклонение бронирования")
    void approveBooking_ShouldReturnRejectedBooking() {
        Booking rejected = new Booking(1L, booking.getStart(), booking.getEnd(), item, booker, Status.REJECTED);

        BookingResponseDto expected =
                new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), Status.REJECTED,
                        new UserResponseDtoForBooking(booking.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(rejected)).thenReturn(rejected);
        when(bookingMapper.toDto(rejected, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(expected);

        BookingResponseDto result = bookingService.approveBooking(1L, 1L, false);
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).save(rejected);
        verify(bookingMapper).toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("озвращает список бронирований по состоянию бронирования")
    void getAllBookingsByOwnerAndState_ShouldReturnBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        // CURRENT
        BookingResponseDto dto =
                new BookingResponseDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(10), Status.APPROVED,
                        new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        List<BookingResponseDto> expected = new ArrayList<>();
        expected.add(dto);

        when(bookingRepository.findCurrentBookings(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto);

        List<BookingResponseDto> resultCurrent = bookingService.getAllBookingsByOwnerAndState(1L, "CURRENT");
        assertEquals(expected, resultCurrent);

        // ALL
        when(bookingRepository.findByBookerIdAsc(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto);

        List<BookingResponseDto> resultAll = bookingService.getAllBookingsByOwnerAndState(1L, "ALL");
        assertEquals(expected, resultAll);

        // WAITING
        when(bookingRepository.requiredStatusBookings(anyLong(), eq(Status.WAITING))).thenReturn(bookings);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto);

        List<BookingResponseDto> resultWaiting = bookingService.getAllBookingsByOwnerAndState(1L, "WAITING");
        assertEquals(expected, resultWaiting);

        // PAST
        bookings.getFirst().setStart(LocalDateTime.now().minusDays(5));
        bookings.getFirst().setEnd(LocalDateTime.now().minusDays(2));

        dto.setStart(LocalDateTime.now().minusDays(5));
        dto.setEnd(LocalDateTime.now().minusDays(2));

        when(bookingRepository.findPastBookings(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto);

        List<BookingResponseDto> resultPast = bookingService.getAllBookingsByOwnerAndState(1L, "PAST");
        assertEquals(expected, resultPast);

        // FUTURE

        bookings.getFirst().setStart(LocalDateTime.now().plusDays(5));

        dto.setStart(LocalDateTime.now().plusDays(5));

        when(bookingRepository.findFutureBookings(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto);

        List<BookingResponseDto> resultFuture = bookingService.getAllBookingsByOwnerAndState(1L, "FUTURE");
        assertEquals(expected, resultFuture);

        assertThrows(NotFoundException.class, () -> bookingService.getAllBookingsByOwnerAndState(1L, "UNKNOWN"));
    }

    @Test
    @DisplayName("Попытка получить недоступную для бронирования вещь либо с не валидными датами создания и окончания бронирования")
    void createBooking_ShouldReturnThrowException() {
        BookingCreateDto bookingCreateDto = new BookingCreateDto(LocalDateTime.now(), LocalDateTime.now().plusDays(10), 1L);
        item.setAvailable(false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.createBooking(1L, bookingCreateDto));

        bookingCreateDto.setEnd(LocalDateTime.now().minusDays(5));
        item.setAvailable(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, bookingCreateDto));
    }

    @Test
    @DisplayName("Получение всех бронирований для владельца вещи и случайного пользователя")
    void getAllBookingsByOwnerOrUser_ShouldReturnAllBookingsByOwnerOrUser() {
        Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5), item, booker, Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), item, booker, Status.WAITING);

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);

        List<BookingResponseDto> expected = new ArrayList<>();

        BookingResponseDto dto1 =
                new BookingResponseDto(1L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(5), Status.WAITING,
                        new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        BookingResponseDto dto2 =
                new BookingResponseDto(2L, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), Status.WAITING,
                        new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));

        expected.add(dto1);
        expected.add(dto2);

        // Для владельца вещи
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findBookingsByItemOwner(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking1, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto1);
        when(bookingMapper.toDto(booking2, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto2);

        List<BookingResponseDto> resultByOwner = bookingService.getAllBookingsByOwner(1L);
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findBookingsByItemOwner(anyLong());

        assertEquals(expected, resultByOwner);

        // Для случайного пользователя
        when(bookingRepository.findBookingsByUser(anyLong())).thenReturn(bookings);
        when(bookingMapper.toDto(booking1, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto1);
        when(bookingMapper.toDto(booking2, new UserResponseDtoForBooking(booker.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()))).thenReturn(dto2);

        List<BookingResponseDto> resultByUser = bookingService.getAllBookingsByUser(2L);
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).findBookingsByItemOwner(anyLong());

        assertEquals(expected, resultByUser);
    }
}
