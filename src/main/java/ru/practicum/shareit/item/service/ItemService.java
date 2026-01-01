package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItemsByOwner(Long userId);

    ItemDto getItemById(Long itemId);

    ItemDto createItem(Item item, Long userId);

    ItemDto editItem(Long itemId, Item item, Long userId);

    List<ItemDto> searchItem(String text);
}
