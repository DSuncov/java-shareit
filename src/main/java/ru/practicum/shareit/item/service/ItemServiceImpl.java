package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.InMemoryItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemStorage inMemoryItemStorage;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        log.info("Обрабатываем запрос на получение списка всех вещей владельца ...");
        List<ItemDto> listOfItemsByOwner = inMemoryItemStorage.getAllItemsByOwner(userId).stream()
                .map(itemMapper::toDto)
                .toList();
        log.info("Запрос обработан успешно. Список вещей отправлен владельцу.");
        return listOfItemsByOwner;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Обрабатываем запрос на получение информации о вещи с id = {} ...", itemId);
        Item item = inMemoryItemStorage.getItemById(itemId);
        log.info("Запрос успешно обработан. Информация о вещи с id = {} отправлена клиенту", itemId);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto createItem(Item item, Long userId) {
        log.info("Обрабатываем запрос на добавление вещи ...");
        Item newItem = inMemoryItemStorage.createItem(item, userId);
        log.info("Запрос успешно обработан. Новая вещь сохранена в БД.");
        return itemMapper.toDto(newItem);
    }

    @Override
    public ItemDto editItem(Long itemId, Item item, Long userId) {
        log.info("Обрабатываем запрос на обновление информации о вещи с id = {} ...", itemId);
        Item editItem = inMemoryItemStorage.editItem(itemId, item, userId);
        log.info("Запрос успешно обработан. Информация о вещи с id = {} обновлена.", itemId);
        return itemMapper.toDto(editItem);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        log.info("Обрабатываем запрос на поиск доступных вещей ...");
        List<ItemDto> listOfItems = inMemoryItemStorage.searchItem(text).stream()
                .map(itemMapper::toDto)
                .toList();
        log.info("Запрос успешно обработан. Список доступных вещей отправлен клиенту.");
        return listOfItems;
    }
}
