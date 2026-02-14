package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId) " +
            "ORDER BY b.id")
    List<BookingResponseDto> findByBookerIdAsc(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE CURRENT_TIMESTAMP BETWEEN b.start AND b.end AND b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId) " +
            "ORDER BY b.start")
    List<BookingResponseDto> findCurrentBookings(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.end < CURRENT_TIMESTAMP AND b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId) " +
            "ORDER BY b.start")
    List<BookingResponseDto> findPastBookings(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.start > CURRENT_TIMESTAMP AND b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId) " +
            "ORDER BY b.start")
    List<BookingResponseDto> findFutureBookings(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.status = :state AND b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId)")
    List<BookingResponseDto> requiredStatusBookings(Long ownerId, String state);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.id = :bookingId AND b.booker.id = (SELECT u.id FROM User u WHERE u.id = :bookerId)")
    Optional<Booking> findByIdAndUser(Long bookingId, Long bookerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = :authorId AND b.item.id = :itemId AND b.status = 'APPROVED' AND b.end < CURRENT_TIMESTAMP")
    Optional<Booking> findByAuthorAndItem(Long authorId, Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.id = :bookingId AND b.item.id = (SELECT i.id FROM Item i WHERE i.owner.id = :ownerId)")
    Optional<Booking> findByIdAndItemOwner(Long bookingId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.booker.id = :userId")
    List<Booking> findBookingsByUser(Long userId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId")
    List<Booking> findBookingsByItemOwner(Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.item.id = :itemId AND b.item.owner.id = :ownerId AND b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Optional<Booking> findLastBooking(Long itemId, Long ownerId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item " +
            "WHERE b.item.id = :itemId AND b.item.owner.id = :ownerId AND b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start " +
            "LIMIT 1")
    Optional<Booking> findNextBooking(Long itemId, Long ownerId);
}
