package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.response.ItemResponseForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mappers.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRequestMapperTest {

    private final RequestMapper itemRequestMapper = new RequestMapper();

    private final User requestor = new User(1L, "User № 1", "user_1@yandex.ru");
    private final User owner = new User(2L, "User № 3", "user_2@yandex.ru");

    private final Item item = new Item(null, "Item № 1 name", "Item № 1 description", true, owner, null);

    @Test
    @DisplayName("Маппинг из ItemRequest в ItemRequestResponseDto")
    void toDto_ShouldReturnItemRequestResponseDto() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, Instant.now());
        List<Item> items = List.of(item);

        ItemRequestResponseDto dto1 = itemRequestMapper.toDto(itemRequest, items);

        assertNotNull(dto1);
        assertEquals(itemRequest.getId(), dto1.getId());
        assertEquals(itemRequest.getDescription(), dto1.getDescription());
        assertEquals(items.size(), dto1.getItems().size());

        ItemRequestResponseDto dto2 = itemRequestMapper.toDto(itemRequest);
        assertNotNull(dto2);
        assertEquals(itemRequest.getId(), dto2.getId());
        assertEquals(itemRequest.getDescription(), dto2.getDescription());
    }

    @Test
    @DisplayName("Маппинг из ItemRequestCreateDto в Entity")
    void toEntity_ShouldReturnItemRequestResponseDto() {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("description");

        ItemRequest itemRequest = itemRequestMapper.toEntity(dto);

        assertNotNull(itemRequest);
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    @DisplayName("Маппинг из Entity в ItemResponseForRequest")
    void itemToRequest_ShouldReturnItemResponseForRequest() {
        ItemRequest itemRequest = new ItemRequest(1L, "description", requestor, Instant.now());
        item.setRequest(itemRequest);

        ItemResponseForRequest dto = itemRequestMapper.itemToRequest(item);

        assertNotNull(dto);
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getOwner().getId(), dto.getOwnerId());
    }
}
