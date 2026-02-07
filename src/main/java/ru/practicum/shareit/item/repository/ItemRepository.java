package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i " +
            "FROM Item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.available AND i.owner.id = :userId AND (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<Item> search(String text, Long userId);

    @Query("SELECT i " +
            "FROM Item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :userId")
    List<Item> findAllItemsByOwner(Long userId);

    @Query("SELECT i FROM Item i WHERE i.id = :itemId")
    Optional<Item> findItemForBooking(Long itemId);
}
