package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.response.ItemResponseForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Component
public class RequestMapper {

    public ItemRequestResponseDto toDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(itemRequest.getId());
        itemRequestResponseDto.setDescription(itemRequest.getDescription());
        itemRequestResponseDto.setCreated(itemRequest.getCreated());

        List<ItemResponseForRequest> itemsForRequest = items.stream()
                        .map(this::itemToRequest)
                        .toList();

        itemRequestResponseDto.setItems(itemsForRequest);

        return itemRequestResponseDto;
    }

    public ItemRequestResponseDto toDto(ItemRequest itemRequest) {
        ItemRequestResponseDto dto = new ItemRequestResponseDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public ItemRequest toEntity(ItemRequestCreateDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public ItemResponseForRequest itemToRequest(Item item) {
        ItemResponseForRequest dto = new ItemResponseForRequest();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}
