package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.create.ItemCreateDto;
import ru.practicum.shareit.item.mappers.ItemMapper;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDtoForComment;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemMapperTest {

    private final ItemMapper itemMapper = new ItemMapper();

    @Test
    @DisplayName("Маппинг из Entity в DTO")
    void toDto_ShouldReturnItemResponseDto() {
        Item item = new Item(null, "Item № 1 name", "Item № 1 description", true, null, null);

        ItemResponseDto dto = itemMapper.toDto(item);

        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    @DisplayName("Маппинг из Entity в DTO")
    void toDto_ShouldReturnItemResponseDtoWithComments() {
        Item item = new Item(null, "Item № 1 name", "Item № 1 description", true, null, null);

        ItemResponseDtoForComment dto = itemMapper.toDtoWithComments(item);

        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getAvailable(), dto.getAvailable());
    }

    @Test
    @DisplayName("Маппинг из DTO в Entity")
    void toEntity_ShouldReturnItem() {
        ItemCreateDto dto = new ItemCreateDto("Item № 1 name", "Item № 1 description", true, null);

        Item item = itemMapper.toEntity(dto);

        assertNotNull(item);
        assertEquals(dto.getName(), item.getName());
        assertEquals(dto.getDescription(), item.getDescription());
        assertEquals(dto.getAvailable(), item.getAvailable());
    }
}
