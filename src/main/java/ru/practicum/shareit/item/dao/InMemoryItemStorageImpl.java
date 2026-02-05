package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.InMemoryUserStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorageImpl implements InMemoryItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;

    @Override
    public List<Item> getAllItemsByOwner(Long userId) {
        userExistInStorage(userId);
        return items.values().stream()
                .filter(u -> u.getOwnerId().equals(userId))
                .toList();
    }

    @Override
    public Item getItemById(Long itemId) {
        itemExistInStorage(itemId);
        return items.get(itemId);
    }

    @Override
    public Item createItem(Item item, Long userId) {
        userExistInStorage(userId);
        item.setId(generateItemId());
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item editItem(Long itemId, Item item, Long userId) {
        itemExistInStorage(itemId);
        Item editItem = items.get(itemId);

        if (!editItem.getOwnerId().equals(userId)) {
            throw new NotFoundException("Пользователя с id = " + userId + " не является владельцем вещи с id = " + itemId + ".");
        }

        if (item.getName() != null) {
            editItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            editItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            editItem.setAvailable(item.getAvailable());
        }

        return editItem;
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        Pattern pattern = Pattern.compile(text, Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        return items.values().stream()
                .filter(i -> (pattern.matcher(i.getName()).find() || pattern.matcher(i.getDescription()).find()))
                .filter(Item::getAvailable)
                .toList();
    }

    private void itemExistInStorage(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещи с id = " + itemId + " не существует.");
        }
    }

    private void userExistInStorage(Long userId) {
        if (!inMemoryUserStorage.getAllUsers().containsKey(userId)) {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        }
    }

    private Long generateItemId() {
        Long currentMaxId = items.keySet().stream()
                .max(Long::compare)
                .orElse(0L);
        return ++currentMaxId;
    }
}
