package ru.practicum.shareit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.dto.item.ItemResponseForRequest;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestResponseDto {
    Long id;
    String description;
    Instant created;
    List<ItemResponseForRequest> items;
}
