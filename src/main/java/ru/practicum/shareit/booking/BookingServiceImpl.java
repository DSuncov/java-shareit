package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponseDtoForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponseDtoForBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingByIdAndUser(Long bookingId, Long userId) {
        log.info("Обрабатываем запрос на получение бронирования с id = {} ...", bookingId);
        Booking bookingByUser = bookingRepository.findByIdAndUser(bookingId, userId)
                .orElseGet(() -> bookingRepository.findByIdAndItemOwner(bookingId, userId)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не является владельцем забронированной вещи или автором бронирования.")));

        Item item = itemRepository.findItemForBooking(bookingByUser.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена."));

        return bookingMapper.toDto(bookingByUser, new UserResponseDtoForBooking(userId), new ItemResponseDtoForBooking(item.getId(), item.getName()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsByOwner(Long ownerId) {
        log.info("Обрабатываем запрос на получение списка бронирований ...");
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не существует."));
        List<Booking> bookings = bookingRepository.findBookingsByItemOwner(ownerId);
        log.info("Список бронирований получен.");
        return convertBookingsFromEntityToDto(bookings);
    }

    @Override
    @Transactional
    public BookingResponseDto createBooking(Long bookerId, BookingCreateDto bookingCreateDto) {
        log.info("Обрабатываем запрос на создание бронирования ...");
        User user = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не существует."));
        Item item = itemRepository.findById(bookingCreateDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещи с id = " + bookingCreateDto.getItemId() + " не существует."));

        if (!item.getAvailable()) {
            throw new NotAvailableException("Бронирование вещи с id + " + bookingCreateDto.getItemId() + " невозможно, так как вещь недоступна.");
        }

        if (bookingCreateDto.getStart().isAfter(bookingCreateDto.getEnd()) || bookingCreateDto.getStart().equals(bookingCreateDto.getEnd())) {
            throw new ValidationException("Дата создания не может быть равна или быть позже даты окончания бронирования.");
        }

        Booking booking = bookingMapper.toEntity(bookingCreateDto, item, user);
        booking.setStatus(Status.WAITING);
        Booking createdBooking = bookingRepository.save(booking);
        log.info("Запрос успешно обработан. Новое бронирование сохранено в БД.");
        return bookingMapper.toDto(createdBooking, new UserResponseDtoForBooking(user.getId()), new ItemResponseDtoForBooking(item.getId(), item.getName()));
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Long ownerId, Boolean bookingStatus) {
        log.info("Обрабатываем запрос на подтверждение бронирования ...");
        userRepository.findById(ownerId).orElseThrow(() -> new AccessException("Пользователя с id = " + ownerId + " не существует. Подтвердить бронирование невозможно."));
        Optional<Booking> approvedBooking = bookingRepository.findById(bookingId);
        Booking booking = approvedBooking.orElseThrow(() -> new NotFoundException("Бронирования с id = " + bookingId + " не существует."));

        Optional<Item> item = itemRepository.findById(booking.getItem().getId());

        if (item.isPresent()) {
            if (!item.get().getOwner().getId().equals(ownerId)) {
                throw new AccessException("Пользователь с id = " + ownerId + " не является владельцем вещи с id = " + bookingId);
            }
        }

        if (bookingStatus) {
            booking.setStatus(Status.APPROVED);
            log.info("Бронирование подтверждено ...");
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Бронирование отклонено ...");
        }

        log.info("Сохраняем бронирование после обновления статуса ...");
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking, new UserResponseDtoForBooking(booking.getBooker().getId()), new ItemResponseDtoForBooking(item.get().getId(), item.get().getName()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsByOwnerAndState(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не существует."));

        switch (state) {
            case "ALL" -> {
                return bookingRepository.findByBookerIdAsc(ownerId);
            }
            case "CURRENT" -> {
                return bookingRepository.findCurrentBookings(ownerId);
            }
            case "PAST" -> {
                return bookingRepository.findPastBookings(ownerId);
            }
            case "FUTURE" -> {
                return bookingRepository.findFutureBookings(ownerId);
            }
            case "WAITING", "REJECTED" -> {
                return bookingRepository.requiredStatusBookings(ownerId, state);
            }
            default -> throw new NotFoundException("В запрос передан неверный параметр state");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookingsByUser(Long userId) {
        log.info("Обрабатываем запрос на получение списка бронирований пользователя с id = {} ...", userId);
        List<Booking> bookings = bookingRepository.findBookingsByUser(userId);
        log.info("Список бронирований получен.");
        return convertBookingsFromEntityToDto(bookings);
    }

    private List<BookingResponseDto> convertBookingsFromEntityToDto(List<Booking> bookings) {
        return bookings.stream()
                .map(b -> bookingMapper.toDto(b,
                        new UserResponseDtoForBooking(b.getBooker().getId()),
                        new ItemResponseDtoForBooking(b.getItem().getId(), b.getItem().getName())))
                .toList();
    }
}
