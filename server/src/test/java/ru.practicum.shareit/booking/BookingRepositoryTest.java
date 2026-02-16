package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;

    private Item item1;
    private Item item2;

    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        owner = new User(null, "User Owner", "user_owner@yandex.ru");
        entityManager.persist(owner);
        entityManager.flush();

        booker = new User(null, "User Booker", "user_booker@yandex.ru");
        entityManager.persist(booker);
        entityManager.flush();

        item1 = new Item(null, "Item № 1 name", "Item № 1 description", true, owner, null);
        entityManager.persist(item1);
        entityManager.flush();

        item2 = new Item(null, "Item № 2 name", "Item № 2 description", false, owner, null);
        entityManager.persist(item2);
        entityManager.flush();

        booking1 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3), item1, booker, Status.WAITING);
        entityManager.persist(booking1);
        entityManager.flush();

        booking2 = new Booking(null, LocalDateTime.now().plusDays(6), LocalDateTime.now().plusDays(10), item2, booker, Status.APPROVED);
        entityManager.persist(booking2);
        entityManager.flush();
    }

    @AfterEach
    void setAfter() {
        testEntityManager.getEntityManager()
                .createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE items RESTART IDENTITY")
                .executeUpdate();

        testEntityManager.getEntityManager()
                .createNativeQuery("TRUNCATE TABLE bookings RESTART IDENTITY")
                .executeUpdate();
    }

    @Test
    @DisplayName("Находим бронирование по id")
    void findById_ShouldReturnBooking() {
        Optional<Booking> booking = bookingRepository.findById(1L);

        assertTrue(booking.isPresent());
        assertEquals(Status.WAITING, booking.get().getStatus());
        assertEquals(2L, booking.get().getBooker().getId());
        assertEquals(1L, booking.get().getItem().getOwner().getId());
    }

    @Test
    @DisplayName("Находим все бронирования")
    void findAll_ShouldReturnAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();

        assertEquals(2, bookings.size());
    }

    @Test
    @DisplayName("Попытка получить текущие бронирования, список должен быть пустым")
    void findCurrentBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findCurrentBookings(1L);

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DisplayName("Попытка получить прошлые бронирования, список должен быть пустым")
    void findPastBookings_ShouldReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findPastBookings(1L);

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DisplayName("Попытка получить предстоящие бронирования, список должен быть равен 2")
    void findPastBookings_ShouldReturnList() {
        List<Booking> bookings = bookingRepository.findFutureBookings(1L);

        assertEquals(2, bookings.size());
    }

    @Test
    @DisplayName("Возвращает бронирование со статусом WAITING")
    void requiredStatusBookings_ShouldReturnBookingWithWaitingStatus() {
        List<Booking> bookings = bookingRepository.requiredStatusBookings(1L, Status.WAITING);

        assertEquals(1, bookings.size());
    }

    @Test
    @DisplayName("Возвращает бронирование со статусом APPROVED")
    void requiredStatusBookings_ShouldReturnBookingWithApprovedStatus() {
        List<Booking> bookings = bookingRepository.requiredStatusBookings(1L, Status.APPROVED);

        assertEquals(1, bookings.size());
    }

    @Test
    @DisplayName("Возвращает бронирование со статусом CANCELLED")
    void requiredStatusBookings_ShouldReturnEmptyListWithCancelledStatus() {
        List<Booking> bookings = bookingRepository.requiredStatusBookings(1L, Status.CANCELED);

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DisplayName("Возвращает бронирование со статусом REJECTED")
    void requiredStatusBookings_ShouldReturnEmptyListWithRejectedStatus() {
        List<Booking> bookings = bookingRepository.requiredStatusBookings(1L, Status.REJECTED);

        assertTrue(bookings.isEmpty());
    }

    @Test
    @DisplayName("Возвращает бронирование по id и booker")
    void findByIdAndUser_ShouldReturnBooking() {
        Optional<Booking> booking = bookingRepository.findByIdAndUser(1L, 2L);

        assertTrue(booking.isPresent());
        assertEquals(Status.WAITING, booking.get().getStatus());
        assertEquals(2L, booking.get().getBooker().getId());
        assertEquals(1L, booking.get().getItem().getOwner().getId());
    }

    @Test
    @DisplayName("Возвращает бронирование по id вещи и booker, должен вернуться, т.к. дата окончания после текущего времени")
    void findByAuthorAndItem_ShouldReturnEmptyListBooking() {
        Optional<Booking> booking = bookingRepository.findByAuthorAndItem(2L, 2L);

        assertTrue(booking.isEmpty());
    }

    @Test
    @DisplayName("Возвращает бронирование по id и владельцу вещи")
    void findByIdAndItemOwner_ShouldReturnBooking() {
        Optional<Booking> booking = bookingRepository.findByIdAndItemOwner(1L, 1L);

        assertTrue(booking.isPresent());
        assertEquals(Status.WAITING, booking.get().getStatus());
        assertEquals(2L, booking.get().getBooker().getId());
        assertEquals(1L, booking.get().getItem().getOwner().getId());
    }

    @Test
    @DisplayName("Находит бронировани по id автора")
    void findBookingsByUser_ShouldReturnBookingsByBooker() {
        List<Booking> bookings = bookingRepository.findBookingsByUser(2L);

        assertEquals(2, bookings.size());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }

    @Test
    @DisplayName("Находит бронировани по id владельца вещи")
    void findBookingsByItemOwner_ShouldReturnBookingsByItemOwner() {
        List<Booking> bookings = bookingRepository.findBookingsByItemOwner(1L);

        assertEquals(2, bookings.size());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }

    @Test
    @DisplayName("Находит последнее бронирование, должно быть пустым")
    void findLastBooking_ShouldReturnEmpty() {
        Optional<Booking> booking = bookingRepository.findLastBooking(1L, 1L);

        assertTrue(booking.isEmpty());
    }

    @Test
    @DisplayName("Находит следующее бронирование")
    void findNextBooking_ShouldReturnEmpty() {
        Optional<Booking> booking = bookingRepository.findNextBooking(1L, 1L);

        assertTrue(booking.isPresent());
        assertEquals(Status.WAITING, booking.get().getStatus());
        assertEquals(2L, booking.get().getBooker().getId());
    }
}
