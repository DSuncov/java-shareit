package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT new ru.practicum.shareit.item.dto.ItemResponseDto(" +
            "i.id, i.name, i.description, i.available)" +
            " FROM Item i WHERE i.available AND i.owner.id = :userId AND (UPPER(i.name) LIKE UPPER(CONCAT('%', :text, '%')) OR UPPER(i.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<ItemResponseDto> search(String text, Long userId);

    @Query("SELECT new ru.practicum.shareit.item.dto.ItemResponseDto(" +
            "i.id, i.name, i.description, i.available) " +
            "FROM Item i WHERE i.owner.id = :userId")
    List<ItemResponseDto> findAllItemsByOwner(Long userId);

    @Query("SELECT i FROM Item i WHERE i.id = :itemId")
    Optional<Item> findItemForBooking(Long itemId);
}
