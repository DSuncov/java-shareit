package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface InMemoryItemStorage {

    List<Item> getAllItemsByOwner(Long userId);

    Item getItemById(Long itemId);

    Item createItem(Item item, Long userId);

    Item editItem(Long itemId, Item item, Long userId);

    List<Item> searchItem(String text);
}
