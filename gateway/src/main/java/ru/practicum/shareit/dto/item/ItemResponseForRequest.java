package ru.practicum.shareit.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseForRequest {
    Long id;
    String name;
    Long ownerId;
}
